import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class Piece {

    Colour colour;
    Square square;
    int value;
    Board board;
    int code;
    int numMoves = 0;
    Direction pinDirection;

    public abstract ArrayList<Move> getMoves(boolean movesOrCheckSeen);

    public abstract long getSeenSquares(long seenSquaresBitMask);

    boolean canMoveThisDirection(Direction direction) {
        if (pinDirection == null) return true;
        return (direction.val == pinDirection.val || direction.val == -pinDirection.val);
    }

    public Colour getColour() { return this.colour; }

    public int getValue() { return this.value; }

    public static Colour colourOf(int pieceCode) {
        if (pieceCode == 0) return null;
        else return pieceCode > 6 ? Colour.WHITE : Colour.BLACK;
    }

    public void move() { this.numMoves ++; }
    public void unMove() { this.numMoves --; }

    public void setPinDirection(Direction direction) {
        this.pinDirection = direction;
    }

    public boolean isPinned() {
        return pinDirection != null;
    }

    public void unPinned() {
        pinDirection = null;
    }

    public boolean hasMoved() { return this.numMoves > 0; }

    public void setSquare(Square square) { this.square = square; }
    public int getSquare() { return this.square.getSquareNum(); }
    public void setBoard(Board board) {this.board = board; }

    public void setColour(Colour colour) { this.colour = colour; }

    public void setCode(int code) { this.code = code; }
    public int getCode() { return this.code; }
}
