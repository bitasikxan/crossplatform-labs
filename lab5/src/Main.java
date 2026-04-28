import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Random;

public class Main extends JFrame {
    private int coins;
    private boolean isPlayerTurn;
    private final Random random = new Random();
    private JLabel coinsCountLabel;
    private JLabel statusLabel;
    private JButton take1Btn;
    private JButton take2Btn;
    private JButton restartBtn;
    private final Font bigFont = new Font("Segoe UI", Font.BOLD, 72);
    private final Font mainFont = new Font("Segoe UI", Font.PLAIN, 18);
    private final Font boldFont = new Font("Segoe UI", Font.BOLD, 18);

    public Main() {
        setTitle("Гра в монети");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
        startGame();
    }

    static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ignored) {}
            new Main().setVisible(true);
        });
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        statusLabel = new JLabel("Підготовка до гри...", SwingConstants.CENTER);
        statusLabel.setFont(boldFont);
        mainPanel.add(statusLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Монет у купі:", SwingConstants.CENTER);
        titleLabel.setFont(mainFont);
        titleLabel.setForeground(Color.GRAY);

        coinsCountLabel = new JLabel("0", SwingConstants.CENTER);
        coinsCountLabel.setFont(bigFont);
        coinsCountLabel.setForeground(new Color(40, 50, 60));

        centerPanel.add(titleLabel);
        centerPanel.add(coinsCountLabel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(1, 3, 10, 0));

        take1Btn = createButton("Взяти 1", new Color(0, 123, 255));
        take2Btn = createButton("Взяти 2", new Color(23, 167, 184));
        restartBtn = createButton("Ще раз", new Color(40, 167, 67));
        restartBtn.setVisible(false);

        take1Btn.addActionListener(_ -> processPlayerMove(1));
        take2Btn.addActionListener(_ -> processPlayerMove(2));
        restartBtn.addActionListener(_ -> startGame());

        bottomPanel.add(take1Btn);
        bottomPanel.add(take2Btn);
        bottomPanel.add(restartBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void startGame() {
        coins = random.nextInt(16) + 15;
        isPlayerTurn = random.nextBoolean();

        updateUIState();
        restartBtn.setVisible(false);
        take1Btn.setVisible(true);
        take2Btn.setVisible(true);

        if (isPlayerTurn) {
            statusLabel.setText("Гра почалася! Ваш хід.");
            statusLabel.setForeground(new Color(0, 123, 255));
            toggleButtons(true);
        } else {
            statusLabel.setText("Комп'ютер ходить першим...");
            statusLabel.setForeground(new Color(220, 53, 69));
            toggleButtons(false);
            triggerAIMove();
        }
    }

    private void processPlayerMove(int amount) {
        if (amount > coins) amount = coins;
        coins -= amount;
        updateUIState();

        if (checkWin("Ви перемогли!")) return;

        isPlayerTurn = false;
        statusLabel.setText(String.format("Ви взяли %d. Хід комп'ютера...", amount));
        statusLabel.setForeground(new Color(220, 53, 69));
        toggleButtons(false);

        triggerAIMove();
    }

    private void triggerAIMove() {
        Timer timer = new Timer(500, _ -> {
            int take;

            if (coins % 3 == 1) take = 1;
            else if (coins % 3 == 2) take = 2;
            else take = random.nextBoolean() ? 1 : 2;
            // 123-125 also could be written like:
            // if (coins % 3 == 0) take = random.nextBoolean() : 1 ? 2;
            // else take = coins % 3;

            if (take > coins) take = coins;

            coins -= take;
            updateUIState();

            if (checkWin("Комп'ютер переміг!")) return;

            isPlayerTurn = true;
            statusLabel.setText(String.format("Комп'ютер взяв %d. Ваш хід!", take));
            statusLabel.setForeground(new Color(0, 123, 255));
            toggleButtons(true);
        });
        timer.setRepeats(false);
        timer.start();
    }

    private boolean checkWin(String winMessage) {
        if (coins <= 0) {
            statusLabel.setText(winMessage);
            statusLabel.setForeground(new Color(40, 167, 69));
            toggleButtons(false);
            take1Btn.setVisible(false);
            take2Btn.setVisible(false);
            restartBtn.setVisible(true);
            return true;
        }
        return false;
    }

    private void updateUIState() {
        coinsCountLabel.setText(String.valueOf(coins));
        if (isPlayerTurn && coins == 1) {
            take2Btn.setEnabled(false);
        }
    }

    private void toggleButtons(boolean enabled) {
        take1Btn.setEnabled(enabled);
        take2Btn.setEnabled(enabled && coins > 1);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(boldFont);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        return btn;
    }
}