import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Main {
    // константи для розмірів, можна погратись
    private static final int ROWS = 50;
    private static final int COLS = 50;
    private static final int CELL_SIZE = 10;
    private static final double POPULATION_RATE = 0.2;
    private static final int TIMER_DELAY = 100; // milliseconds

    private static boolean[][] grid = new boolean[ROWS][COLS];

    public static void main(String[] args) {
        randomizeGrid();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (int r = 0; r < ROWS; r++) {
                    for (int c = 0; c < COLS; c++) {
                        if (grid[r][c]) {
                            g.setColor(Color.BLACK);
                        } else {
                            g.setColor(Color.WHITE);
                        }
                        g.fillRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                        g.setColor(Color.LIGHT_GRAY);
                        g.drawRect(c * CELL_SIZE, r * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }
        };
        canvas.setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        frame.add(canvas, BorderLayout.CENTER);

        JButton restartButton = new JButton("Restart");
        frame.getRootPane().setDefaultButton(restartButton);
        // restartButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        // restartButton.setBorderPainted(false);
        // restartButton.setOpaque(true);
        restartButton.addActionListener(e -> {
            randomizeGrid();
            canvas.repaint();
        });
        frame.add(restartButton, BorderLayout.SOUTH);

        Timer timer = new Timer(TIMER_DELAY, _ -> {
            calculateNextGeneration();
            canvas.repaint();
        });
        timer.start();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void randomizeGrid() {
        Random rand = new Random();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = rand.nextDouble() < POPULATION_RATE;
            }
        }
    }

    private static void calculateNextGeneration() {
        boolean[][] nextGrid = new boolean[ROWS][COLS];

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int liveNeighbors = countLiveNeighbors(r, c);
                boolean isAlive = grid[r][c];

                if (isAlive && (liveNeighbors == 2 || liveNeighbors == 3)) {
                    nextGrid[r][c] = true;
                } else if (!isAlive && liveNeighbors == 3) {
                    nextGrid[r][c] = true;
                } else {
                    nextGrid[r][c] = false;
                }
            }
        }
        grid = nextGrid;
    }

    private static int countLiveNeighbors(int row, int col) {
        int count = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (r == row && c == col) continue;
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
                    if (grid[r][c]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}