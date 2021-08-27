
public class Move implements Comparable<Move>{
    int start;
    int end;
    int captureSquare;
    int value = 0;
    private Piece captured;
    private Piece piece;
    Piece promotionPiece;
    private Move specialMove = null;

    public Move(int start, int end, Piece piece) {
        this.start = start;
        this.end = end;
        this.piece = piece;
        captureSquare = end;
    }

    public Move(int start, int end, Piece piece, Piece promotionPiece) {
        // constructor for when this will be a promotion
        this.start = start;
        this.end = end;
        this.promotionPiece = promotionPiece;
        this.piece = piece;
        captureSquare = end;
    }

    public void createSpecialMove(Board board) {
        if (board.pieceAt(captureSquare) != null) {
            this.value = board.pieceAt(captureSquare).getValue();
        }

        if (this.doublePawnMoveCriterion(board, piece)){
            specialMove = new DoublePawnMove(start, end, piece);
        }
        else if (castleCriterion(board, piece)) {
            // finding the square the rook stands on
            int rookSquare = (end < start ? start - 4 : start + 3);
            specialMove = new CastlingMove(rookSquare, start, end, piece);
        } else if (enPassantCriterion(board, piece)) {
            specialMove = new EnPassant(start, end, piece);
            captureSquare = specialMove.captureSquare;
        } else if (promotionCriterion(board, piece)) {
            specialMove = new Promotion(start, end, piece, promotionPiece);
        }
    }

    public void execute(Board board) {
        board.removePiece(start);

        piece.move();
        piece.setSquare(board.squareAt(end));
        captured = board.removePiece(captureSquare);

        board.addPiece(piece, end);

        if (specialMove != null) {
            specialMove.execute(board);
        }
    }

    public void undo(Board board) {
        board.removePiece(end);

        piece.unMove();
        piece.setSquare(board.squareAt(start));
        if (captured != null) {
            board.addPiece(captured, captureSquare);
        }

        board.addPiece(piece, start);

        if (specialMove != null) {
            specialMove.undo(board);
        }
    }

    private boolean castleCriterion(Board board, Piece piece) {
        if (piece instanceof King) {
            return (Math.abs(end % 8 - start % 8) == 2);
        }
        return false;
    }

    private boolean doublePawnMoveCriterion(Board board, Piece piece) {
        return (piece instanceof Pawn && Math.abs(end-start) == 16);
    }

    private boolean enPassantCriterion(Board board, Piece piece) {
        return (piece instanceof Pawn && (((end - start) % 8) != 0) && board.pieceAt(end) == null);
    }

    private boolean promotionCriterion(Board board, Piece piece) {
        return (piece instanceof Pawn && (end / 8 == 7 || end / 8 == 0));
    }

    public void printString() {
        System.out.println("Moved piece from " + start + " to " + end);
    }

    public int getEnd() { return this.end; }

    public boolean from(int startGuess) { return start == startGuess; }
    public boolean to(int endGuess) { return end == endGuess; }

    @Override
    public int compareTo(Move o) {
        return o.value - value;
    }

    public int getStart() {
        return start;
    }
}
