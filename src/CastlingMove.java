public class CastlingMove extends Move {
    private int rookSquare;

    public CastlingMove(int rookSquare, int start, int end, Piece piece) {
        super(start, end, piece);
        this.rookSquare = rookSquare;
    }

    public void execute(Board board) {
        Piece rook = board.removePiece(rookSquare);
        board.addPiece(rook, (start+end)/2);
    }

    public void undo(Board board) {
        Piece rook = board.removePiece((start + end) /2);
        board.addPiece(rook, rookSquare);
    }
}
