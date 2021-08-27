
import java.util.ArrayList;

public class Board {
    // the square on which a pawn can currently be captured via en passant, if one exists
    private int enPassant;

    // Array of all squares on the board
    private final Square[] squares = new Square[64];
    private final Piece[] squaresToPieces = new Piece[64];
    private final int[] board = new int[64];

    // Storing the squares seen by each colour
    private final boolean[][] squaresSeen = new boolean[2][64];
    private boolean[] inCheck = new boolean[2];
    private ArrayList<ArrayList<Integer>> blockCheckSquares = new ArrayList<>();

    private final int[] kingSquares = new int[2];
    private final Move[][] castles = new Move[2][2];
    private boolean[][] canCastle = new boolean[2][2];

    private final ArrayList<Move> completedMoves = new ArrayList<>();
    private int turn;   // stores the character whose turn it is to move

    private int move;
    private int movesSinceCaptureOrPush;

    private final ArrayList<Piece>[] pieces = new ArrayList[2];
    private final King[] kings = new King[2];

    // FEN string encoding the initial status of the chess board at the start of a game
    private static final String START_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    public Board() {
        makeSquares();
        loadFromFEN(START_FEN);
        createCastlingMoves();
    }

    public Board(String fen) {
        // Constructor when a specific position is provided
        this.makeSquares();
        this.loadFromFEN(fen);
        createCastlingMoves();
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
                this.pieces[colourIndex].add(piece);
                if (piece instanceof King) {
                    kings[colourIndex] = (King) piece;
                }
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

        updateSeenAndPins();
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

    private void updateSeenAndPins() {
        int turnIndex = turn == 1 ? 1 : 0; // looking for squares and pieces seen and pinned by the other player
        squaresSeen[turnIndex] = new boolean[64]; // resetting the previously seen squares
        inCheck = new boolean[2]; // resetting the other player to not be in check
        blockCheckSquares = new ArrayList<ArrayList<Integer>>();

        for (Piece piece : pieces[turnIndex]) {
            piece.getMoves(false); // just update seen and pins, don't want moves back
        }
    }

    private ArrayList<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (Piece piece : pieces[turn == -1 ? 1 : 0]) {
            moves.addAll(piece.getMoves(true)); // return available moves
            piece.unPinned();
        }

        return moves;
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

    private ArrayList<Move> getLegalCastlingMoves(ArrayList<Move> alreadyLegalMoves) {
        int colourIndex  = turn == 1 ? 0 : 1;
        int kingSquare = kingSquares[colourIndex];
        Piece king = kings[colourIndex];
        if (king.hasMoved()) {
            return alreadyLegalMoves;
        }

        if (isCheck(true)) {
            return alreadyLegalMoves;
        }

        for (int direction = -1; direction < 2; direction += 2) {
            int offsetFromKing = direction;
            int directionIndex = direction == 1 ? 1 : 0;

            boolean canCastleThisDirection = canCastle[colourIndex][directionIndex];

            while (canCastleThisDirection && (kingSquare + offsetFromKing)/ 8 == kingSquare/8) {
                if (Math.abs(offsetFromKing) <= 2) {
                    canCastleThisDirection = canMoveThrough(kingSquare + offsetFromKing, king);
                } else if (offsetFromKing == -3 && squaresToPieces[kingSquare - 3] == null) {
                    canCastleThisDirection = false;
                } else {
                    Piece piece = squaresToPieces[kingSquare + offsetFromKing];
                    if (piece == null || (piece.hasMoved()) || !(piece instanceof Rook)) {
                        canCastleThisDirection = false;
                    }
                }
                offsetFromKing += direction;
            }
            if (canCastleThisDirection) {
                alreadyLegalMoves.add(castles[colourIndex][direction == -1 ? 0 : 1]);
            }
        }
        return alreadyLegalMoves;
    }

    private boolean canMoveThrough(int square, Piece piece) {
        if (board[square] != 0) {
            return false;
        }
        int colourIndex = (Util.colourToIndex.get(piece.getColour()) + 1) % 2; // Can the other colour see the square?
        return !squaresSeen[colourIndex][square];
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
        ArrayList<Move> legalMoves = getMoves();
        enPassant = -1;
        return getLegalCastlingMoves(legalMoves);
    }

    boolean isCheck(boolean checkPlayerToMove) {
        int colourIndex = turn == 1 ? 0 : 1;

        if (checkPlayerToMove) colourIndex = (colourIndex + 1) % 2;

        return squaresSeen[colourIndex][kingSquares[(colourIndex + 1) % 2]];
    }

    public void addPiece(Piece piece, int square) {
        squaresToPieces[square] = piece;
        board[square] = piece.getCode();
        pieces[Util.colourToIndex.get(piece.colour)].add(piece);

        if (piece instanceof King) {
            kingSquares[Util.colourToIndex.get(piece.getColour())] = square;
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

        /* if (piece != null) {
            piece.setSquare(null);
        }*/

        return piece;
    }

    public void makeMove(Move move){
        // Executes a move on the board
        move.createSpecialMove(this);
        move.execute(this);
        completedMoves.add(move);
        turn = -turn;

        updateSeenAndPins();
    }

    public void undoMove(){
        // takes back the last move from the board
        Move undoingMove = completedMoves.remove(completedMoves.size() - 1);
        undoingMove.undo(this);
        turn = -turn;

        updateSeenAndPins();
    }
//comment
    public void draw() {
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
