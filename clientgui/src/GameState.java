import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private Set<Card> hostHand;
    private Set<Card> clientHand;
    private Wall[] walls;
    private int deckSize;
    private Map<CardColor, List<Card>> discard;
    private boolean isClientTurn;
    private int cauldronCount;
    private boolean usedCauldron;
    private boolean isClientAttacker;

    public GameState(Set<Card> hostHand, Set<Card> clientHand, Wall[] walls, int deckSize, Map<CardColor, List<Card>> discard, boolean isClientTurn, int cauldronCount, boolean usedCauldron, boolean isClientAttacker) {
        this.hostHand = hostHand;
        this.clientHand = clientHand;
        this.walls = walls;
        this.deckSize = deckSize;
        this.discard = discard;
        this.isClientTurn = isClientTurn;
        this.cauldronCount = cauldronCount;
        this.usedCauldron = usedCauldron;
        this.isClientAttacker = isClientAttacker;
    }

    public Set<Card> getHostHand() {
        return hostHand;
    }

    public Set<Card> getClientHand() {
        return clientHand;
    }

    public Wall[] getWalls() {
        return walls;
    }

    public int getDeckSize() {
        return deckSize;
    }

    public Map<CardColor, List<Card>> getDiscard() {
        return discard;
    }

    public boolean isClientTurn() {
        return isClientTurn;
    }

    public int getCauldronCount() {
        return cauldronCount;
    }

    public boolean getHasUsedCauldron() {
        return usedCauldron;
    }

    public boolean isClientAttacker() {
        return isClientAttacker;
    }
}
