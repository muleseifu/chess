package Chess.model.game;
 
import Chess.ai.Bot;
import Chess.model.board.Board;
import Chess.model.board.Cell;
import Chess.model.pieces.Pawn;
import Chess.model.pieces.Piece;
//import Chess.util.Constants.GameMode;
//import Chess.util.Constants.GameStatus;
import Chess.util.MoveHistory;
//import Chess.util.MoveRecord;
 
import java.util.List;
 
public class Game {
 
    private Board board;
    private int currentTurn;       // 0 = White, 1 = Black
    private GameMode gameMode;
    private Bot bot;
    private MoveHistory history;
    private GameStatus status;
 
    // Tracks the selected cell between clicks
    private Cell selectedCell;
 
    public Game() {
        this.history = new MoveHistory();
        this.status = GameStatus.ONGOING;
        this.selectedCell = null;
    }
 
    public void startGame(GameMode mode) {
        this.gameMode = mode;
        this.currentTurn = 0; // White always starts
        this.status = GameStatus.ONGOING;
        this.selectedCell = null;
        history.clear();
 
        board = new Board();
        board.initBoard();
        board.placePieces();
 
        if (mode == GameMode.PVB) {
            // Bot plays Black by default (color = 1)
            bot = new Bot(1);
        } else {
            bot = null;
        }
    }
 
    public void handleCellClick(Cell cell) {
        if (status == GameStatus.CHECKMATE
                || status == GameStatus.STALEMATE
                || status == GameStatus.DRAW
                || status == GameStatus.RESIGNED) {
            return;
        }
 
        // If bot's turn in PVB, ignore human clicks
        if (gameMode == GameMode.PVB && currentTurn == 1) {
            return;
        }
 
        if (selectedCell == null) {
            // Nothing selected yet — try to select a piece
            if (!cell.isEmpty() && cell.getPiece().getColor() == currentTurn) {
                selectedCell = cell;
                cell.select();
                List<Cell> legalMoves = board.getLegalMoves(cell);
                for (Cell target : legalMoves) {
                    target.setPossibleDestination();
                }
            }
        } else {
            // Something already selected
            if (cell == selectedCell) {
                // Deselect same cell
                clearSelection();
                return;
            }
 
            if (!cell.isEmpty() && cell.getPiece().getColor() == currentTurn) {
                // Clicked own piece — switch selection
                clearSelection();
                selectedCell = cell;
                cell.select();
                List<Cell> legalMoves = board.getLegalMoves(cell);
                for (Cell target : legalMoves) {
                    target.setPossibleDestination();
                }
                return;
            }
 
            // Try to move to this cell
            List<Cell> legalMoves = board.getLegalMoves(selectedCell);
            if (legalMoves.contains(cell)) {
                makeMove(selectedCell, cell);
                clearSelection();
            } else {
                clearSelection();
            }
        }
    }
 
    public void makeMove(Cell from, Cell to) {
        Piece movedPiece = from.getPiece();
        Piece capturedPiece = to.getPiece();
 
        boolean isCastle = false;
        boolean isEnPassant = false;
        boolean isPromotion = false;
 
        // Detect special moves before executing
        if (movedPiece instanceof chess.model.pieces.King) {
            int colDiff = Math.abs(to.getY() - from.getY());
            if (colDiff == 2) isCastle = true;
        }
        if (movedPiece instanceof Pawn) {
            int colDiff = Math.abs(to.getY() - from.getY());
            if (colDiff == 1 && to.isEmpty()) isEnPassant = true;
 
            int promotionRow = (movedPiece.getColor() == 0) ? 0 : 7;
            if (to.getX() == promotionRow) isPromotion = true;
        }
 
        // Build notation before move
        String notation = buildNotation(from, to, movedPiece, capturedPiece, isCastle, isEnPassant, isPromotion);
 
        // Execute move
        board.movePiece(from, to);
 
        // Handle pawn promotion
        if (isPromotion) {
            // Notify listeners to show promotion dialog — GUI handles piece replacement
            // In PVB when bot promotes, auto-queen handled in Bot
            board.notifyListeners();
        }
 
        // Record the move
        MoveRecord record = new MoveRecord(from, to, movedPiece, capturedPiece,
                isCastle, isEnPassant, isPromotion, notation);
        history.push(record);
 
        // Evaluate status for next player
        updateStatus();
 
        // Notify GUI
        board.notifyListeners();
 
        // Switch turn (bot trigger happens inside switchTurn)
        switchTurn();
    }
 
