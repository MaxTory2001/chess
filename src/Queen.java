public class Queen extends SlidingPiece{

    public Queen() {
        this.directions = new Direction[]{
                Direction.DOWN, Direction.DOWN_LEFT, Direction.DOWN_RIGHT, Direction.LEFT, Direction.RIGHT, Direction.UP,
                Direction.UP_LEFT, Direction.UP_RIGHT
        };
        this.value = 900;
    }

    public Queen(Colour colour, Square square) {
        this.directions = new Direction[]{
                Direction.DOWN, Direction.DOWN_LEFT, Direction.DOWN_RIGHT, Direction.LEFT, Direction.RIGHT, Direction.UP,
                Direction.UP_LEFT, Direction.UP_RIGHT
        };
        this.colour = colour;
        this.square = square;
        this.value = 900;
    }
}
