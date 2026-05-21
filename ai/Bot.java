package Chess.ai;
import Chess.model.board.*;
import Chess.model.pieces.*;
 
import java.util.ArrayList;
import java.util.List;
 
public class Bot {
 
    private int color;
    private int depth;
 
    public Bot(int color) {
        this.color = color;
        this.depth = 3; // default medium depth
    }
 
    // A — Entry point: returns best move as {fromCell, toCell}
    public Cell[] getBestMove(Board board) {
        List<Cell[]> allMoves = generateAllMoves(board, color);
        if (allMoves.isEmpty()) return null;
 
        Cell[] bestMove = null;
        int bestScore = Integer.MIN_VALUE;
 
        for (Cell[] move : allMoves) {
            Board cloned = board.cloneBoard();
            Cell cloneFrom = cloned.getCell(move[0].getX(), move[0].getY());
            Cell cloneTo   = cloned.getCell(move[1].getX(), move[1].getY());
 
            applyMove(cloned, cloneFrom, cloneTo);
 
            int score = minimax(cloned, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
 
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }
 
    // B — Recursive minimax with alpha-beta pruning
    private int minimax(Board board, int currentDepth, int alpha, int beta, boolean maximizing) {
        if (currentDepth == 0 || isTerminal(board)) {
            return evaluateBoard(board);
        }
 
        int playerColor = maximizing ? color : (color == 0 ? 1 : 0);
        List<Cell[]> moves = generateAllMoves(board, playerColor);
 
        if (moves.isEmpty()) {
            return evaluateBoard(board);
        }
 
        if (maximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Cell[] move : moves) {
                Board cloned = board.cloneBoard();
                Cell cloneFrom = cloned.getCell(move[0].getX(), move[0].getY());
                Cell cloneTo   = cloned.getCell(move[1].getX(), move[1].getY());
                applyMove(cloned, cloneFrom, cloneTo);
                int eval = minimax(cloned, currentDepth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break; // beta cutoff
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Cell[] move : moves) {
                Board cloned = board.cloneBoard();
                Cell cloneFrom = cloned.getCell(move[0].getX(), move[0].getY());
                Cell cloneTo   = cloned.getCell(move[1].getX(), move[1].getY());
                applyMove(cloned, cloneFrom, cloneTo);
                int eval = minimax(cloned, currentDepth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) break; // alpha cutoff
            }
            return minEval;
        }
    }
 
    // C — Score board: material + position tables + mobility
    private int evaluateBoard(Board board) {
        int score = 0;
 
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = board.getCell(row, col);
                if (!cell.isEmpty()) {
                    Piece piece = cell.getPiece();
                    int value = getPieceValue(piece);
                    int positionalBonus = getPositionalBonus(piece, row, col);
 
                    if (piece.getColor() == color) {
                        score += value + positionalBonus;
                    } else {
                        score -= value + positionalBonus;
                    }
                }
            }
        }
 
        // Mobility bonus
        int botMobility = generateAllMoves(board, color).size();
        int opponentColor = (color == 0) ? 1 : 0;
        int opponentMobility = generateAllMoves(board, opponentColor).size();
        score += (botMobility - opponentMobility) * 10;
 
        return score;
    }
 
