public class Bishop extends SlidingPiece{

    public Bishop() {
        this.directions = new Direction[]{
                Direction.UP_LEFT, Direction.UP_RIGHT,
                Direction.DOWN_LEFT, Direction.DOWN_RIGHT};
        this.value = 300;
    }

    public Bishop(Colour colour, Square square) {
        this.directions = new Direction[]{
                Direction.UP_LEFT, Direction.UP_RIGHT,
                Direction.DOWN_LEFT, Direction.DOWN_RIGHT};
        this.colour = colour;
        this.square = square;
        this.value = 300;
    }
}
