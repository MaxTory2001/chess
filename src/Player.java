import java.util.ArrayList;

public abstract class Player {
    public Colour colour;
    Board board;

    public Player(Board board, Colour colour) {
        this.board = board;
        this.colour = colour;
    }

    public abstract Move chooseMove(ArrayList<Move> availableMoves);
}
