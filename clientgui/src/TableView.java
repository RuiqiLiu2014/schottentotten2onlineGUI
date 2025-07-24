import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

public class TableView extends JPanel {
    public TableView(Wall[] walls, int deckSize, Map<CardColor, List<Card>> discard, Consumer<Wall> onWallClicked, boolean isHostAttacker) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        BoardView boardView = new BoardView(walls, onWallClicked, isHostAttacker);
        DeckView deckView = new DeckView(deckSize);
        DiscardView discardView = new DiscardView(discard);

        add(Box.createHorizontalGlue());
        add(deckView);
        add(Box.createHorizontalGlue());
        add(boardView);
        add(Box.createHorizontalGlue());
        add(discardView);
        add(Box.createHorizontalStrut(20));
    }
}
