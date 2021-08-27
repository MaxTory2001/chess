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

    public ArrayList<Move> getMoves(boolean movesOrCheckSeen) {
        ArrayList<Move> moves = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            // can't move off the edge of the board
            if (square.distances.get(direction) != 0) {
                int start = square.getSquareNum();
                int end = start + direction.val;

                if (!movesOrCheckSeen || !board.isSeen(end, colour)) {
                    int endSquarePiece = board.at(end);

                    if (!movesOrCheckSeen) board.addSeen(end, colour);

                    else if (Piece.colourOf(endSquarePiece) != colour) {
                        moves.add(new Move(start, end, this));
                    }
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
            seenSquaresBitMask |= (long) 1 << end;
        }

        return seenSquaresBitMask;
    }
}
