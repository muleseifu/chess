package Chess.model.board;
import Chess.model.pieces.*;
import javax.swing.*;
import java.awt.*;
 
public class Cell {
 
    private boolean isPossibleDestination;
    private JLabel content;
    private Piece piece;
    private boolean isSelected;
    private boolean isCheck;
    private int x;
    private int y;
    private Color baseColor;
 
    public Cell(int x, int y, Color baseColor) {
        this.x = x;
        this.y = y;
        this.baseColor = baseColor;
        this.piece = null;
        this.isSelected = false;
        this.isCheck = false;
        this.isPossibleDestination = false;
        this.content = new JLabel();
        this.content.setHorizontalAlignment(SwingConstants.CENTER);
        this.content.setVerticalAlignment(SwingConstants.CENTER);
    }
 
    public void setPiece(Piece piece) {
        this.piece = piece;
        updateSprite();
    }
 
    public void removePiece() {
        this.piece = null;
        updateSprite();
    }
 
    public Piece getPiece() {
        return piece;
    }
 
    public void select() {
        this.isSelected = true;
        content.setBackground(new Color(186, 202, 68));
        content.setOpaque(true);
    }
 
    public void deselect() {
        this.isSelected = false;
        content.setBackground(baseColor);
        content.setOpaque(true);
    }
 
    public boolean isSelected() {
        return isSelected;
    }
 
    public void setPossibleDestination() {
        this.isPossibleDestination = true;
        // Show a dot indicator overlay (green tint)
        content.setBorder(BorderFactory.createLineBorder(new Color(0, 200, 0), 3));
    }
 
    public void removePossibleDestination() {
        this.isPossibleDestination = false;
        content.setBorder(null);
    }
 
    public boolean isPossibleDestination() {
        return isPossibleDestination;
    }
 
    public void setCheck() {
        this.isCheck = true;
        content.setBackground(new Color(220, 50, 50));
        content.setOpaque(true);
    }
 
    public void removeCheck() {
        this.isCheck = false;
        content.setBackground(baseColor);
        content.setOpaque(true);
    }
 
    public boolean isCheck() {
        return isCheck;
    }
 
    public int getX() {
        return x;
    }
 
    public int getY() {
        return y;
    }
 
    public boolean isEmpty() {
        return piece == null;
    }
 
    public boolean isEnemy(int color) {
        if (piece == null) return false;
        return piece.getColor() != color;
    }
 
    public void updateSprite() {
        if (piece != null && piece.getPath() != null) {
            ImageIcon icon = new ImageIcon(piece.getPath());
            Image scaled = icon.getImage().getScaledInstance(
                    content.getWidth() > 0 ? content.getWidth() - 10 : 60,
                    content.getHeight() > 0 ? content.getHeight() - 10 : 60,
                    Image.SCALE_SMOOTH
            );
            content.setIcon(new ImageIcon(scaled));
        } else {
            content.setIcon(null);
        }
    }
 
    public JLabel getContent() {
        return content;
    }
 
    public Color getBaseColor() {
        return baseColor;
    }
}