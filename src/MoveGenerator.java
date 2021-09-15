import java.util.ArrayList;

public interface MoveGenerator {
    // generate all pseudolegal moves in the position
    public ArrayList<Move> generatePseudoLegalMoves(Board board);

    // generate all legal moves in the position
    public ArrayList<Move> generateLegalMoves(Board board);
}
