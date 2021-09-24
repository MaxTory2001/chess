import java.util.ArrayList;

public abstract class SlidingPiece extends Piece{
    Direction[] directions; // the directions in which the piece can slide

    @Override
    public ArrayList<Move> getMoves(long squaresSeenByOtherSide, long pinRayBitMask, long checkSquareBitMask, boolean inCheck) {
        ArrayList<Move> moves = new ArrayList<>();
        int start = square.getSquareNum();

        boolean pinned = (pinRayBitMask & 1L << start) != 0;

        // if the piece is pinned and you are also in check, this piece can't move
        if (inCheck && pinned) return moves;

        for (Direction direction : directions) {

            int directionOffset = direction.val;

            // piece can only move in the direction it is pinned
            if (pinned && ((1L << (start + directionOffset) & pinRayBitMask) == 0)) break;

            for (int i = 1; i <= square.getDistance(direction); i++) {

                int end = start + i * direction.val;

                // piece can move here if it isn't in check or it will block a check
                int endSquarePiece = board.at(end);

                // if we run into our own piece, we can go no further
                if(Piece.colourOf(endSquarePiece) == colour) {
                    break;
                }

                moves.add(new Move(start, end, this));

                // If we run into an opponent's piece, add the move but then stop.
                if(Piece.colourOf(endSquarePiece) != null) {

                    break;
                }
            }
        }

        return moves;
    }

    @Override
    public long getSeenSquares(long seenSquaresBitMask) {
        for (Direction direction : directions) {
            for (int i = 1; i <= square.getDistance(direction); i++) {

                int start = square.getSquareNum();
                int end = start + i * direction.val;

                // add this square to the squares seen
                seenSquaresBitMask |= 1L << end;

                if (board.at(end) != 0) {
                    break;
                }
            }
        }

        return seenSquaresBitMask;
    }

    @Override
    public long generateCheckBitMask(int kingSquare){
        long checkRayBitMask = 0;

        int start = square.getSquareNum();
        checkRayBitMask |= 1L << start;
        // will be negative if we are further up the board than the king, otherwise negative
        int pathDirection = start > kingSquare ? -1 : 1;
        int direction;

        // on same rank as the king, check horizontally
        if (start / 8 == kingSquare / 8) direction = 1;
        // on same file as the king, check vertically
        else if (start % 8 == kingSquare % 8) direction = 8;
        // along up-left / down-right diagonal
        else if ((kingSquare-start) / 8 + (kingSquare-start) % 8 == 0) direction = 7;
        // along up-right / down-left diagonal
        else direction = 9;

        for (int directionOffset = direction; start < kingSquare; directionOffset += direction) {
            start = start + (directionOffset * pathDirection);
            checkRayBitMask |= 1L << start;
        }

        return checkRayBitMask;
    }

    @Override
    boolean canPinThisDirection(Direction direction) {
        // sliding pieces can pin any piece in the direction they move
        for (Direction viableDirection : directions) {
            if (direction.val == viableDirection.val) return true;
        }
        return false;
    }
}
