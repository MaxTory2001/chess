import java.util.ArrayList;

public class NumberOfPositionsDetector extends Player{
    private int maxDepth;

    public NumberOfPositionsDetector(Board board, Colour colour, int maxDepth) {
        super(board, colour);
        this.maxDepth = maxDepth;
    }

    @Override
    public Move chooseMove(ArrayList<Move> availableMoves) {

        for (int depth = 1; depth <= maxDepth; depth ++) {
            int availablePositions = 0;
            for (Move move : availableMoves){
                int downThisBranch = getNumAvailable(move, depth - 1);
                availablePositions += downThisBranch;
                if (depth == maxDepth) {
                    System.out.println("From " + move.getStart() + " to " + move.getEnd() + ": " + downThisBranch + " possible sequences.");
                }
            }
            System.out.println("Depth = " + depth + " ply: " + availablePositions + " possible sequences.\n");
        }
        return null;
    }

    private int getNumAvailable(Move move, int remainingDepth) {
        if (remainingDepth == 0) return 1;

        int numberOfPositions = 0;

        board.makeMove(move);
        ArrayList<Move> responses = board.getLegalMoves();
        if (remainingDepth == 1) {
            numberOfPositions = responses.size();
        } else {
            for (Move response : board.getLegalMoves()) {
                numberOfPositions += getNumAvailable(response, remainingDepth -1);
            }
        }
        board.undoMove();

        return numberOfPositions;
    }
}
