import java.util.ArrayList;

public class LegalMovesGenerator implements MoveGenerator{
    int friendlyPieceColour; // Colour of player to move

    int friendlyKingSquare;

    long pinRayBitMask;  // Stores all the pin squares in the position in a single long
    long checkRayBitMask;// stores all the squares involved in checks on the king
    long squaresOpponentSees;
    boolean playerToMoveInCheck;
    boolean isDoubleCheck = false;

    Board board;

    public LegalMovesGenerator(int turnIndex) {
        this.friendlyPieceColour = turnIndex;

        friendlyKingSquare = 0;
        pinRayBitMask = 0;
        checkRayBitMask = 0;
        squaresOpponentSees = 0;
        playerToMoveInCheck = false;
        isDoubleCheck = false;
    }

    @Override
    public ArrayList<Move> generatePseudoLegalMoves(Board board) {
        return null;
    }

    @Override
    public ArrayList<Move> generateLegalMoves(Board board) {
        this.board = board;

        friendlyKingSquare = board.kingSquares[friendlyPieceColour];

        updateChecksAttacksAndPins();

        ArrayList<Move> legalMoves = new ArrayList<>(generateKingMoves());

        if (isDoubleCheck) return legalMoves;

        legalMoves.addAll(generateNonKingMoves());
        legalMoves.addAll(getLegalCastlingMoves());
        return legalMoves;
    }


    void updateChecksAttacksAndPins() {
        // resetting values for pins, checks and squares seen
        checkRayBitMask = 0;
        pinRayBitMask = 0;
        squaresOpponentSees = 0;
        playerToMoveInCheck = false;

        int enemyPieceColour = 1 - friendlyPieceColour;

        for (Piece piece : board.pieces[enemyPieceColour]) {
            long thisPieceSeenSquares = piece.getSeenSquares(pinRayBitMask);

            if ((thisPieceSeenSquares & (long) friendlyKingSquare) != 0) {
                // other piece can see our king! we are in check
                if (playerToMoveInCheck) {
                    isDoubleCheck = true;
                }
                playerToMoveInCheck = true;
                board.playerInCheck(friendlyPieceColour);

                checkRayBitMask |= piece.generateCheckBitMask(friendlyKingSquare);
            }

            squaresOpponentSees |= thisPieceSeenSquares;
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
                    if ((Piece.colourOf(pieceAtSquare).val + 2) % 3 != friendlyPieceColour) {
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
        return king.getMoves(squaresOpponentSees, pinRayBitMask, checkRayBitMask, playerToMoveInCheck);
    }

    ArrayList<Move> getLegalCastlingMoves() {
        int colourIndex  = (board.turn + 2) % 3;
        int kingSquare = board.kingSquares[colourIndex];
        Piece king = board.kings[colourIndex];

        ArrayList<Move> castlingMoves = new ArrayList<>();

        if (king.hasMoved()) {
            return castlingMoves;
        }

        if (board.playerToMoveInCheck()) {
            return castlingMoves;
        }

        for (int direction = -1; direction < 2; direction += 2) {
            int offsetFromKing = direction;
            int directionIndex = direction == 1 ? 1 : 0;

            boolean canCastleThisDirection = board.canCastle[colourIndex][directionIndex];

            while (canCastleThisDirection && (kingSquare + offsetFromKing)/ 8 == kingSquare/8) {
                if (Math.abs(offsetFromKing) <= 2) {
                    canCastleThisDirection = canMoveThrough(kingSquare + offsetFromKing);
                } else if (offsetFromKing == -3 && board.squaresToPieces[kingSquare - 3] == null) {
                    canCastleThisDirection = false;
                } else {
                    Piece piece = board.squaresToPieces[kingSquare + offsetFromKing];
                    if (piece == null || (piece.hasMoved()) || !(piece instanceof Rook)) {
                        canCastleThisDirection = false;
                    }
                }
                offsetFromKing += direction;
            }
            if (canCastleThisDirection) {
                castlingMoves.add(board.castles[colourIndex][direction == -1 ? 0 : 1]);
            }
        }
        return castlingMoves;
    }

    private boolean canMoveThrough(int square) {
        if (board.at(square) != 0) {
            return false;
        }
        return (squaresOpponentSees & 1L << square) == 0;
    }

    ArrayList<Move> generateNonKingMoves() {
        ArrayList<Move> moves = new ArrayList<>();

        for (Piece piece : board.pieces[friendlyPieceColour]) {
            moves.addAll(piece.getMoves(squaresOpponentSees, pinRayBitMask, checkRayBitMask, playerToMoveInCheck));
        }

        return moves;
    }
}
