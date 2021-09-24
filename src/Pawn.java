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

                int end = start + direction.val;
                int pieceAtEndSquare = board.at(end);

                // Check if we will hit the edge of the board!
                if (square.distances.get(direction) == 0) continue;

                Direction forward = (colour == Colour.WHITE) ? Direction.UP : Direction.DOWN;

                // how many squares away from promoting are we?
                int toPromotion = square.distances.get(forward);

                if (pinned && ((1L << (start + directionOffset) & pinRayBitMask) == 0)) continue;

                if (Math.abs(directionOffset) == 8) {
                    // getting moves straight forward
                    for (int squaresMoved = 1; squaresMoved <= toPromotion / 6 + 1; squaresMoved ++) {
                        end = start + direction.val * squaresMoved;
                        pieceAtEndSquare = board.at(end);

                        if (pieceAtEndSquare == 0) {

                            if (toPromotion == 1) {
                                moves.addAll(makePromotingMoves(start, end));
                            } else {
                                moves.add(new Move(start, end, this));
                            }
                        } else break; // can never move if there is a piece in front
                    }
                }
                // diagonal moves
                else if ((pieceAtEndSquare != 0 && Piece.colourOf(pieceAtEndSquare) != colour)) {
                    if (toPromotion == 1) {
                        moves.addAll(makePromotingMoves(start, end));
                    } else {
                        moves.add(new Move(start, end, this));
                    }
                } else if (board.canEnPassant(end)) {
                    // specific condition required to check if an enPassant is legal

                    int friendlyKingSquare = board.kingSquares[(colour.val + 2) % 3];
                    int directionOfTargetPawn = end % 8 - start % 8;

                    // en passant can only be pinned if king is on same rank
                    if (friendlyKingSquare / 8 != start / 8) continue;

                    if (!pinnedEnPassant(friendlyKingSquare, start, directionOfTargetPawn)) {
                        moves.add(new Move(start, start, this));
                    }

                }

            }
        }

        return moves;
    }

    @Override
    public long getSeenSquares(long seenSquaresBitMask) {
        for (Direction direction : moveTypes.get("capture")) {
            if (square.distances.get(direction) == 0) continue;

            int start = square.getSquareNum();
            int end = start + direction.val;

            // add this square to the squares seen
            seenSquaresBitMask |= 1L << end;
        }

        return seenSquaresBitMask;
    }

    private boolean pinnedEnPassant(int kingSquare, int pawnSquare, int directionOfTargetPawn) {
        int distKingToFirstPawn = Math.min(Math.abs(pawnSquare - kingSquare), Math.abs(pawnSquare + directionOfTargetPawn - kingSquare));
        Direction dirKingToPawn = (pawnSquare - kingSquare) > 1 ? Direction.RIGHT : Direction.LEFT;

        for (int i = 1; i < distKingToFirstPawn; i ++ ) {
            kingSquare += dirKingToPawn.val;
            // if there is any piece between the king and the en-passanting pawns, the weird pin rule doesn't apply
            if (board.at(kingSquare) != 0) return false;
        }

        kingSquare += 2* dirKingToPawn.val;

        for (int i = 1; i <= board.squareAt(kingSquare).distances.get(dirKingToPawn); i++) {
            kingSquare += dirKingToPawn.val;
            // the first piece we encounter determines whether we can play the en passant.
            int pieceAtThisSquare = board.at(kingSquare);

            if (pieceAtThisSquare != 0) {
                // same colour piece cannot pin us
                if (Piece.colourOf(pieceAtThisSquare) == colour) return false;
                // can only be pinned by a piece that sees in this direction
                if (board.pieceAt(kingSquare).canPinThisDirection(dirKingToPawn)) {
                    return true;
                }
            }
        }

        return false;
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
