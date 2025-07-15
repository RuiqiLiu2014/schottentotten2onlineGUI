import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class BoardView extends JPanel {
    private final Wall[] walls;
    private final Consumer<Wall> onWallClicked;

    public BoardView(Wall[] walls, Consumer<Wall> onWallClicked, boolean isHostAttacker) {
        this.walls = walls;
        this.onWallClicked = onWallClicked;
        int hgap = 15;
        setLayout(new FlowLayout(FlowLayout.CENTER, hgap, 0));
        setMaximumSize(new Dimension(Constants.NUM_WALLS * WallView.WALL_WIDTH + (Constants.NUM_WALLS - 1) * hgap, WallView.OVERALL_HEIGHT));
        for (Wall wall : walls) {
            add(new WallView(wall, onWallClicked, isHostAttacker));
        }
    }
}