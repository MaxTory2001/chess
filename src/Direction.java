public enum Direction {
    UP (8),
    DOWN (-8),
    RIGHT (1),
    LEFT (-1),
    UP_LEFT (7),
    DOWN_LEFT(-9),
    UP_RIGHT (9),
    DOWN_RIGHT(-7);

    public int val;
    Direction(int val) {
        this.val = val;
    }
}
