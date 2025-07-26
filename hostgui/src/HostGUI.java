import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class HostGUI {
    private static GameController gameController;
    private static JFrame mainFrame;

    private static Role hostRole;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Waiting for client...");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        hostRole = chooseHostRole();
        gameController = new GameController(new Game(new Player(), new Player(), new Board(), new Deck(), new Discard()), hostRole, clientSocket);

        mainFrame = new JFrame("Schotten Totten 2 (host)");
        mainFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Constants.resize(mainFrame.getWidth(), mainFrame.getHeight());
                gameController.updateGameView();
                displayGameState();
            }
        });

        while (true) {
            gameController.setup();
            gameController.displayGameState(false);
            gameController.runGame();
            mainFrame.setVisible(true);

            while (!gameController.gameOver()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if (gameController.switchSides()) {
                hostRole = hostRole == Role.ATTACKER ? Role.DEFENDER : Role.ATTACKER;
            }
            gameController = new GameController(new Game(new Player(), new Player(), new Board(), new Deck(), new Discard()), hostRole, clientSocket);
        }
    }

    public static void displayGameState() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(gameController.getGameView());
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static Role chooseHostRole() {
        while (true) {
            Scanner scan = new Scanner(System.in);
            System.out.print("Choose your role (attacker/defender/random): ");
            String role = scan.nextLine().trim().toLowerCase();
            if ("attacker".startsWith(role)) {
                return Role.ATTACKER;
            } else if ("defender".startsWith(role)) {
                return Role.DEFENDER;
            } else if ("random".startsWith(role)) {
                return Math.random() < 0.5 ? Role.ATTACKER : Role.DEFENDER;
            } else {
                System.out.println("Invalid role.");
            }
        }
    }
}
