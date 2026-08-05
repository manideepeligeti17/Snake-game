import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

/**
 * Simple Snake Game built with Java Swing.
 *
 * How to run:
 *   javac SnakeGame.java
 *   java SnakeGame
 *
 * Controls: Arrow keys to move, R to restart after Game Over.
 */
public class SnakeGame extends JPanel implements ActionListener {

    private final int TILE_SIZE = 25;
    private final int GRID_WIDTH = 20;
    private final int GRID_HEIGHT = 20;
    private final int GAME_SPEED_MS = 120;

    private ArrayList<Point> snake;
    private Point food;
    private char direction = 'R'; // U, D, L, R
    private boolean running = false;
    private int score = 0;
    private Timer timer;
    private Random random = new Random();

    public SnakeGame() {
        setPreferredSize(new Dimension(TILE_SIZE * GRID_WIDTH, TILE_SIZE * GRID_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new SnakeKeyAdapter());
        startGame();
    }

    private void startGame() {
        snake = new ArrayList<>();
        snake.add(new Point(GRID_WIDTH / 2, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2 - 1, GRID_HEIGHT / 2));
        snake.add(new Point(GRID_WIDTH / 2 - 2, GRID_HEIGHT / 2));
        direction = 'R';
        score = 0;
        running = true;
        spawnFood();

        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(GAME_SPEED_MS, this);
        timer.start();
    }

    private void spawnFood() {
        int x, y;
        do {
            x = random.nextInt(GRID_WIDTH);
            y = random.nextInt(GRID_HEIGHT);
        } while (snake.contains(new Point(x, y)));
        food = new Point(x, y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                g.setColor(i == 0 ? new Color(0, 200, 0) : new Color(0, 140, 0));
                g.fillRoundRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, 6, 6);
            }

            // Score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Score: " + score, 10, 20);
        } else {
            gameOverScreen(g);
        }
    }

    private void gameOverScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        String msg = "GAME OVER";
        g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2 - 20);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        fm = g.getFontMetrics();
        String scoreMsg = "Score: " + score;
        g.drawString(scoreMsg, (getWidth() - fm.stringWidth(scoreMsg)) / 2, getHeight() / 2 + 10);

        String restartMsg = "Press R to Restart";
        g.drawString(restartMsg, (getWidth() - fm.stringWidth(restartMsg)) / 2, getHeight() / 2 + 35);
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead;

        switch (direction) {
            case 'U': newHead = new Point(head.x, head.y - 1); break;
            case 'D': newHead = new Point(head.x, head.y + 1); break;
            case 'L': newHead = new Point(head.x - 1, head.y); break;
            default:  newHead = new Point(head.x + 1, head.y); break;
        }

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            score += 10;
            spawnFood();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollisions() {
        Point head = snake.get(0);

        // Wall collision
        if (head.x < 0 || head.x >= GRID_WIDTH || head.y < 0 || head.y >= GRID_HEIGHT) {
            running = false;
        }

        // Self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }

    private class SnakeKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (!running && key == KeyEvent.VK_R) {
                startGame();
                return;
            }

            switch (key) {
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U';
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D';
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L';
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R';
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();

        frame.add(game);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        game.requestFocusInWindow();
    }
}
