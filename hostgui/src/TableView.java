import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class TableView extends JPanel {
    private final BoardView boardView;
    private final DeckView deckView;
    private final DiscardView discardView;

    public TableView(Wall[] walls, int deckSize, Map<CardColor, List<Card>> discard, Consumer<Wall> onWallClicked, boolean hostIsAttacker) {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        boardView = new BoardView(walls, onWallClicked, hostIsAttacker);
        deckView = new DeckView(deckSize);
        discardView = new DiscardView(discard);

        add(Box.createHorizontalGlue());
        add(deckView);
        add(Box.createHorizontalGlue());
        add(boardView);
        add(Box.createHorizontalGlue());
        add(discardView);
        add(Box.createHorizontalStrut(20));
    }
}
