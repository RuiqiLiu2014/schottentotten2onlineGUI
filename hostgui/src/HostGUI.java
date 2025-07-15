import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Scanner;

public class HostGUI {
    private static ServerSocket serverSocket;
    private static Socket clientSocket;

    private static GameController gameController;
    private static JFrame mainFrame;

    private static Role hostRole;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(12345);
        System.out.println("Waiting for client...");
        clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

        hostRole = chooseHostRole(true);
        gameController = new GameController(new Game(new Attacker(hostRole == Role.ATTACKER ? PlayerType.HOST : PlayerType.CLIENT), new Defender(hostRole == Role.ATTACKER ? PlayerType.CLIENT : PlayerType.HOST), new Board(), new Deck(), new Discard()), hostRole, out, in);

        mainFrame = new JFrame("Schotten Totten 2 (host)");
        mainFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        gameController.setup();
        gameController.displayGameState();
        gameController.runGame();
        mainFrame.setVisible(true);
    }

    public static void displayGameState() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(gameController.getGameView());
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void notYourTurn() {
        JOptionPane.showMessageDialog(mainFrame, "It's not your turn", "ur bad", JOptionPane.ERROR_MESSAGE);
    }

    public static Role chooseHostRole(boolean isFirstGame) {
        if (isFirstGame) {
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
        } else {
            while (true) {
                Scanner scan = new Scanner(System.in);
                System.out.print("Choose your role (attacker/defender/random/swap): ");
                String role = scan.nextLine().trim().toLowerCase();
                if ("attacker".startsWith(role)) {
                    return Role.ATTACKER;
                } else if ("defender".startsWith(role)) {
                    return Role.DEFENDER;
                } else if ("random".startsWith(role)) {
                    return Math.random() < 0.5 ? Role.ATTACKER : Role.DEFENDER;
                } else if ("swap".startsWith(role)) {
                    return hostRole == Role.ATTACKER ? Role.DEFENDER : Role.ATTACKER;
                } else {
                    System.out.println("Invalid role.");
                }
            }
        }
    }
}
