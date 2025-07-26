import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class HandView extends JPanel {
    private CardContainer selectedCard = null;
    private final boolean glowing;

    public HandView(Set<Card> cards, boolean isAttacker, int cauldronCount, boolean hasUsedCauldron, boolean isOpponent, boolean isTurn) {
        this.glowing = isTurn;
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.CARD_HEIGHT + Constants.POP_OFFSET));
        setMaximumSize(new Dimension(Constants.WINDOW_WIDTH, Constants.CARD_HEIGHT + Constants.POP_OFFSET));
        if (isOpponent) {
            for (int i = 0; i < cards.size(); i++) {
                add(new CardBackView());
            }
        } else {
            for (Card card : cards) {
                add(new CardContainer(card, this));
            }
        }

        if (isAttacker) {
            if (!isOpponent) {
                add(new CardContainer(Card.RETREAT, this));
            }
        } else if (!hasUsedCauldron) {
            for (int i = 0; i < cauldronCount; i++) {
                add(isOpponent ? new CardView(Card.CAULDRON) : new CardContainer(Card.CAULDRON, this));
            }
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (glowing && getComponentCount() > 0) {
            Rectangle glowRect = null;
            for (Component card : getComponents()) {
                Rectangle bounds = card.getBounds();
                if (glowRect == null) {
                    glowRect = new Rectangle(bounds);
                } else {
                    glowRect = glowRect.union(bounds);
                }
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Add some padding to make the glow extend beyond the cards
            glowRect.grow(Constants.POP_OFFSET, Constants.POP_OFFSET);

            // Create a radial glow gradient manually (or use simpler semi-transparent stroke)
            Color glowColor = new Color(255, 215, 0, 64);
            g2.setColor(glowColor);
            g2.fillRoundRect(glowRect.x, glowRect.y, glowRect.width, glowRect.height, 20, 20);

            g2.dispose();
        }
    }

    public void notifyCardClicked(CardContainer clickedCard) {
        if (selectedCard != null && selectedCard != clickedCard) {
            selectedCard.unPop();
        } else if (selectedCard == clickedCard) {
            unselectCard();
            return;
        }
        selectedCard = clickedCard;
    }

    public Card getSelectedCard() {
        return selectedCard == null ? null : selectedCard.getCard();
    }

    public void unselectCard() {
        selectedCard = null;
    }
}
