import java.util.ArrayList;

public class LegalMovesGenerator implements MoveGenerator{

    int friendlyPieceColour; // Colour of piece to move
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

        ArrayList<Move> legalMoves = new ArrayList<>();

        updateChecksAttacksAndPins();
        return legalMoves;
    }

    void updateChecksAttacksAndPins() {
        // resetting values for pins, checks and squares seen
        checkRayBitMask = 0;
        pinRayBitMask = 0;
        seenSquares = 0;

        int enemyPieceColour = 1 - friendlyPieceColour;

        for (Piece piece : board.pieces[enemyPieceColour]) {
            long thisPieceSeenSquares = piece.getSeenSquares(pinRayBitMask);

            if ((thisPieceSeenSquares & (long) board.kingSquares[friendlyPieceColour]) != 0) {
                // other piece can see our king! we are in check
                if (playerToMoveInCheck) {
                    isDoubleCheck = true;
                }
                playerToMoveInCheck = true;
            }

            seenSquares |= thisPieceSeenSquares;
        }
    }

    ArrayList<Move> generateSlidingMoves() {
        return null;
    }
}
