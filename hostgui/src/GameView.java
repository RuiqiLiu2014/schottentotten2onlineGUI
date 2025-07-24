import javax.swing.*;
import java.util.function.Consumer;

public class GameView extends JPanel {
    private final HandView hostHandView;

    public GameView(GameState gameState, Consumer<Wall> onWallClicked) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        hostHandView = new HandView(gameState.getHostHand(), !gameState.isClientAttacker(), gameState.getCauldronCount(), gameState.hasUsedCauldron(), false);
        HandView clientHandView = new HandView(gameState.getClientHand(), gameState.isClientAttacker(), gameState.getCauldronCount(), gameState.hasUsedCauldron(), true);
        TableView tableView = new TableView(gameState.getWalls(), gameState.getDeckSize(), gameState.getDiscard(), onWallClicked, !gameState.isClientAttacker());

        add(clientHandView);
        add(Box.createVerticalGlue());
        add(tableView);
        add(Box.createVerticalGlue());
        add(hostHandView);
    }

    public void displayWinner(Winner winner) {
        String message = switch (winner) {
            case ATTACKER -> "Attacker wins!";
            case DEFENDER -> "Defender wins!";
            case NONE -> null;
        };
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public Card getSelectedCard() {
        return hostHandView.getSelectedCard();
    }

    public void unselectCard() {
        hostHandView.unselectCard();
    }
}
