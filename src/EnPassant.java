
public class EnPassant extends Move {

    public EnPassant(int start, int end, Piece piece) {
        super(start, end, piece);
        int captureOffset = (end - start) % 8;
        if (captureOffset == 7) {
            captureOffset = -1;
        }
        captureSquare = start + captureOffset;
    }

    @Override
    public void execute(Board board) {
        // do nothing
    }

    @Override
    public void undo(Board board) {
        // do nothing
    }
}
