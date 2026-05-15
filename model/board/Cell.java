package Chess.model.board;
import javax.swing.*;
import Chess.model.pieces.Piece;
import 
public class Cell {
    private boolean isPossibleDestination;
    private JLabel content;
    private Piece piece;
    private boolean isSelected;
    private boolean isCheck;
    private int x,y;
    //private Color baseColor;
    public void setPiece(){

    }
     public void select(){

    }
     public void deselect(){

    }
     public void isSelected(){

    }
     public void removePiece(){

    }
    public Piece getPiece(){
        return piece;
    }
    public void setPossibleDestination(){
        
    }
    public void removePossibleDestination(){
        
    }
    public boolean isPossibleDestination(){
        return isPossibleDestination;
    }
    public void setCheck(){
        
    }
    public void removeCheck(){
        
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
        

    }
    public void updateSprite(){

    }
    


}