import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.prefs.Preferences;

public class Panel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 20;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 75;
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    int bodyParts = 4;
    int applesEaten = 0;
    int appleX;
    int appleY;
    int highestScore = 0;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    Panel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.WHITE);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
        highestScore = loadHighestScore();
    }

    public void startGame() {
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        newApple();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX,appleY,UNIT_SIZE,UNIT_SIZE);

            for(int i = 0; i < bodyParts; i++)
                if(i == 0) {
                    g.setColor(Color.GREEN);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }

            // Displaying score
            g.setColor(Color.BLACK);
            g.setFont(new Font("Consolas", Font.PLAIN, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

            // Displaying the highest score
            g.setColor(Color.BLACK);
            g.setFont(new Font("Consolas", Font.PLAIN, 20));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("Highest Score: " + highestScore, (SCREEN_WIDTH - metrics2.stringWidth("Highest Score: " + highestScore)) - 5, g.getFont().getSize() + 15);
        }  else
            gameOver(g);
    }

    public void move() {
        for(int i = bodyParts; i > 0; --i) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void newApple() {
        appleX = random.nextInt((SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Checks if head collides with body
        for(int i = bodyParts; i > 0; --i)
            if((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }

        // Checks if head touches edges
        if(x[0] < 0)
            running = false;
        if(x[0] > SCREEN_WIDTH)
            running = false;
        if(y[0] < 0)
            running = false;
        if(y[0] > SCREEN_HEIGHT)
            running = false;

        if(!running)
            timer.stop();
    }
    // Save highest score

    private void saveHighestScore(int score) {
        Preferences prefs = Preferences.userNodeForPackage(Panel.class);
        prefs.putInt("highestScore", score);
    }

    // Load the highest score
    private int loadHighestScore() {
        Preferences prefs = Preferences.userNodeForPackage(Panel.class);
        return prefs.getInt("highestScore", 0);
    }

    public void gameOver(Graphics g) {
        // Showing the score after defeat
        g.setColor(Color.BLACK);
        g.setFont(new Font("Consolas", Font.PLAIN, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten))/2, g.getFont().getSize());

        // Game Over text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Consolas", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2 - 50);

        // Restart instruction
        g.setFont(new Font("Consolas", Font.PLAIN, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press 'R' to restart", (SCREEN_WIDTH - metrics3.stringWidth("Press 'R' to restart")) / 2, SCREEN_HEIGHT / 2 + 50);

        // Display previous and new highest scores
        int previousHighestScore = highestScore;
        if (applesEaten > highestScore) {
            highestScore = applesEaten;
            saveHighestScore(highestScore);
        }
        g.setColor(Color.BLACK);
        g.setFont(new Font("Consolas", Font.PLAIN, 20));
        g.drawString("Highest Score: " + previousHighestScore, (SCREEN_WIDTH - metrics3.stringWidth("Highest Score: " + previousHighestScore)) / 2, SCREEN_HEIGHT / 2 + 100);
        if(applesEaten > highestScore)
            g.drawString("New Highest Score: " + applesEaten, (SCREEN_WIDTH - metrics3.stringWidth("New Highest Score: " + highestScore)) / 2, SCREEN_HEIGHT / 2 + 130);
    }
    public void restartGame() {
        bodyParts = 4;
        applesEaten = 0;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        running = true;
        startGame();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT -> {
                    if (direction != 'R')
                        direction = 'L';
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != 'L')
                        direction = 'R';
                }
                case KeyEvent.VK_UP -> {
                    if (direction != 'D')
                        direction = 'U';
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != 'U')
                        direction = 'D';
                }
                case KeyEvent.VK_R -> {
                    if(!running)
                        restartGame();
                }
            }
        }
    }
}
