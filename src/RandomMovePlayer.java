import java.util.ArrayList;

public class RandomMovePlayer extends Player{

    public RandomMovePlayer(Board board, Colour colour) {
        super(board, colour);
    }

    @Override
    public Move chooseMove(ArrayList<Move> availableMoves) {
        int choiceIndex = Util.rand.nextInt(availableMoves.size());
        return availableMoves.get(choiceIndex);
    }
}
