import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pawn extends Piece{
    private Map<String, Direction[]> moveTypes;

    public Pawn(){
        this.value = 100;
    }

    public Pawn(Colour colour, Square square) {
        this.colour = colour;
        if (colour == Colour.WHITE) {
            this.moveTypes = new HashMap<>() {{
                put("capture", new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT});
                put("normal", new Direction[]{Direction.UP});
            }};
        } else {
            this.moveTypes = new HashMap<>() {{
                put("capture", new Direction[]{Direction.DOWN_LEFT, Direction.DOWN_RIGHT});
                put("normal", new Direction[]{Direction.DOWN});
            }};
        }
        this.square = square;
        this.value = 100;
    }

    @Override
    public void setColour(Colour colour) {
        super.setColour(colour);
        if (colour == Colour.WHITE) {
            this.moveTypes = new HashMap<>() {{
                put("capture", new Direction[]{Direction.UP_LEFT, Direction.UP_RIGHT});
                put("normal", new Direction[]{Direction.UP});
            }};
        } else {
            this.moveTypes = new HashMap<>() {{
                put("capture", new Direction[]{Direction.DOWN_LEFT, Direction.DOWN_RIGHT});
                put("normal", new Direction[]{Direction.DOWN});
            }};
        }
    }

    @Override
    public ArrayList<Move> getMoves(boolean movesOrCheckSeen) {
        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<ArrayList<Integer>> squaresToBlockCheck = board.getCheckSquares();

        // if the king is double-checked, only a king move can escape check
        if (squaresToBlockCheck.size() > 1) {
            return moves;
        }

        int toPromotion = 7;

        for (Direction direction : moveTypes.get("normal")) {
            toPromotion = square.distances.get(direction);

            if (movesOrCheckSeen && !canMoveThisDirection(direction)) {
                break;
            }

            for (int squaresMoved = 1; squaresMoved <= toPromotion / 6 + 1; squaresMoved ++) {

                int start = square.getSquareNum();
                int end = start + direction.val * squaresMoved;
                if (board.at(end) == 0) {

                    if (toPromotion == 1) {
                        if (squaresToBlockCheck.size() == 0 || (squaresToBlockCheck.get(0).contains(end))) {
                            moves.addAll(makePromotingMoves(start, end));
                        }
                    } else {
                        if (squaresToBlockCheck.size() == 0 || (squaresToBlockCheck.get(0).contains(end))) {
                            moves.add(new Move(start, end, this));
                        }
                    }
                } else break;
            }
        }

        for (Direction captureDirection : moveTypes.get("capture")) {
            if (movesOrCheckSeen && !canMoveThisDirection(captureDirection)) {
                break;
            }
            if (square.distances.get(captureDirection) != 0) {
                int start = square.getSquareNum();
                int end = start + captureDirection.val;
                // if there is no check to block (or piece delivering check to take), continue as normal
                if (squaresToBlockCheck.size() == 0 || (squaresToBlockCheck.get(0).contains(end))) {

                    int pieceAtEndSquare = board.at(end);

                    if (!movesOrCheckSeen) {
                        board.addSeen(end, colour);

                        if (board.pieceAt(end) instanceof King) {
                            ArrayList<Integer> thisSquareAndCheckSquare = new ArrayList<>();
                            thisSquareAndCheckSquare.add(start);
                            thisSquareAndCheckSquare.add(end);
                            board.addCheckSquares(thisSquareAndCheckSquare);
                        }
                    } else if ((pieceAtEndSquare != 0 && Piece.colourOf(pieceAtEndSquare) != colour) || toPromotion == 3 &&
                            board.canEnPassant(end)) {
                        if (toPromotion == 1) {
                            moves.addAll(makePromotingMoves(start, end));
                        } else {
                            moves.add(new Move(start, end, this));
                        }
                    }
                }
            }
        }

        return moves;
    }

    ArrayList<Move> makePromotingMoves(int start, int end){
        ArrayList<Move> promotionMoves = new ArrayList<>();
        Piece promotingPiece = new Queen(colour, square);
        promotingPiece.setBoard(board);
        promotionMoves.add(new Move(start, end, this, promotingPiece));
        promotingPiece = new Rook(colour, square);
        promotingPiece.setBoard(board);
        promotionMoves.add(new Move(start, end, this, promotingPiece));
        promotingPiece = new Knight(colour, square);
        promotingPiece.setBoard(board);
        promotionMoves.add(new Move(start, end, this, promotingPiece));
        promotingPiece = new Bishop(colour, square);
        promotingPiece.setBoard(board);
        promotionMoves.add(new Move(start, end, this, promotingPiece));

        return promotionMoves;
    }
}
