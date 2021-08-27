
public class DoublePawnMove extends Move {

    public DoublePawnMove(int start, int end, Piece piece) {
        super(start, end, piece);
    }

    @Override
    public void execute(Board board) {
        board.setEnPassant((start + end) / 2);
    }

    @Override
    public void undo(Board board) {
        // nothing to do
    }
}
