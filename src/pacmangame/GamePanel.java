package pacmangame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.awt.Point;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer timer;
    private Pacman pacman;
    private ArrayList<Ghost> ghosts;
    private ArrayList<Point> dots;
    private ArrayList<Point> powerBerries;
    private int score;
    private boolean gameRunning;
    private boolean powerUpActive = false;
    private long powerUpEndTime = 0;
    private long lastFrameTime;
    private int fps;
    private long fpsLastCheck;
    private int frameCount;
    private long berryRespawnTime = 0;
    private boolean waitingForBerryRespawn = false;
    
    public GamePanel() {
        setPreferredSize(new Dimension(
            GameConfig.GRID_WIDTH * GameConfig.TILE_SIZE, 
            GameConfig.GRID_HEIGHT * GameConfig.TILE_SIZE
        ));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        setDoubleBuffered(true);
        
        initializeGame();
    }
    
    private void initializeGame() {
        pacman = new Pacman(GameConfig.PACMAN_SPAWN[0], GameConfig.PACMAN_SPAWN[1]);
        
        ghosts = new ArrayList<>();
        dots = new ArrayList<>();
        powerBerries = new ArrayList<>();
        score = 0;
        gameRunning = true;
        powerUpActive = false;
        lastFrameTime = System.nanoTime();
        fps = 0;
        fpsLastCheck = System.currentTimeMillis();
        frameCount = 0;
        waitingForBerryRespawn = false;
        
        for (int y = 0; y < GameConfig.GRID_HEIGHT; y++) {
            for (int x = 0; x < GameConfig.GRID_WIDTH; x++) {
                if (GameConfig.MAZE[y][x] == 0) {
                    dots.add(new Point(x, y));
                }
            }
        }
        
        initializeBerries();
        
        ghosts.add(new Ghost(9, 7, Color.RED));
        ghosts.add(new Ghost(10, 7, Color.PINK));
        ghosts.add(new Ghost(11, 7, Color.CYAN));
        ghosts.add(new Ghost(10, 8, Color.ORANGE));
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(GameConfig.GAME_SPEED, this);
        timer.start();
    }
    
    private void initializeBerries() {
        powerBerries.clear();
        for (int[] pos : GameConfig.BERRY_POSITIONS) {
            if (GameConfig.isValidPosition(pos[0], pos[1])) {
                powerBerries.add(new Point(pos[0], pos[1]));
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        
        drawMaze(g2d);
        drawDots(g2d);
        drawPowerBerries(g2d);
        pacman.draw(g2d);
        
        for (Ghost ghost : ghosts) {
            ghost.draw(g2d);
        }
        
        drawHUD(g2d);
        drawFPS(g2d);
        
        if (waitingForBerryRespawn) {
            drawBerryRespawnTimer(g2d);
        }
        
        if (!gameRunning) {
            drawGameOverMessage(g2d);
        }
        
        updateFPS();
    }
    
    private void drawMaze(Graphics g) {
        g.setColor(Color.BLUE);
        for (int y = 0; y < GameConfig.GRID_HEIGHT; y++) {
            for (int x = 0; x < GameConfig.GRID_WIDTH; x++) {
                if (GameConfig.MAZE[y][x] == 1) {
                    g.fillRect(x * GameConfig.TILE_SIZE, y * GameConfig.TILE_SIZE, 
                              GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                }
            }
        }
    }
    
    private void drawDots(Graphics g) {
        g.setColor(Color.WHITE);
        for (Point dot : dots) {
            g.fillOval(dot.x * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE/2 - 3, 
                      dot.y * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE/2 - 3, 6, 6);
        }
    }
    
    private void drawPowerBerries(Graphics g) {
        for (Point berry : powerBerries) {
            long currentTime = System.currentTimeMillis();
            int pulse = (int)(Math.sin(currentTime * 0.01) * 3 + 8);
            
            g.setColor(Color.MAGENTA);
            g.fillOval(berry.x * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE/2 - pulse/2, 
                      berry.y * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE/2 - pulse/2, 
                      pulse, pulse);
            
            g.setColor(Color.WHITE);
            g.fillOval(berry.x * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE/2 - pulse/4, 
                      berry.y * GameConfig.TILE_SIZE + GameConfig.TILE_SIZE/2 - pulse/4, 
                      pulse/3, pulse/3);
        }
    }
    
    private void drawHUD(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 25);
        
        if (powerUpActive) {
            long timeLeft = powerUpEndTime - System.currentTimeMillis();
            if (timeLeft > 0) {
                int secondsLeft = (int)(timeLeft / 1000) + 1;
                g.setColor(Color.MAGENTA);
                g.drawString("Power: " + secondsLeft + "s", 150, 25);
                
                if (timeLeft < 2000 && (timeLeft / 200) % 2 == 0) {
                    g.setColor(Color.RED);
                    g.drawString("Power: " + secondsLeft + "s", 150, 25);
                }
            }
        }
    }
    
    private void drawFPS(Graphics g) {
        g.setColor(Color.GREEN);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("FPS: " + fps, GameConfig.GRID_WIDTH * GameConfig.TILE_SIZE - 80, 20);
    }
    
    private void drawBerryRespawnTimer(Graphics g) {
        long timeLeft = berryRespawnTime - System.currentTimeMillis();
        if (timeLeft > 0) {
            int secondsLeft = (int)(timeLeft / 1000) + 1;
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            g.drawString("Berries respawn in: " + secondsLeft + "s", 
                       GameConfig.GRID_WIDTH * GameConfig.TILE_SIZE / 2 - 100, 
                       40);
        }
    }
    
    private void updateFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - fpsLastCheck >= 1000) {
            fps = frameCount;
            frameCount = 0;
            fpsLastCheck = currentTime;
        }
    }
    
    private void drawGameOverMessage(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String message = dots.isEmpty() && powerBerries.isEmpty() ? "YOU WIN!" : "GAME OVER";
        g.drawString(message, 
                    GameConfig.GRID_WIDTH * GameConfig.TILE_SIZE / 2 - 120, 
                    GameConfig.GRID_HEIGHT * GameConfig.TILE_SIZE / 2);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Press R to restart", 
                    GameConfig.GRID_WIDTH * GameConfig.TILE_SIZE / 2 - 60, 
                    GameConfig.GRID_HEIGHT * GameConfig.TILE_SIZE / 2 + 40);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameRunning) return;
        
        updatePowerUpState();
        
        if (waitingForBerryRespawn) {
            checkBerryRespawn();
        }
        
        pacman.move();
        
        Point pacmanPos = new Point(pacman.getGridX(), pacman.getGridY());
        if (dots.contains(pacmanPos)) {
            dots.remove(pacmanPos);
            score += 10;
        }
        
        if (powerBerries.contains(pacmanPos)) {
            powerBerries.remove(pacmanPos);
            score += 50;
            activatePowerUp();
            
            if (powerBerries.isEmpty() && !waitingForBerryRespawn) {
                startBerryRespawnTimer();
            }
        }
        
        for (Ghost ghost : ghosts) {
            ghost.move();
            
            if (ghost.getGridX() == pacman.getGridX() && ghost.getGridY() == pacman.getGridY()) {
                if (powerUpActive && ghost.isFleeing()) {
                    score += 200;
                    ghost.respawn();
                } else {
                    gameRunning = false;
                    timer.stop();
                }
            }
        }
        
        if (dots.isEmpty() && powerBerries.isEmpty() && !waitingForBerryRespawn) {
            gameRunning = false;
            timer.stop();
        }
        
        repaint();
    }
    
    private void checkBerryRespawn() {
        if (System.currentTimeMillis() >= berryRespawnTime) {
            initializeBerries();
            waitingForBerryRespawn = false;
        }
    }
    
    private void startBerryRespawnTimer() {
        waitingForBerryRespawn = true;
        berryRespawnTime = System.currentTimeMillis() + GameConfig.BERRY_RESPAWN_TIME;
    }
    
    private void updatePowerUpState() {
        if (powerUpActive && System.currentTimeMillis() > powerUpEndTime) {
            powerUpActive = false;
        }
    }
    
    private void activatePowerUp() {
        powerUpActive = true;
        powerUpEndTime = System.currentTimeMillis() + GameConfig.POWER_UP_DURATION;
        
        for (Ghost ghost : ghosts) {
            ghost.activatePowerUp();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                pacman.setNextDirection(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                pacman.setNextDirection(0, 1);
                break;
            case KeyEvent.VK_LEFT:
                pacman.setNextDirection(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                pacman.setNextDirection(1, 0);
                break;
            case KeyEvent.VK_R:
                if (!gameRunning) {
                    initializeGame();
                }
                break;
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
}