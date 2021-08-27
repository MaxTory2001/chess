import java.util.ArrayList;

public class Knight extends Piece{

    private static final int[] possibleMoves = {6, 15, 17, 10, -6, -15, -17, -10};

    public Knight() {
        this.value = 300;
    }

    public Knight(Colour colour, Square square) {
        this.colour = colour;
        this.square = square;
        this.value = 300;
    }

    @Override
    public ArrayList<Move> getMoves(boolean movesOrCheckSeen) {
        ArrayList<Move> moves = new ArrayList<>();
        ArrayList<ArrayList<Integer>> squaresToBlockCheck = board.getCheckSquares();

        // if the king is double-checked, only a king move can escape check. A Knight can also never move if pinned
        if (isPinned() || squaresToBlockCheck.size() > 1) {
            return moves;
        }

        for (int possibleMove : possibleMoves) {

            int start = square.getSquareNum();
            int startx = start % 8;
            int starty = start / 8;

            int end = start + possibleMove;
            int endx = end % 8;
            int endy = end / 8;

            // checking that knight can move within boundaries of the board

            if (end >= 0 && end < 64) {

                if (squaresToBlockCheck.size() == 0 || (squaresToBlockCheck.get(0).contains(end))) {

                    // horizontal and vertical movement of knight should only ever be one or 2 squares
                    if (Math.abs(endx - startx) < 3 && Math.abs(endy - starty) < 3) {
                        int endSquarePiece = board.at(end);

                        if (Piece.colourOf(endSquarePiece) != colour) {
                            if (movesOrCheckSeen) {
                                moves.add(new Move(start, end, this));
                            } else {
                                board.addSeen(end, colour);

                                if (board.pieceAt(end) instanceof King) {
                                    ArrayList<Integer> thisSquareAndCheckSquare = new ArrayList<>(2);
                                    thisSquareAndCheckSquare.add(start);
                                    thisSquareAndCheckSquare.add(end);
                                    board.addCheckSquares(thisSquareAndCheckSquare);
                                }
                            }

                        }
                    }
                }
            }
        }

        return moves;
    }
}
