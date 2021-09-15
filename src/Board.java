
import java.util.ArrayList;

public class Board {
    // the square on which a pawn can currently be captured via en passant, if one exists
    private int enPassant;

    // Array of all squares on the board
    private final Square[] squares = new Square[64];
    final Piece[] squaresToPieces = new Piece[64];
    private final int[] board = new int[64];

    // Storing the squares seen by each colour
    private final boolean[][] squaresSeen = new boolean[2][64];
    final boolean[] inCheck = new boolean[2];
    private ArrayList<ArrayList<Integer>> blockCheckSquares = new ArrayList<>();

    final int[] kingSquares = new int[2];
    final Move[][] castles = new Move[2][2];
    boolean[][] canCastle = new boolean[2][2];

    MoveGenerator[] moveGenerators;

    private final ArrayList<Move> completedMoves = new ArrayList<>();
    int turn;   // stores the character whose turn it is to move

    private int move;
    private int movesSinceCaptureOrPush; // for 50 move draw rule

    ArrayList<Piece>[] pieces = new ArrayList[2];
    King[] kings = new King[2];
    ArrayList<Knight>[] knights = new ArrayList[2];
    ArrayList<SlidingPiece>[] slidingPieces = new ArrayList[2];
    ArrayList<Pawn>[] pawns = new ArrayList[2];

    // FEN string encoding the initial status of the chess board at the start of a game
    private static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public Board() {
        makeSquares();
        loadFromFEN(START_FEN);
        createCastlingMoves();

        moveGenerators = new MoveGenerator[]{new LegalMovesGenerator(0), new LegalMovesGenerator(1)};
    }

    public Board(String fen) {
        // Constructor when a specific position is provided
        this.makeSquares();
        this.loadFromFEN(fen);
        createCastlingMoves();

        moveGenerators = new MoveGenerator[]{new LegalMovesGenerator(0), new LegalMovesGenerator(1)};
    }

    public void addSeen(int square, Colour colour) {
        int colourIndex = colour == Colour.WHITE ? 0 : 1;

        squaresSeen[colourIndex][square] = true;

        if (kingSquares[(colourIndex + 1) % 2] == square) {
            inCheck[(colourIndex + 1) % 2] = true;
        }
    }

    public void addCheckSquares(ArrayList<Integer> squares) {
        blockCheckSquares.add(squares);
    }

    public ArrayList<ArrayList<Integer>> getCheckSquares() {
        return blockCheckSquares;
    }

    public boolean isSeen(int square, Colour colour) {
        int colourIndex = colour == Colour.WHITE ? 1 : 0;

        return squaresSeen[colourIndex][square];
    }

    public int at(int boardIndex) {
        return board[boardIndex];
    }

    public Piece pieceAt(int boardIndex) {
        return squaresToPieces[boardIndex];
    }

    public Square squareAt(int boardIndex) { return squares[boardIndex]; }

    public int getTurn() { return turn; }

    private void makeSquares() {
        for (int i = 0; i < 64; i++) {
            squares[i] = new Square(i);
        }
    }

    void playerInCheck(int colourIndex) {
        inCheck[colourIndex] = true;
    }

    boolean playerToMoveInCheck() {
        return inCheck[(turn + 2) % 3];
    }

    private void loadFromFEN(String fen) {
        // Loads a chess position into the board from its FEN string
        this.pieces[0] = new ArrayList<>(16);
        this.pieces[1] = new ArrayList<>(16);

        String[] fenStrings = fen.split(" ");

        assert(fenStrings.length == 6); // A FEN string should have 6 fields

        int square = 56; // have to traverse board in a weird order due to order of files in FEN string

        // filling out the board
        for (int i = 0; i < fenStrings[0].length(); i++) {
            char x = fenStrings[0].charAt(i);

            if (Character.isDigit(x)) {
                square += Character.getNumericValue(x);
            } else if (x != '/') {
                assert(square % 8 == 7 && square < 64); // slashes should appear at the end of every row in the board

                int pieceCode = Util.fenCharToPiece.get(x);
                this.board[square] = pieceCode;

                Piece piece = createPieceObject(pieceCode, square);
                int colourIndex = Util.colourToIndex.get(piece.getColour());

                if (piece instanceof King) {
                    kings[colourIndex] = (King) piece;
                } else this.pieces[colourIndex].add(piece);

                this.squaresToPieces[square] = piece;

                square += 1;
            } else {
                square -= 16;
            }
        }

        // determining who moves next from last character in FEN
        this.turn = (fenStrings[1].charAt(0) == 'w') ? Colour.WHITE.val : Colour.BLACK.val;

        // determining the castling rights of each player
        for (int i = 0; i < fenStrings[2].length(); i++) {
            char x = fenStrings[2].charAt(i);
            if (x == '-') break;
            switch (x) {
                case 'K' -> canCastle[0][1] = true;
                case 'Q' -> canCastle[0][0] = true;
                case 'k' -> canCastle[1][1] = true;
                case 'q' -> canCastle[1][0] = true;
            }
        }

        // setting the current active en Passant square of the board
        char enPassantChar = fenStrings[3].charAt(0);
        enPassant = enPassantChar == '-' ? -1 : Character.getNumericValue(enPassantChar);

        // setting number of half moves since last pawn push or capture, and current board move
        movesSinceCaptureOrPush = Character.getNumericValue(fenStrings[4].charAt(0));
        move = Character.getNumericValue(fenStrings[5].charAt(0));
    }

