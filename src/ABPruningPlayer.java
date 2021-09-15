import java.util.ArrayList;
import java.util.Collections;

public class ABPruningPlayer extends Player{
    private int depth;

    public ABPruningPlayer(Board board, Colour colour, int depth) {
        super(board, colour);
        this.depth = depth;
    }

    @Override
    public Move chooseMove(ArrayList<Move> availableMoves) {
        Move bestMove = null;
        int alpha = -Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;

        Collections.sort(availableMoves);

        for (Move move : availableMoves) {
            board.makeMove(move);
            int valuation = -findValuation(depth-1, -beta, -alpha);

            if (valuation > alpha) {
                alpha = valuation;
                bestMove = move;
            }
            board.undoMove(move);
        }

        System.out.println(alpha);
        return bestMove;
    }

    private int findValuation(int remainingDepth, int alpha, int beta) {
        // alpha is my best score, beta is opponent's worst score
        if (remainingDepth == 0) {
            return board.evaluate();
        }

        ArrayList<Move> availableMoves = board.getLegalMoves();

        Collections.sort(availableMoves);

        if (availableMoves.size() == 0) {
            if (board.playerToMoveInCheck()) {
                return -1000000;
            }
        }

        for (Move move : availableMoves) {
            board.makeMove(move);
            // for the opposite player, alpha and beta swap
            int valuation = -findValuation(remainingDepth - 1, -beta, -alpha);
            board.undoMove(move);

            if (valuation >= beta) {
                // we have too many good options, opponent shouldn't play this move
                return beta;
            }
            alpha = Math.max(alpha, valuation);
        }

        return alpha;
    }

}
