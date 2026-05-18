package Chess.model.board;
import javax.swing.*;
import Chess.model.pieces.Piece;
public class Cell {
    private boolean isPossibleDestination;
    private JLabel content;
    private Piece piece;
    private boolean isSelected;
    private boolean isCheck;
    private int x,y;
    //private Color baseColor;
    public void setPiece( Piece piece){
        this.piece = piece;
        updateSprite();

    }
     public void select(){
        this.isSelected = true;

    }
     public void deselect(){
        this.isSelected = false;

    }
     public boolean  isSelected(){
        return isSelected;

    }
     public void removePiece(){
        this.piece = null;

    }
    public Piece getPiece(){
        return piece;
    }
    public void setPossibleDestination(){
        this.isPossibleDestination = true;
        
    }
    public void removePossibleDestination(){
        this.isPossibleDestination = false;
        
    }
    public boolean isPossibleDestination(){
        return isPossibleDestination;
    }
    public void setCheck(){
        this.isCheck = true;
        
    }
    public void removeCheck(){
        this.isCheck = false;
    }
    public boolean isCheck(){
        return isCheck;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public boolean isEmpty(){
        return piece == null;
    }
    public boolean isEnemy(int color){
        if(piece == null) return false;
        return piece.getColor() != color;
        

    }
    public void updateSprite(){
        
        

    }
    public boolean isInCheck(int color) {
        // Find the king of the given color
        Piece king = null;
        Cell kingCell = null;
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = getCell(i, j);
                Piece piece = cell.getPiece();
                
                // Find the king of the specified color
                if (piece != null && piece.getColor() == color && piece.getId().contains("King")) {
                    king = piece;
                    kingCell = cell;
                    break;
                }
            }
            if (king != null) break;
        }
        
        // If no king found, return false (shouldn't happen in normal game)
        if (kingCell == null) {
            return false;
        }
        
        // Check if any opponent piece can attack the king
        int opponentColor = 1 - color; // 0 -> 1, 1 -> 0
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell cell = getCell(i, j);
                Piece piece = cell.getPiece();
                
                // Check only opponent pieces that are still available
                if (piece != null && piece.getColor() == opponentColor && piece.isAvailable()) {
                    List<Cell> attackCells = piece.move(cell, this);
                    
                    // If the king's cell is in the list of cells this piece can attack
                    if (attackCells != null && attackCells.contains(kingCell)) {
                        return true; // King is under attack
                    }
                }
            }
        }
        
        return false; // No opponent piece can attack the king
    }
}