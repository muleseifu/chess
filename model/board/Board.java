
import java.util.List;
public class Board{
    private Cell[][] cells= new  Cell[8][8];
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;
    
   // private King whiteKing;
    //private King blackKing;
   // private  List<BoardListener> listeners;
   public void initBoard(){
   }
   public void placePieces(){
   }
   public Cell getCell(int x,int y){
       return cells[x][y];
   }
   public void movePiece(Cell from,Cell to){
      if(from.getPiece() != null && to.isPossibleDestination()){
          to.setPiece(from.getPiece());
          from.removePiece();
          clearHighlights();
          notifyListeners();
      }

   }
   public void getLegalMoves(Cell cell){
       // dont forget to se the return type to List<Cell>
   }
   public boolean isInCheck(int color){
       return false;
       // return type should be boolean
   }
    public boolean isCheckmate(int color){
        return false;
    }
    public boolean isStalemate(int color) {
    // Check if the player is NOT in check
    if (isInCheck(color)) {
        return false;  // Can't be stalemate if in check
    }
    
    // Check if the player has any legal moves
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            Cell cell = getCell(i, j);
            pieces piece = cell.getPiece();
            
            // Check only pieces of the current player
            if (piece != null && piece.getColor() == color) {
                // Get all legal moves for this piece
                List<Cell> legalMoves = getLegalMoves(cell);
                
                if (legalMoves != null && !legalMoves.isEmpty()) {
                    return false;  // Found at least one legal move
                }
            }
        }
    }
    
    // No legal moves found and not in check = stalemate
    return true;
    }
    public Board clone(){
        return this;
    }
    public void clearHighlights(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                cells[i][j].removePossibleDestination();
                cells[i][j].removeCheck();
            }
        }
    }   
    public void notifyListeners(){
        // for(BoardListener listener:listeners){
        //     listener.boardChanged();
        // }
    }
    /*
    public void addListener(BoardListener listener){
        // listeners.add(listener);
    }
     public void removeListener(BoardListener listener){
        // listeners.remove(listener);
    }
        public void perfromCastle(King king,Rook rook){
            // implement castle logic here
        }
            public void performEnPassant(Pawn pawn,Cell target){
                // implement en passant logic here
            }
             public void performPromotion(Pawn pawn,Piece newPiece){
                // implement promotion logic here
            }
     */
    
    // return type should be boolean



}
