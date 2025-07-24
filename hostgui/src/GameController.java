import javax.swing.*;
import java.io.*;
import java.util.List;
import com.google.gson.Gson;
import java.net.*;

public class GameController {
    private final Game game;
    private GameView gameView;
    private final Role hostRole;
    private static final Gson gson = new Gson();
    private final PrintWriter out;
    private final BufferedReader in;

    private Thread listenerThread = null;

    private Phase currentPhase;

    private enum Phase {
        HOST_TURN,
        CLIENT_TURN,
        GAME_OVER
    }

    public GameController(Game game, Role hostRole, Socket clientSocket) throws IOException {
        this.game = game;
        this.gameView = new GameView(createGameState(false), this::onWallClicked);
        this.hostRole = hostRole;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public void setup(){
        game.setup();
    }

    public void runGame() throws IOException {
        currentPhase = hostRole == Role.ATTACKER ? Phase.HOST_TURN : Phase.CLIENT_TURN;
        displayGameState(false);
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
                while (currentPhase == Phase.CLIENT_TURN) {
                    String json = in.readLine();  // read JSON from client
                    if (json == null) continue;
                    ClientMove move = gson.fromJson(json, ClientMove.class);
                    if (move != null) {
                        processMove(move);

                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    e.printStackTrace();
                }
            }
        });

        listenerThread.start();
    }

    private void processMove(ClientMove move) throws IOException {
        Card card = move.card();
        Wall wall = game.board().getWalls()[move.wallIndex()];

        if (currentPhase == Phase.CLIENT_TURN) {
            PlayResult result = wall.playCard(card, hostRole == Role.DEFENDER);
            if (result.getResultType() == PlayResult.Type.SUCCESS) {
                game.discard().addAll(result.getToDiscard());
                getClient().getHand().remove(card);
                getClient().draw(game.deck());
                game.declareControl();
                getClient().setUseCauldron(false);
                currentPhase = Phase.HOST_TURN;
                displayGameState(hostRole == Role.DEFENDER);
            } else if (result.getResultType() == PlayResult.Type.ACTION) {
                List<Card> toDiscard = result.getToDiscard();
                if (!toDiscard.isEmpty()) {
                    game.discard().addAll(toDiscard);
                    if (hostRole == Role.ATTACKER) {
                        game.defender().setUseCauldron(true);
                    }
                    displayGameState(false);
                }
            }
        }
    }

    private Player getClient() {
        return hostRole == Role.DEFENDER ? game.attacker() : game.defender();
    }

    private Player getHost() {
        return hostRole == Role.ATTACKER ? game.attacker() : game.defender();
    }

    private GameState createGameState(boolean checkDeck) {
        return new GameState(getHost().getHand().getCards(),
                getClient().getHand().getCards(),
                game.board().getWalls(),
                game.deck().size(),
                game.discard().getCardsByColor(),
                currentPhase == Phase.CLIENT_TURN,
                game.defender().getCauldronCount(),
                game.defender().hasUsedCauldron(),
                hostRole == Role.DEFENDER,
                game.getWinner(checkDeck));
    }

    public boolean displayGameState(boolean checkDeck) throws IOException {
        GameState state = createGameState(checkDeck);
        gameView = new GameView(state, this::onWallClicked);
        HostGUI.displayGameState();
        String json = gson.toJson(state);
        out.println(json);

        if (state.getWinner() != Winner.NONE) {
            currentPhase = Phase.GAME_OVER;
            SwingUtilities.invokeLater(() -> gameView.displayWinner(state.getWinner()));
            return true;
        }

        return false;
    }

    public void onWallClicked(Wall wall) {
        if (currentPhase == Phase.HOST_TURN) {
            Card card = getSelectedCard();
            if (card != null) {
                PlayResult result = wall.playCard(card, hostRole == Role.ATTACKER);
                if (result.getResultType() == PlayResult.Type.SUCCESS) {
                    game.discard().addAll(result.getToDiscard());
                    getHost().getHand().remove(card);
                    gameView.unselectCard();
                    getHost().draw(game.deck());
                    game.declareControl();
                    getHost().setUseCauldron(false);
                    currentPhase = Phase.CLIENT_TURN;
                    try {
                        if (displayGameState(hostRole == Role.ATTACKER)) {
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (result.getResultType() == PlayResult.Type.ACTION) {
                    List<Card> toDiscard = result.getToDiscard();
                    if (!toDiscard.isEmpty()) {
                        game.discard().addAll(toDiscard);
                        if (hostRole == Role.DEFENDER) {
                            game.defender().setUseCauldron(true);
                        }
                        try {
                            displayGameState(false);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                waitForClientMove();
            }
        } else {
            HostGUI.notYourTurn();
        }
    }

    public GameView getGameView() {
        return gameView;
    }
}
