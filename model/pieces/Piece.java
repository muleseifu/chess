package chess.model.piece;

import chess.model.board.Board;
import chess.model.board.Cell;
import java.util.List;

public abstract class Piece {
    protected int color;
    protected String id;
    protected String imagePath;
    protected boolean availability;
    protected boolean hasMoved;

    public Piece(int color, String id, String imagePath) {
        this.color = color;
        this.id = id;
        this.imagePath = imagePath;
        this.availability = true;
        this.hasMoved = false;
    }

    public void SetId(String id) { this.id = id; }
    public void SetPath(String path) { this.imagePath = path; }
    public void SetColor(int color) { this.color = color; }

    public String getId() { return id; }
    public String getPath() { return imagePath; }
    public int getColor() { return color; }

    public boolean isAvailable() { return availability; }
    public void setAvailable(boolean b) { this.availability = b; }
    public boolean hasMoved() { return hasMoved; }
    public void setMoved() { this.hasMoved = true; }


    public abstract Piece getCopy();
    public abstract List<Cell> move(Cell curr, Board b);

    @Override
    public String toString() {
        return id + " (" + (color == 0 ? "White" : "Black") + ")";
    }
}