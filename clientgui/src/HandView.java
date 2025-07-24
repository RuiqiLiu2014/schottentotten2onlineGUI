import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class HandView extends JPanel {
    private CardContainer selectedCard = null;

    public HandView(Set<Card> cards, boolean isAttacker, int cauldronCount, boolean hasUsedCauldron, boolean isOpponent) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        setMaximumSize(new Dimension(Constants.WINDOW_WIDTH, Constants.CARD_HEIGHT));
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
