public class Attacker extends Player {
    public Attacker(PlayerType playerType) {
        super(playerType);
    }

    public boolean isAttacker() {
        return true;
    }
}
