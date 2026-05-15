package Chess.model.pieces;

public abstract class Piece {
    protected int color;
    protected String id;
    protected String imagePath;
    protected boolean availablity;
    protected boolean hasMoved;


    public void SetId(String id) {
        this.id = id;
    }

    public void SetPath(String Path) {
        this.imagePath = Path;
    }

    public void SetColor(int color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return imagePath;
    }

    public int getColor() {
        return color;
    }

    public boolean isAvailable() {
        return availablity;
    }

    public void setAvailable(boolean available) {
        this.availablity = available;
    }

    public void setMoved() {
        this.hasMoved = true;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public abstract Piece getCopy();
}



