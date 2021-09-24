public class Main {
    public static final String[] TEST_FENS = {"6r1/1pk4r/2pp1b2/5P2/1K6/QP2nnp1/4P1B1/5R2 b - - 0 20",
            "8/8/6pr/6p1/5pPk/5P1p/5P1K/R7 w - - 0 20",
            "1kr5/ppN2ppr/8/3p2n1/3bb3/8/n3PPPQ/4R1K1 w - - 0 1",
            "r3k2r/ppp2Npp/1b5n/4p2b/2B1P2q/BQP2P2/P5PP/RN5K w kq - 1 0",
            "r1b3kr/ppp1Bp1p/1b6/n2P4/2p3q1/2Q2N2/P4PPP/RN2R1K1 w - - 1 0"
    };

    public static void main(String[] args) {
        //for (String testFen : TEST_FENS) {
            Board gameBoard = new Board();
            //Board gameBoard = new Board("rnbqkbnr/pppppppp/8/8/8/2P5/PP1PPPPP/RNBQKBNR b KQkq - 0 1");

            //Player white = new TerminalPlayer(gameBoard, Colour.WHITE);
            //Player white = new ABPruningPlayer(gameBoard, Colour.WHITE, 5);
            Player white = new NumberOfPositionsDetector(gameBoard, Colour.WHITE, 5);
            //Player black = new TerminalPlayer(gameBoard, Colour.BLACK);
            //Player black = new ABPruningPlayer(gameBoard, Colour.BLACK, 5);
            Player black = new NumberOfPositionsDetector(gameBoard, Colour.BLACK, 3);

            Game game = new Game(gameBoard, white, black);
            int winner = game.play();
            printWinnerMessage(winner);
        //}
    }

    private static void printWinnerMessage(int winner) {
        if (winner == 0) System.out.println("Draw");
        else System.out.println("Checkmate, " + (winner == 1 ? "black" : "white") + " wins");
    }
}
