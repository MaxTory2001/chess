import java.util.ArrayList;

public class NaiveDepthPlayer extends Player{
    private int depth;

    public NaiveDepthPlayer(Board board, Colour colour, int depth) {
        super(board, colour);
        this.depth = depth;
    }

    @Override
    public Move chooseMove(ArrayList<Move> availableMoves) {
        Move bestMove = null;
        int bestValuation = -10000;
        for (Move move : availableMoves){
            int valuation = findValuation(move, depth);
            if (valuation > bestValuation) {
                bestValuation = valuation;
                bestMove = move;
            }
        }
        System.out.println(bestValuation);
        return bestMove;
    }

    private int findValuation(Move move, int remainingDepth) {
        int bestValuation = -10000;

        board.makeMove(move);

        if (remainingDepth == 0) {
            bestValuation = board.evaluate();
        } else {
            for (Move response : board.getLegalMoves()) {
                int valuation = findValuation(response, remainingDepth - 1);
                if (valuation > bestValuation) bestValuation = valuation;
            }
        }

        board.undoMove(move);

        return -bestValuation;
    }
}
