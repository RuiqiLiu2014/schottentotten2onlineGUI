import java.io.Serializable;

public class ClientMove implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Card card;
    private final int wallIndex;

    public ClientMove(Card card, int wallIndex) {
        this.card = card;
        this.wallIndex = wallIndex;
    }

    public Card getCard() {
        return card;
    }

    public int getWallIndex() {
        return wallIndex;
    }

    public String toString() {
        return card.getColor() + " " + card.getValue() + " on wall " + wallIndex;
    }
}
