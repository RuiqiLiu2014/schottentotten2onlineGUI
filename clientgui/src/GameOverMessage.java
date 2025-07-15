import java.io.Serializable;

public class GameOverMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String winner;

    public GameOverMessage(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }
}