    public void switchTurn() {
        currentTurn = (currentTurn == 0) ? 1 : 0;
 
        // If PVB and it is bot's turn, trigger bot move
        if (gameMode == GameMode.PVB && currentTurn == bot.getColor()) {
            Cell[] botMove = bot.getBestMove(board);
            if (botMove != null) {
                makeMove(botMove[0], botMove[1]);
            }
        }
    }
 
    public void updateStatus() {
        int nextTurn = (currentTurn == 0) ? 1 : 0;
 
        if (board.isCheckmate(nextTurn)) {
            status = GameStatus.CHECKMATE;
        } else if (board.isStalemate(nextTurn)) {
            status = GameStatus.STALEMATE;
        } else if (board.isInCheck(nextTurn)) {
            status = GameStatus.CHECK;
        } else {
            status = GameStatus.ONGOING;
        }
    }
 
    public void undoMove() {
        if (history.isEmpty()) return;
 
        MoveRecord last = history.pop();
        if (last == null) return;
 
        Cell from = last.from;
        Cell to = last.to;
        Piece movedPiece = last.movedPiece;
        Piece capturedPiece = last.capturedPiece;
 
        // Restore moved piece to original cell
        to.removePiece();
        from.setPiece(movedPiece);
 
        // Restore captured piece
        if (capturedPiece != null && !last.isEnPassant) {
            capturedPiece.setAvailable(true);
            to.setPiece(capturedPiece);
            // Re-add to piece lists
            if (capturedPiece.getColor() == 0) {
                board.getWhitePieces().add(capturedPiece);
            } else {
                board.getBlackPieces().add(capturedPiece);
            }
        }
 
        // Restore hasMoved if this was the piece's first move
        // (Simplified: full undo of hasMoved requires tracking prior state)
 
        // Switch turn back
        currentTurn = (currentTurn == 0) ? 1 : 0;
 
        updateStatus();
        board.notifyListeners();
    }
 
    public void resignGame(int color) {
        status = GameStatus.RESIGNED;
        board.notifyListeners();
    }
 
    public void offerDraw() {
        // In a real implementation this would prompt the opponent
        // Here we simply mark the status as DRAW (to be confirmed externally)
        status = GameStatus.DRAW;
        board.notifyListeners();
    }
 
    public GameStatus getStatus() {
        return status;
    }
 
    public int getCurrentTurn() {
        return currentTurn;
    }
 
    public void restartGame() {
        startGame(gameMode);
        board.notifyListeners();
    }
 
    public Board getBoard() {
        return board;
    }
 
    public MoveHistory getHistory() {
        return history;
    }
 
    // ---- Private helpers ----
 
    private void clearSelection() {
        if (selectedCell != null) {
            selectedCell.deselect();
        }
        board.clearHighlights();
        selectedCell = null;
    }
 
    private String buildNotation(Cell from, Cell to, Piece movedPiece,
                                  Piece capturedPiece, boolean isCastle,
                                  boolean isEnPassant, boolean isPromotion) {
        if (isCastle) {
            return (to.getY() > from.getY()) ? "O-O" : "O-O-O";
        }
 
        StringBuilder sb = new StringBuilder();
        String pieceSymbol = getPieceSymbol(movedPiece);
        sb.append(pieceSymbol);
        if (capturedPiece != null || isEnPassant) sb.append("x");
        sb.append((char) ('a' + to.getY()));
        sb.append(8 - to.getX());
        if (isPromotion) sb.append("=Q"); // default queen promotion notation
        return sb.toString();
    }
 
    private String getPieceSymbol(Piece piece) {
        String name = piece.getClass().getSimpleName();
        switch (name) {
            case "King":   return "K";
            case "Queen":  return "Q";
            case "Rook":   return "R";
            case "Bishop": return "B";
            case "Knight": return "N";
            default:       return ""; // Pawn has no symbol
        }
    }
}
