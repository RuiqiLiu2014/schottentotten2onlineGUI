import java.io.Serializable;
import java.util.*;

public class Wall implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int wallIndex;
    private Status status;
    private int length;
    private final int intactLength;
    private final int damagedLength;
    private WallPattern pattern;
    private final WallPattern intactPattern;
    private final WallPattern damagedPattern;

    private final List<Card> attackerCards;
    private final List<Card> defenderCards;

    private boolean attackerFinishedFirst;
    private static final int MULTIPLIER = 100;

    public enum Status {
        BROKEN, DAMAGED, INTACT
    }

    public Wall(int wallIndex, int intactLength, int damagedLength, WallPattern intactPattern, WallPattern damagedPattern) {
        this.wallIndex = wallIndex;
        this.status = Status.INTACT;
        this.intactLength = intactLength;
        this.damagedLength = damagedLength;
        this.intactPattern = intactPattern;
        this.damagedPattern = damagedPattern;
        this.length = intactLength;
        this.pattern = intactPattern;

        this.attackerCards = new ArrayList<>();
        this.defenderCards = new ArrayList<>();
    }

    public List<Card> getAttackerCards() {
        return attackerCards;
    }

    public List<Card> getDefenderCards() {
        return defenderCards;
    }

    public Status getStatus() {
        return status;
    }

    public WallPattern getPattern() {
        return pattern;
    }

    public int getLength() {
        return length;
    }

    public int getWallIndex() {
        return wallIndex;
    }
}