    // D — Generate all legal moves for given colour
    public List<Cell[]> generateAllMoves(Board board, int playerColor) {
        List<Cell[]> allMoves = new ArrayList<>();
 
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = board.getCell(row, col);
                if (!cell.isEmpty() && cell.getPiece().getColor() == playerColor) {
                    List<Cell> legalTargets = board.getLegalMoves(cell);
                    for (Cell target : legalTargets) {
                        allMoves.add(new Cell[]{cell, target});
                    }
                }
            }
        }
        return allMoves;
    }
 
    // D — Adjust difficulty (sets search depth)
    public void setDepth(int depth) {
        this.depth = depth;
    }
 
    public int getColor() {
        return color;
    }
 
    // ---- Private helpers ----
 
    private void applyMove(Board board, Cell from, Cell to) {
        Piece piece = from.getPiece();
        if (piece == null) return;
 
        // Handle captures
        if (!to.isEmpty()) {
            to.getPiece().setAvailable(false);
        }
 
        // Execute move
        from.removePiece();
        to.setPiece(piece);
        piece.setMoved();
 
        // King coordinate update
        if (piece instanceof King) {
            ((King) piece).x = to.getX();
            ((King) piece).y = to.getY();
        }
 
        // Auto-promote pawn to Queen
        if (piece instanceof Pawn) {
            int promotionRow = (piece.getColor() == 0) ? 0 : 7;
            if (to.getX() == promotionRow) {
                Queen queen = new Queen(piece.getColor());
                queen.SetId(piece.getId());
                queen.setMoved();
                to.removePiece();
                to.setPiece(queen);
            }
        }
    }
 
    private boolean isTerminal(Board board) {
        int opponentColor = (color == 0) ? 1 : 0;
        return board.isCheckmate(color)
                || board.isCheckmate(opponentColor)
                || board.isStalemate(color)
                || board.isStalemate(opponentColor);
    }
 
    private int getPieceValue(Piece piece) {
        if (piece instanceof Pawn)   return 100;
        if (piece instanceof Knight) return 320;
        if (piece instanceof Bishop) return 330;
        if (piece instanceof Rook)   return 500;
        if (piece instanceof Queen)  return 900;
        if (piece instanceof King)   return 20000;
        return 0;
    }
 
    private int getPositionalBonus(Piece piece, int row, int col) {
        // Piece-square tables (from white's perspective, flip for black)
        int r = (piece.getColor() == 0) ? row : (7 - row);
        int c = col;
 
        if (piece instanceof Pawn) {
            int[][] pawnTable = {
                { 0,  0,  0,  0,  0,  0,  0,  0},
                {50, 50, 50, 50, 50, 50, 50, 50},
                {10, 10, 20, 30, 30, 20, 10, 10},
                { 5,  5, 10, 25, 25, 10,  5,  5},
                { 0,  0,  0, 20, 20,  0,  0,  0},
                { 5, -5,-10,  0,  0,-10, -5,  5},
                { 5, 10, 10,-20,-20, 10, 10,  5},
                { 0,  0,  0,  0,  0,  0,  0,  0}
            };
            return pawnTable[r][c];
        }
 
        if (piece instanceof Knight) {
            int[][] knightTable = {
                {-50,-40,-30,-30,-30,-30,-40,-50},
                {-40,-20,  0,  0,  0,  0,-20,-40},
                {-30,  0, 10, 15, 15, 10,  0,-30},
                {-30,  5, 15, 20, 20, 15,  5,-30},
                {-30,  0, 15, 20, 20, 15,  0,-30},
                {-30,  5, 10, 15, 15, 10,  5,-30},
                {-40,-20,  0,  5,  5,  0,-20,-40},
                {-50,-40,-30,-30,-30,-30,-40,-50}
            };
            return knightTable[r][c];
        }
 
        if (piece instanceof Bishop) {
            int[][] bishopTable = {
                {-20,-10,-10,-10,-10,-10,-10,-20},
                {-10,  0,  0,  0,  0,  0,  0,-10},
                {-10,  0,  5, 10, 10,  5,  0,-10},
                {-10,  5,  5, 10, 10,  5,  5,-10},
                {-10,  0, 10, 10, 10, 10,  0,-10},
                {-10, 10, 10, 10, 10, 10, 10,-10},
                {-10,  5,  0,  0,  0,  0,  5,-10},
                {-20,-10,-10,-10,-10,-10,-10,-20}
            };
            return bishopTable[r][c];
        }
 
        if (piece instanceof Rook) {
            int[][] rookTable = {
                { 0,  0,  0,  0,  0,  0,  0,  0},
                { 5, 10, 10, 10, 10, 10, 10,  5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                {-5,  0,  0,  0,  0,  0,  0, -5},
                { 0,  0,  0,  5,  5,  0,  0,  0}
            };
            return rookTable[r][c];
        }
 
        if (piece instanceof Queen) {
            int[][] queenTable = {
                {-20,-10,-10, -5, -5,-10,-10,-20},
                {-10,  0,  0,  0,  0,  0,  0,-10},
                {-10,  0,  5,  5,  5,  5,  0,-10},
                { -5,  0,  5,  5,  5,  5,  0, -5},
                {  0,  0,  5,  5,  5,  5,  0, -5},
                {-10,  5,  5,  5,  5,  5,  0,-10},
                {-10,  0,  5,  0,  0,  0,  0,-10},
                {-20,-10,-10, -5, -5,-10,-10,-20}
            };
            return queenTable[r][c];
        }
 
        if (piece instanceof King) {
            int[][] kingMiddleTable = {
                {-30,-40,-40,-50,-50,-40,-40,-30},
                {-30,-40,-40,-50,-50,-40,-40,-30},
                {-30,-40,-40,-50,-50,-40,-40,-30},
                {-30,-40,-40,-50,-50,-40,-40,-30},
                {-20,-30,-30,-40,-40,-30,-30,-20},
                {-10,-20,-20,-20,-20,-20,-20,-10},
                { 20, 20,  0,  0,  0,  0, 20, 20},
                { 20, 30, 10,  0,  0, 10, 30, 20}
            };
            return kingMiddleTable[r][c];
        }
 
        return 0;
    }
}
