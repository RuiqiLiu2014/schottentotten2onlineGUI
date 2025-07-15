import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class GameController {
    private final Game game;
    private GameView gameView;
    private final Role hostRole;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    private Thread listenerThread = null;

    private Phase currentPhase;

    private enum Phase {
        HOST_TURN,
        CLIENT_TURN,
        GAME_OVER
    }

    public GameController(Game game, Role hostRole, ObjectOutputStream out, ObjectInputStream in) {
        this.game = game;
        this.gameView = new GameView(createGameState(), this::onWallClicked);
        this.hostRole = hostRole;
        this.out = out;
        this.in = in;
    }

    public void setup(){
        game.setup();
    }

    public void runGame() throws IOException {
        currentPhase = hostRole == Role.ATTACKER ? Phase.HOST_TURN : Phase.CLIENT_TURN;
        displayGameState();
        if (currentPhase == Phase.CLIENT_TURN) {
            waitForClientMove();
        }
    }

    private Card getSelectedCard() {
        if (currentPhase == Phase.HOST_TURN) {
            return gameView.getSelectedCard();
        }
        return null;
    }

    private void waitForClientMove() {
        if (listenerThread != null && listenerThread.isAlive()) {
            listenerThread.interrupt();
        }

        listenerThread = new Thread(() -> {
            try {
                while (currentPhase == Phase.CLIENT_TURN) {  // Keep checking if it's the client's turn
                    Object obj = in.readObject();
                    if (obj instanceof ClientMove move) {
                        processMove(move);
                    } else {
                        System.err.println("Unexpected object: " + obj.getClass());
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        listenerThread.start();
    }

    private void processMove(ClientMove move) throws IOException {
        Card card = move.getCard();
        Wall wall = game.getBoard().getWalls()[move.getWallIndex()];

        if (currentPhase == Phase.CLIENT_TURN) {
            PlayResult result = wall.playCard(card, hostRole == Role.DEFENDER);
            if (result.getResultType() == PlayResult.Type.SUCCESS) {
                game.getDiscard().addAll(result.getToDiscard());
                getClient().getHand().remove(card);
                getClient().draw(game.getDeck());
                game.declareControl();
                displayGameState();
                Winner winner = game.getWinner(hostRole == Role.DEFENDER);
                if (winner != Winner.NONE) {
                    currentPhase = Phase.GAME_OVER;
                    SwingUtilities.invokeLater(() -> gameView.displayWinner(winner));
                    out.writeObject(new GameOverMessage(winner == Winner.ATTACKER ? "Attacker" : "Defender"));
                    return;
                }
                currentPhase = Phase.HOST_TURN;
                getClient().setUseCauldron(false);
            } else if (result.getResultType() == PlayResult.Type.ACTION) {
                List<Card> toDiscard = result.getToDiscard();
                if (!toDiscard.isEmpty()) {
                    game.getDiscard().addAll(toDiscard);
                    if (hostRole == Role.ATTACKER) {
                        game.getDefender().setUseCauldron(true);
                    }
                }
            }
            displayGameState();
        }
    }

    private Player getClient() {
        return hostRole == Role.DEFENDER ? game.getAttacker() : game.getDefender();
    }

    private Player getHost() {
        return hostRole == Role.ATTACKER ? game.getAttacker() : game.getDefender();
    }

    private GameState createGameState() {
        return new GameState(getHost().getHand().getCards(),
                getClient().getHand().getCards(),
                game.getBoard().getWalls(),
                game.getDeck().size(),
                game.getDiscard().getCardsByColor(),
                currentPhase == Phase.CLIENT_TURN,
                game.getDefender().getCauldronCount(),
                game.getDefender().hasUsedCauldron(),
                hostRole == Role.DEFENDER);
    }

    public void displayGameState() throws IOException {
        GameState state = createGameState();
        gameView = new GameView(state, this::onWallClicked);
        HostGUI.displayGameState();
        out.reset();
        out.writeObject(state);
        out.flush();
    }

    public void onWallClicked(Wall wall) {
        if (currentPhase == Phase.HOST_TURN) {
            Card card = getSelectedCard();
            if (card != null) {
                PlayResult result = wall.playCard(card, hostRole == Role.ATTACKER);
                if (result.getResultType() == PlayResult.Type.SUCCESS) {
                    game.getDiscard().addAll(result.getToDiscard());
                    getHost().getHand().remove(card);
                    gameView.unselectCard();
                    getHost().draw(game.getDeck());
                    game.declareControl();
                    try {
                        displayGameState();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Winner winner = game.getWinner(hostRole == Role.ATTACKER);
                    if (winner != Winner.NONE) {
                        currentPhase = Phase.GAME_OVER;
                        SwingUtilities.invokeLater(() -> gameView.displayWinner(winner));
                        try {
                            out.writeObject(new GameOverMessage(winner == Winner.ATTACKER ? "Attacker" : "Defender"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    currentPhase = Phase.CLIENT_TURN;
                    getHost().setUseCauldron(false);
                    try {
                        displayGameState();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    waitForClientMove();
                } else if (result.getResultType() == PlayResult.Type.ACTION) {
                    List<Card> toDiscard = result.getToDiscard();
                    if (!toDiscard.isEmpty()) {
                        game.getDiscard().addAll(toDiscard);
                        if (hostRole == Role.DEFENDER) {
                            game.getDefender().setUseCauldron(true);
                        }
                        try {
                            displayGameState();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        waitForClientMove();
                    }
                }
            }
        } else {
            HostGUI.notYourTurn();
        }
    }

    public GameView getGameView() {
        return gameView;
    }
}
