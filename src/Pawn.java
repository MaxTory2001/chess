import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Pawn extends Piece{
    private Map<String, Direction[]> moveTypes;
    int toPromotion;

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
    public ArrayList<Move> getMoves(long squaresSeenByOtherSide, long pinRayBitMask, long checkSquareBitMask, boolean inCheck) {
        ArrayList<Move> moves = new ArrayList<>();

        int start = square.getSquareNum();
        boolean pinned = (pinRayBitMask & 1L << start) != 0;

        // pinned piece can't move if the king is also in check
        if (pinned && inCheck) return moves;

        for (Direction[] directions : moveTypes.values()) {

            for (Direction direction : directions) {
                int directionOffset = direction.val;

                toPromotion = square.distances.get(direction);

                if (pinned && ((1L << (start + directionOffset) & pinRayBitMask) == 0)) break;

                for (int squaresMoved = 1; squaresMoved <= toPromotion / 6 + 1; squaresMoved ++) {
                    int end = start + direction.val * squaresMoved;
                    if (directionOffset == 8 && board.at(end) == 0) {

                        if (toPromotion == 1) {
                            moves.addAll(makePromotingMoves(start, end));
                        } else {
                            moves.add(new Move(start, end, this));
                        }
                    } else break;
                }
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
        }

        return moves;
    }

    @Override
    public long getSeenSquares(long seenSquaresBitMask) {
        for (Direction direction : moveTypes.get("captures")) {
            if (square.distances.get(direction) == 0) break;

            int start = square.getSquareNum();
            int end = start + direction.val;

            // add this square to the squares seen
            seenSquaresBitMask |= 1L << end;
        }

        return seenSquaresBitMask;
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
