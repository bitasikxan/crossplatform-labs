import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JFrame {

    private static final int GRID_SIZE = 10;
    private static final int CELL_SIZE = 40;

    // logic
    private static final int EMPTY = 0;
    private static final int SHIP = 1;
    private static final int MISS = 2;
    private static final int HIT = 3;
    private static final int SUNK = 4;

    // palette
    private final Color COL_WATER = new Color(41, 128, 185);
    private final Color COL_PLAYER_SHIP = new Color(52, 73, 94);
    private final Color COL_BORDER = new Color(30, 30, 30);
    private final Color COL_MISS = new Color(189, 195, 199);
    private final Color COL_HIT = new Color(231, 76, 60);
    private final Color COL_SUNK = new Color(44, 44, 44);
    private final Color COL_REVEALED = new Color(243, 156, 18);

    private final int[][] playerBoard = new int[GRID_SIZE][GRID_SIZE];
    private final int[][] aiBoard = new int[GRID_SIZE][GRID_SIZE];
    private final JButton[][] playerButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private final JButton[][] aiButtons = new JButton[GRID_SIZE][GRID_SIZE];

    private final List<Point> aiPotentialTargets = new ArrayList<>();
    private final Random random = new Random();
    private final JLabel statusLabel;

    private boolean playerTurn;
    private boolean gameEnded = false;
    private int playerShipsSunk = 0;
    private int aiShipsSunk = 0;

    public Main() {
        setTitle("Sea Battle Lab #8");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // Status Panel (Top)
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(45, 45, 45));
        statusLabel = new JLabel("Welcome to Battleship!");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Game Boards Panel (Center)
        JPanel gamePanel = new JPanel(new GridLayout(1, 2, 30, 0));
        gamePanel.setBackground(new Color(60, 60, 60));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gamePanel.add(createBoardPanel("YOUR FLEET", playerButtons, false));
        gamePanel.add(createBoardPanel("ENEMY WATERS", aiButtons, true));
        add(gamePanel, BorderLayout.CENTER);

        // Control Panel (Bottom)
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(45, 45, 45));
        JButton restartBtn = new JButton("New Game");
        styleControlButton(restartBtn);
        restartBtn.addActionListener(_ -> startNewGame());
        controlPanel.add(restartBtn);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        startNewGame();
    }

    static void main() {
        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }

    private JPanel createBoardPanel(String title, JButton[][] buttons, boolean isEnemy) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setOpaque(false);

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        wrapper.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        grid.setPreferredSize(new Dimension(GRID_SIZE * CELL_SIZE, GRID_SIZE * CELL_SIZE));
        grid.setBackground(COL_BORDER);

        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                JButton btn = createGridButton(isEnemy, r, c);
                buttons[r][c] = btn;
                grid.add(btn);
            }
        }
        wrapper.add(grid, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton createGridButton(boolean isEnemy, int r, int c) {
        JButton btn = new JButton();
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(COL_BORDER, 1));
        btn.setBackground(COL_WATER);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);

        if (isEnemy) {
            final int row = r, col = c;
            btn.addActionListener(_ -> handlePlayerShot(row, col));
        }
        return btn;
    }

    private void styleControlButton(JButton btn) {
        btn.setBackground(new Color(70, 70, 70));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
    }

    private void startNewGame() {
        clearBoard(playerBoard);
        clearBoard(aiBoard);
        aiPotentialTargets.clear();
        playerShipsSunk = 0;
        aiShipsSunk = 0;
        gameEnded = false;

        resetButtons(playerButtons);
        resetButtons(aiButtons);

        placeShipsRandomly(playerBoard);
        placeShipsRandomly(aiBoard);

        // Visualize player ships
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (playerBoard[r][c] == SHIP) {
                    playerButtons[r][c].setBackground(COL_PLAYER_SHIP);
                }
            }
        }

        playerTurn = random.nextBoolean();
        statusLabel.setText(playerTurn ? "Your turn!" : "AI is thinking...");
        if (!playerTurn) triggerAiWithDelay();
    }

    private void handlePlayerShot(int r, int c) {
        if (gameEnded || !playerTurn || aiBoard[r][c] > SHIP) return;

        if (aiBoard[r][c] == SHIP) {
            aiBoard[r][c] = HIT;
            updateButton(aiButtons[r][c], COL_HIT, "X");
            if (checkIfSunk(aiBoard, r, c)) {
                markSunk(aiBoard, aiButtons, r, c);
                aiShipsSunk++;
            }
            if (aiShipsSunk == 10) {
                gameEnded = true;
                statusLabel.setText("VICTORY!");
                revealEnemyShips();
            }
        } else {
            aiBoard[r][c] = MISS;
            updateButton(aiButtons[r][c], COL_MISS, "•");
            playerTurn = false;
            statusLabel.setText("AI Turn...");
            triggerAiWithDelay();
        }
    }

    private void triggerAiWithDelay() {
        Timer t = new Timer(700, _ -> aiTurn());
        t.setRepeats(false);
        t.start();
    }

    private void aiTurn() {
        if (gameEnded || playerTurn) return;

        Point shot;
        if (!aiPotentialTargets.isEmpty()) {
            shot = aiPotentialTargets.removeFirst();
        } else {
            do {
                shot = new Point(random.nextInt(GRID_SIZE), random.nextInt(GRID_SIZE));
            } while (playerBoard[shot.x][shot.y] > SHIP);
        }

        int r = shot.x, c = shot.y;
        if (playerBoard[r][c] == SHIP) {
            playerBoard[r][c] = HIT;
            updateButton(playerButtons[r][c], COL_HIT, "X");
            addPotentialTargets(r, c);
            if (checkIfSunk(playerBoard, r, c)) {
                markSunk(playerBoard, playerButtons, r, c);
                playerShipsSunk++;
                // Clean up targets that are now marked as sunk/miss
                aiPotentialTargets.removeIf(p -> playerBoard[p.x][p.y] == SUNK || playerBoard[p.x][p.y] == MISS);
            }
            if (playerShipsSunk == 10) {
                gameEnded = true;
                statusLabel.setText("DEFEAT!");
                revealEnemyShips();
            } else {
                triggerAiWithDelay();
            }
        } else {
            playerBoard[r][c] = MISS;
            updateButton(playerButtons[r][c], COL_MISS, "•");
            playerTurn = true;
            statusLabel.setText("Your Turn!");
        }
    }

    private void updateButton(JButton btn, Color bg, String text) {
        btn.setBackground(bg);
        btn.setText(text);
        btn.setForeground(Color.WHITE);
    }

    // --- AI Logic Helpers ---
    private void addPotentialTargets(int r, int c) {
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nr < GRID_SIZE && nc >= 0 && nc < GRID_SIZE && playerBoard[nr][nc] <= SHIP) {
                Point p = new Point(nr, nc);
                if (!aiPotentialTargets.contains(p)) aiPotentialTargets.addFirst(p);
            }
        }
    }

    private boolean checkIfSunk(int[][] board, int r, int c) {
        List<Point> parts = new ArrayList<>();
        findShipParts(board, r, c, parts, new boolean[GRID_SIZE][GRID_SIZE]);
        for (Point p : parts) if (board[p.x][p.y] == SHIP) return false;
        return true;
    }

    private void findShipParts(int[][] board, int r, int c, List<Point> parts, boolean[][] visited) {
        if (r < 0 || r >= GRID_SIZE || c < 0 || c >= GRID_SIZE || visited[r][c] || board[r][c] == EMPTY || board[r][c] == MISS)
            return;
        visited[r][c] = true;
        parts.add(new Point(r, c));
        findShipParts(board, r + 1, c, parts, visited);
        findShipParts(board, r - 1, c, parts, visited);
        findShipParts(board, r, c + 1, parts, visited);
        findShipParts(board, r, c - 1, parts, visited);
    }

    private void markSunk(int[][] board, JButton[][] buttons, int r, int c) {
        List<Point> parts = new ArrayList<>();
        findShipParts(board, r, c, parts, new boolean[GRID_SIZE][GRID_SIZE]);
        for (Point p : parts) {
            board[p.x][p.y] = SUNK;
            updateButton(buttons[p.x][p.y], COL_SUNK, "#");
            // Mark surrounding cells as MISS
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int ni = p.x + i, nj = p.y + j;
                    if (ni >= 0 && ni < GRID_SIZE && nj >= 0 && nj < GRID_SIZE && board[ni][nj] == EMPTY) {
                        board[ni][nj] = MISS;
                        updateButton(buttons[ni][nj], COL_MISS, "•");
                    }
                }
            }
        }
    }

    // --- Ship Placement Logic ---
    private void placeShipsRandomly(int[][] board) {
        int[] ships = {4, 3, 3, 2, 2, 2, 1, 1, 1, 1};
        for (int size : ships) {
            boolean placed = false;
            while (!placed) {
                int r = random.nextInt(GRID_SIZE), c = random.nextInt(GRID_SIZE);
                boolean hor = random.nextBoolean();
                if (canPlace(board, r, c, size, hor)) {
                    for (int i = 0; i < size; i++) board[hor ? r : r + i][hor ? c + i : c] = SHIP;
                    placed = true;
                }
            }
        }
    }

    private boolean canPlace(int[][] board, int r, int c, int size, boolean hor) {
        if (hor && c + size > GRID_SIZE) return false;
        if (!hor && r + size > GRID_SIZE) return false;
        for (int i = 0; i < size; i++) {
            int cr = hor ? r : r + i, cc = hor ? c + i : c;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = cr + dr, nc = cc + dc;
                    if (nr >= 0 && nr < GRID_SIZE && nc >= 0 && nc < GRID_SIZE && board[nr][nc] != EMPTY) return false;
                }
            }
        }
        return true;
    }

    // --- UI State Management ---
    private void clearBoard(int[][] board) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) board[i][j] = EMPTY;
        }
    }

    private void resetButtons(JButton[][] buttons) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                buttons[i][j].setBackground(COL_WATER);
                buttons[i][j].setText("");
            }
        }
    }

    private void revealEnemyShips() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (aiBoard[r][c] == SHIP) aiButtons[r][c].setBackground(COL_REVEALED);
            }
        }
    }
}