    private Piece createPieceObject(int code, int square) {
        // initialises a new piece of the correct type and colour from its code
        Colour colour = (code > 6) ? Colour.WHITE : Colour.BLACK; // white pieces have codes 7-12, black are 1-6
        int pieceCode = (code - 1) % 6;
        Piece piece = null;
        switch (pieceCode) {
            case 0 -> piece = new Pawn();
            case 1 -> piece = new Rook();
            case 2 -> piece = new Knight();
            case 3 -> piece = new Bishop();
            case 4 -> {
                piece = new King();
                int colourIndex = Util.colourToIndex.get(colour);
                kingSquares[colourIndex] = square;
            }
            case 5 -> piece = new Queen();
        }

        if (piece != null) {
            piece.setSquare(squares[square]);
            piece.setColour(colour);
            piece.setBoard(this);
            piece.setCode(code);
        }

        return piece;
    }

    private ArrayList<Move> getMoves() {
        // setting player not in check before looking for check and generating moves
        inCheck[(turn + 2) % 3] = false;
        return moveGenerators[(turn + 2) % 3].generatePseudoLegalMoves(this);
    }

    public void setEnPassant(int square) {
        enPassant = square;
    }

    private void createCastlingMoves() {
        for (int i = 0; i < 2; i++) {
            castles[i][0] = new Move(kingSquares[i], kingSquares[i] - 2, kings[i]);
            castles[i][1] = new Move(kingSquares[i], kingSquares[i] + 2, kings[i]);
        }
    }

    public int evaluate() {
        int myPieces = 0;
        int theirPieces = 0;
        for (Piece piece : pieces[turn == 1 ? 0 : 1]) {
            myPieces += piece.getValue();
        }
        for (Piece piece : pieces[turn == 1 ? 1 : 0]) {
            theirPieces += piece.getValue();
        }
        return (myPieces - theirPieces);
    }

    public boolean canEnPassant(int square) {
        return enPassant == square;
    }

    public ArrayList<Move> getLegalMoves() {
        return moveGenerators[(turn + 2) % 3].generateLegalMoves(this);
    }

    public void addPiece(Piece piece, int square) {
        squaresToPieces[square] = piece;
        board[square] = piece.getCode();

        if (piece instanceof King) {
            kingSquares[Util.colourToIndex.get(piece.colour)] = square;
        } else {
            pieces[Util.colourToIndex.get(piece.colour)].add(piece);
        }

        piece.setSquare(squares[square]);
    }

    public Piece removePiece(int square) {
        Piece piece = squaresToPieces[square];
        squaresToPieces[square] = null;
        if (piece != null) {
            pieces[Util.colourToIndex.get(piece.colour)].remove(piece);
        }
        board[square] = 0;
        /*
        if (piece != null) {
            piece.setSquare(null);
        }
        */
        return piece;
    }

    public void makeMove(Move move){
        // Executes a move on the board
        move.createSpecialMove(this);
        move.execute(this);

        turn = -turn;
    }

    public void undoMove(Move move){
        // takes back the last move from the board
        move.undo(this);
        turn = -turn;
    }

    public void draw() {
        // draws board in the terminal
        int i = 56;
        System.out.println("---------------------------------");
        while (i > -8) {
            System.out.print("| " + Util.intToDisplayChar.get(board[i]) + " ");
            i ++;
            if (i % 8 == 0) {
                System.out.println("|");
                System.out.println("---------------------------------");
                i -= 16;
            }
        }
    }
}
