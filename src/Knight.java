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
    public ArrayList<Move> getMoves(long squaresSeenByOtherSide, long pinRayBitMask, long checkSquareBitMask, boolean inCheck) {
        ArrayList<Move> moves = new ArrayList<>();

        int start = square.getSquareNum();

        // pinned knight can never move
        if ((pinRayBitMask & 1L << start) != 0) return moves;

        int startx = start % 8;
        int starty = start / 8;

        for (int possibleMove : possibleMoves) {
            int end = start + possibleMove;
            int endx = end % 8;
            int endy = end / 8;

            // checking that knight can move within boundaries of the board

            if (end < 0 || end >= 64) continue;
            // horizontal and vertical movement of knight should only ever be one or 2 squares
            if (Math.abs(endx - startx) >= 3 || Math.abs(endy - starty) >= 3) continue;

            if (!board.playerToMoveInCheck() || (checkSquareBitMask & 1L << end) != 0) {
                // if the king isn't in check or this move blocks the check, we are safe to move legally
                int endSquarePiece = board.at(end);

                if (Piece.colourOf(endSquarePiece) != colour) {
                    moves.add(new Move(start, end, this));
                }
            }
        }

        return moves;
    }

    @Override
    public long getSeenSquares(long seenSquaresBitMask) {
        int start = square.getSquareNum();

        int startx = start % 8;
        int starty = start / 8;

        for (int possibleMove : possibleMoves) { ;
            int end = start + possibleMove;

            if (end < 0 || end > 63) break; // can only move within board

            int endx = end % 8;
            int endy = end / 8;

            // move takes the knight from one side of the board to the other. Not allowed!
            if (Math.abs(endx - startx) >= 3 || Math.abs(endy - starty) >= 3) break;

            // add this square to the squares seen
            seenSquaresBitMask |= 1L << end;
        }

        return seenSquaresBitMask;
    }
}
