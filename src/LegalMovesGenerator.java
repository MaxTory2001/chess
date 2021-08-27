import java.util.ArrayList;

public class LegalMovesGenerator implements MoveGenerator{
    int friendlyPieceColour; // Colour of player to move

    int friendlyKingSquare;
    int enemyKingSquare;

    long pinRayBitMask;  // Stores all the pin squares in the position in a single long
    long checkRayBitMask;// stores all the squares involved in checks on the king
    long seenSquares;
    boolean playerToMoveInCheck = false;
    boolean isDoubleCheck = false;

    Board board;

    public LegalMovesGenerator(int turnIndex) {
        this.friendlyPieceColour = turnIndex == 1 ? 0 : 1;
    }

    @Override
    public ArrayList<Move> generatePseudoLegalMoves(Board board) {
        return null;
    }

    @Override
    public ArrayList<Move> generateLegalMoves(Board board) {
        this.board = board;

        friendlyKingSquare = board.kingSquares[friendlyPieceColour];

        ArrayList<Move> legalMoves = new ArrayList<>();

        updateChecksAttacksAndPins();

        legalMoves.addAll(generateKingMoves());

        if (isDoubleCheck) return legalMoves;

        legalMoves.addAll(generateKnightMoves());
        legalMoves.addAll(generateSlidingMoves());
        legalMoves.addAll(generatePawnMoves())
        return legalMoves;
    }

    void updateChecksAttacksAndPins() {
        // resetting values for pins, checks and squares seen
        checkRayBitMask = 0L;
        pinRayBitMask = 0L;
        seenSquares = 0L;

        int enemyPieceColour = 1 - friendlyPieceColour;

        for (Piece piece : board.pieces[enemyPieceColour]) {
            long thisPieceSeenSquares = piece.getSeenSquares(pinRayBitMask);

            if ((thisPieceSeenSquares & (long) friendlyKingSquare) != 0) {
                // other piece can see our king! we are in check
                if (playerToMoveInCheck) {
                    isDoubleCheck = true;
                }
                playerToMoveInCheck = true;

                checkRayBitMask |= piece.generateCheckBitMask(friendlyKingSquare);
            }

            seenSquares |= thisPieceSeenSquares;
        }

        updatePins();
    }

    void updatePins() {
        // sending out rays from king to detect pinned pieces
        for (Direction direction : Direction.values()) {
            long thisDirectionPins = 0L;
            boolean foundFriendlyPiece = false;

            for (int dist = 1; dist < board.squareAt(friendlyKingSquare).getDistance(direction); dist++) {
                int square = friendlyKingSquare + dist * direction.val;
                int pieceAtSquare = board.at(square);


                if (pieceAtSquare != 0) {
                    if (Piece.colourOf(pieceAtSquare).val % 3 - 1 != friendlyPieceColour) {
                        if (!foundFriendlyPiece) break;
                        // friendly piece in this direction is pinned by enemy piece
                        if (board.pieceAt(square).canPinThisDirection(direction)) {
                            pinRayBitMask |= thisDirectionPins;
                        }
                    } else {
                        // if we have already found a friendly piece, no pin can occur
                        if (foundFriendlyPiece) break;
                        // if this is the first friendly piece we encounter, continue looking to see if it is pinned
                        foundFriendlyPiece = true;
                        thisDirectionPins |= 1L << square;
                    }
                } else {
                    thisDirectionPins |= 1L << square;
                }
            }
        }
    }

    ArrayList<Move> generateKingMoves() {
        Piece king = board.kings[friendlyPieceColour];
        assert (king instanceof King);
        return king.getMoves(seenSquares, 0, 0);
    }

    ArrayList<Move> generateKnightMoves() {
        ArrayList<Move> knightMoves = new ArrayList<>();

        for (Piece knight : board.knights[friendlyPieceColour]) {
            knightMoves.addAll(knight.getMoves(seenSquares, pinRayBitMask, checkRayBitMask));
        }

        return knightMoves;
    }

    ArrayList<Move> generateSlidingMoves() {
        return null;
    }
}
