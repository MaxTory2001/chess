import java.util.HashMap;
import java.util.Map;

public class Square {
    private int square;
    public Map<Direction, Integer> distances = new HashMap<>();

    public Square(){}

    public Square(int square) {
        this.square = square;
        this.calculateEdgeDistances();
    }

    public int getSquareNum() { return this.square; }

    public int getDistance(Direction direction) {
        return this.distances.get(direction);
    }

    private void calculateEdgeDistances() {
        distances.put(Direction.UP,7 - square/8);
        distances.put(Direction.DOWN, square/8);
        distances.put(Direction.LEFT, square % 8);
        distances.put(Direction.RIGHT,7 - square % 8);
        distances.put(Direction.UP_LEFT, Math.min(distances.get(Direction.UP),distances.get(Direction.LEFT)));
        distances.put(Direction.UP_RIGHT, Math.min(distances.get(Direction.UP),distances.get(Direction.RIGHT)));
        distances.put(Direction.DOWN_LEFT, Math.min(distances.get(Direction.DOWN),distances.get(Direction.LEFT)));
        distances.put(Direction.DOWN_RIGHT, Math.min(distances.get(Direction.DOWN),distances.get(Direction.RIGHT)));
    }
}
