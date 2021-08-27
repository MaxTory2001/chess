
public class Promotion extends Move {
    Piece newPiece;
    Piece oldPawn;

    public Promotion(int start, int end, Piece piece, Piece newPiece) {
        super(start, end, piece);
        this.newPiece = newPiece;
    }

    @Override
    public void execute(Board board) {
        oldPawn = board.removePiece(end);
        board.addPiece(newPiece, end);
    }

    @Override
    public void undo(Board board) {
        board.removePiece(start);
        board.addPiece(oldPawn, start);
    }
}
