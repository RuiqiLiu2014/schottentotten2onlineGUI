import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.*;
import java.io.*;
import java.util.Objects;
import java.util.Scanner;
import com.google.gson.Gson;

public class ClientGUI {
    private static Socket socket;
    private static final Gson gson = new Gson();
    private static JFrame mainFrame;
    private static GameState gameState;
    private static GameView gameView;

    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter host IP: ");
        String hostIP = scan.nextLine().trim();
        socket = new Socket(hostIP, 12345);

        mainFrame = new JFrame("Schotten Totten 2 (client)");
        mainFrame.setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Constants.resize(mainFrame.getWidth(), mainFrame.getHeight());
                if (gameView != null) {
                    gameView.updateLayout(ClientGUI::onWallClicked);
                }
                updateUI();
            }
        });

        mainFrame.add(new JLabel("Host is choosing role"));
        mainFrame.setVisible(true);
        listenForGameState();
    }

    private static void listenForGameState() {
        new Thread(() -> {
            try (InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            ) {
                String json;
                while ((json = reader.readLine()) != null) {
                    gameState = gson.fromJson(json, GameState.class);
                    gameView = new GameView(gameState, ClientGUI::onWallClicked);
                    updateUI();

                    if (gameState.getWinner() != Winner.NONE) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(mainFrame,
                                        gameState.getWinner() == Winner.ATTACKER ? "Attacker wins!" : "Defender wins!",
                                        "Game Over", JOptionPane.INFORMATION_MESSAGE));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void updateUI() {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(Objects.requireNonNullElseGet(gameView, () -> new JLabel("Host is choosing role")));
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public static void onWallClicked(Wall wall) {
        Card card = gameView.getSelectedCard();
        if (card != null) {
            if (gameState.isClientTurn()) {
                ClientMove move = new ClientMove(card, wall.getWallIndex());
                gameView.unselectCard();
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    String jsonMove = gson.toJson(move);
                    out.println(jsonMove);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

