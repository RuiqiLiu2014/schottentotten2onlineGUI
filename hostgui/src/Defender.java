public class Defender extends Player {
    public Defender(PlayerType playerType) {
        super(playerType);
    }

    public boolean isAttacker() {
        return false;
    }
}