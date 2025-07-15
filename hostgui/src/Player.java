public abstract class Player {
    protected Hand hand;
    protected boolean usedCauldron;
    protected int cauldronCount = Constants.NUM_CAULDRONS;
    protected PlayerType playerType;

    public Player(PlayerType playerType) {
        this.playerType = playerType;
        hand = new Hand();
    }

    public void draw(Deck deck) {
        Card card = deck.pop();
        if (card != null) {
            hand.add(card);
        }
    }

    public Hand getHand() {
        return hand;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public boolean hasUsedCauldron() {
        return usedCauldron;
    }

    public void setUseCauldron(boolean used) {
        if (used) {
            cauldronCount--;
        }
        usedCauldron = used;
    }

    public int getCauldronCount() {
        return cauldronCount;
    }

    public abstract boolean isAttacker();
}