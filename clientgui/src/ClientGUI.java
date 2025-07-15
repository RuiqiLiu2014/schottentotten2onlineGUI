import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ClientGUI {
    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static JFrame mainFrame;
    private static GameState state;
    private static GameView gameView;

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter host IP: ");
        String hostIP = scan.nextLine().trim();
        socket = new Socket(hostIP, 12345);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        listenForGameState();
        mainFrame = new JFrame("Schotten Totten 2 (client)");
        mainFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainFrame.add(new JLabel("Host is choosing role"));
        mainFrame.setVisible(true);
        listenForGameState();
    }

    private static void listenForGameState() {
        new Thread(() -> {
            try {
                while (true) {
                    Object obj = in.readObject();
                    if (obj instanceof GameState) {
                        state = (GameState) obj;
                        gameView = new GameView(state, ClientGUI::onWallClicked);
                        updateUI();
                    } else if (obj instanceof GameOverMessage) {
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainFrame, ((GameOverMessage) obj).getWinner() + " " + "wins!", "Game Over", JOptionPane.INFORMATION_MESSAGE));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void updateUI() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(gameView);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void stop() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public static void onWallClicked(Wall wall) {
        Card card = gameView.getSelectedCard();
        if (card != null) {
            if (state.isClientTurn()) {
                ClientMove move = new ClientMove(card, wall.getWallIndex());
                gameView.unselectCard();
                try {
                    System.out.println(move);
                    out.writeObject(move);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "It's not your turn", "ur bad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

