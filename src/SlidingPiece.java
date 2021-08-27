import java.util.ArrayList;

public abstract class SlidingPiece extends Piece{
    Direction[] directions; // the directions in which the piece can slide

    public ArrayList<Move> getMoves(boolean movesOrCheckSeen){
        ArrayList<ArrayList<Integer>> squaresToBlockCheck = board.getCheckSquares();
        ArrayList<Move> moves = new ArrayList<>();

        // if the king is double-checked, only a king move can escape check
        if (squaresToBlockCheck.size() > 1) {
            return moves;
        }

        for (Direction direction : directions) {
            ArrayList<Integer> squaresInThisDirection = new ArrayList<>(8);
            // piece can only move in the direction it is pinned
            if (movesOrCheckSeen && !canMoveThisDirection(direction)) {
                break;
            }

            for (int i = 1; i <= square.getDistance(direction); i++) {

                int start = square.getSquareNum();
                int end = start + i * direction.val;


                // piece can move here if it isn't in check or it will block a check
                int endSquarePiece = board.at(end);

                // if we run into our own piece, we can go no further
                if(Piece.colourOf(endSquarePiece) == colour) {
                    break;
                }
                if (squaresToBlockCheck.size() == 0 || (squaresToBlockCheck.get(0).contains(end))) {
                    if (movesOrCheckSeen) {
                        moves.add(new Move(start, end, this));
                    } else {
                        board.addSeen(end, colour);
                    }

                    // If we run into an opponent's piece, add the move but then stop.
                    if(Piece.colourOf(endSquarePiece) != null) {
                        if (!movesOrCheckSeen) {
                            Piece opponentsPiece = board.pieceAt(end);
                            if (opponentsPiece instanceof King) {
                                squaresInThisDirection.add(start);
                                board.addCheckSquares(squaresInThisDirection);
                            }
                            else if (pinsToKing(end, direction)) {
                                opponentsPiece.setPinDirection(direction);
                            }
                        }

                        break;
                    }
                }
                squaresInThisDirection.add(end);
            }
        }

        return moves;
    }

    private boolean pinsToKing(int start, Direction direction) {
        // checks beyond a piece to see if the king is behind it in this direction
        for (int i = 1; i <= board.squareAt(start).getDistance(direction); i++) {
            int end = start + i * direction.val;

            int endSquarePiece = board.at(end);
            Colour endSquarePieceColour = Piece.colourOf(endSquarePiece);

            if(endSquarePiece != 0) {
                return (board.pieceAt(end) instanceof King && endSquarePieceColour != colour);
            }
        }
        return false;
    }
}
