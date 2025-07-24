import javax.swing.*;
import java.util.function.Consumer;

public class GameView extends JPanel {
    private final HandView clientHandView;

    public GameView(GameState gameState, Consumer<Wall> onWallClicked) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        HandView hostHandView = new HandView(gameState.getHostHand(), !gameState.isClientAttacker(), gameState.getCauldronCount(), gameState.getHasUsedCauldron(), true);
        clientHandView = new HandView(gameState.getClientHand(), gameState.isClientAttacker(), gameState.getCauldronCount(), gameState.getHasUsedCauldron(), false);
        TableView tableView = new TableView(gameState.getWalls(), gameState.getDeckSize(), gameState.getDiscard(), onWallClicked, !gameState.isClientAttacker());

        add(hostHandView);
        add(Box.createVerticalGlue());
        add(tableView);
        add(Box.createVerticalGlue());
        add(clientHandView);
    }

    public Card getSelectedCard() {
        return clientHandView.getSelectedCard();
    }

    public void unselectCard() {
        clientHandView.unselectCard();
    }
}
