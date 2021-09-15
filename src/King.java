import java.util.ArrayList;

public class King extends Piece{
    public King() {
        this.value = 0;
    }

    public King(Colour colour, Square square) {
        this.colour = colour;
        this.square = square;
        this.value = 0;
    }

    public ArrayList<Move> getMoves(long squaresSeenByOtherSide, long pinRayBitMask, long checkSquareBitMask, boolean inCheck) {
        ArrayList<Move> moves = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            // can't move off the edge of the board
            if (square.distances.get(direction) != 0) {
                int start = square.getSquareNum();
                int end = start + direction.val;
                // can't move into check
                if ((1L << end & squaresSeenByOtherSide) != 0) {
                    moves.add(new Move(start, end, this));
                }
            }
        }

        return moves;
    }

    @Override
    public long getSeenSquares(long seenSquaresBitMask) {
        for (Direction direction : Direction.values()) {
            if (square.distances.get(direction) == 0) break;

            int start = square.getSquareNum();
            int end = start + direction.val;

            // add this square to the squares seen
            seenSquaresBitMask |= 1L << end;
        }

        return seenSquaresBitMask;
    }
}
