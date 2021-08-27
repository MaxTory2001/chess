public class Rook extends SlidingPiece{
    public Rook() {
        this.directions = new Direction[]{
                Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP
        };
        this.value = 500;
    }

    public Rook(Colour colour, Square square) {
        this.directions = new Direction[]{
                Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP
        };
        this.colour = colour;
        this.square = square;
        this.value = 500;
    }
}
