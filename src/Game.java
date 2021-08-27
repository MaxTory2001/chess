
import java.util.ArrayList;

public class Game {
    private Board board;
    private Player white;
    private Player black;
    private int move = 0;
    private int turn;

    public Game(Board board, Player white, Player black) {
        this.board = board;
        this.white = white;
        this.black = black;
        turn = board.getTurn();
    }

    public int play() {
        while (move < 100) {
            if (turn == 1) move++;
            System.out.println("moves.Move: " + move);
            board.draw();
            ArrayList<Move> availableMoves = board.getLegalMoves();

            if (availableMoves.size() == 0) {
                // if no moves are available it is either checkmate or stalemate
                return (board.playerToMoveInCheck() ? turn : 0);
            }
            // get the appropriate player to select their next move
            Move chosenMove = (turn == 1 ? white : black).chooseMove(availableMoves);

            if (chosenMove == null) break;

            board.makeMove(chosenMove);
            chosenMove.printString();
            turn = -turn;
        }
        return 0;
    }

}
