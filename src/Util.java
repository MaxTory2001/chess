
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class Util {
    public static final Map<Character, Integer> fenCharToPiece = new HashMap<Character, Integer>() {{
        put('p', 1);
        put('r', 2);
        put('n', 3);
        put('b', 4);
        put('k', 5);
        put('q', 6);
        put('P', 7);
        put('R', 8);
        put('N', 9);
        put('B', 10);
        put('K', 11);
        put('Q', 12);
    }};

    public static final Map<Integer, Character> intToDisplayChar = new HashMap<Integer, Character>() {{
        put(0,' ');
        put(1,'p');
        put(2,'r');
        put(3,'n');
        put(4,'b');
        put(5,'k');
        put(6,'q');
        put(7,'P');
        put(8,'R');
        put(9,'N');
        put(10,'B');
        put(11,'K');
        put(12,'Q');
    }};

    public static final Map<Colour, Integer>colourToIndex = new HashMap<Colour, Integer>() {{
        put(Colour.WHITE, 0);
        put(Colour.BLACK, 1);
    }};

    public static Random rand = new Random();
}
