package Chess.util;
 
import Chess.model.board.Cell;
import Chess.model.pieces.Piece;
 
public class MoveRecord {
 
    public Cell from;
    public Cell to;
    public Piece movedPiece;
    public Piece capturedPiece;
    public boolean isCastle;
    public boolean isEnPassant;
    public boolean isPromotion;
    public String notation;
 
    public MoveRecord(Cell from, Cell to, Piece movedPiece, Piece capturedPiece,
                      boolean isCastle, boolean isEnPassant, boolean isPromotion,
                      String notation) {
        this.from = from;
        this.to = to;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.isCastle = isCastle;
        this.isEnPassant = isEnPassant;
        this.isPromotion = isPromotion;
        this.notation = notation;
    }
}

