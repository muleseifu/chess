package Chess.model.pieces;

public abstract class Piece {
    protected int color;
    protected String id;
    protected String imagePath;
    protected boolean hasMoved;
    protected boolean availability;

    public Piece(int color, String id, String imagePath) {
        this.color = color;
        this.id = id;
        this.imagePath = imagePath;
        this.hasMoved = false;
        this.availability = true;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setPath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setAvailable(boolean availability) {
        this.availability = availability;
    }

    public void setMoved() {
        this.hasMoved = true;
    }

    public int getColor() {
        return color;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return imagePath;
    }

    public boolean isAvailable() {
        return availability;
    }

    public boolean hasMoved() {
        return hasMoved;
    }
