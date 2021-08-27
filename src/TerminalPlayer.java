import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class TerminalPlayer extends Player{

    public TerminalPlayer(Board board, Colour colour) {
        super(board, colour);
    }

    @Override
    public Move chooseMove(ArrayList<Move> availableMoves) {
        // if no legal moves are available, we have stalemate or checkmate

        if (availableMoves.size() == 0) return null;

        Scanner input  = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the move you want to make (eg. e2e4):");
            System.out.println("There are " + availableMoves.size() + " valid moves available");

            String moveString = input.nextLine();
            char[] startChars = Arrays.copyOfRange(moveString.toCharArray(), 0, 2);
            char[] endChars = Arrays.copyOfRange(moveString.toCharArray(), 2, 4);

            boolean validMove = true;

            if(!(startChars[0] >= 'a' && startChars[0] <= 'h' && startChars[1] >='1' && startChars[1] <= '8')) {
                validMove = false;
            }
            if(!(endChars[0] >= 'a' && endChars[0] <= 'h' && endChars[1] >='1' && endChars[1] <= '8')) {
                validMove = false;
            }

            int start = (((int) startChars[0]) - ((int) 'a')) + 8 * (Character.getNumericValue(startChars[1]) - 1);
            int end = (((int) endChars[0]) - ((int) 'a')) + 8 * (Character.getNumericValue(endChars[1]) - 1);

            if (validMove) {
                for (Move move : availableMoves) {
                    if(move.from(start) && move.to(end)) {
                        return move;
                    }
                }
            }

            System.out.println("Incorrect move supplied. Please prove a valid move.");
        }
    }
}
