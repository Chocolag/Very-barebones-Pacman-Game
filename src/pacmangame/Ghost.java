package pacmangame;

import java.awt.*;
import java.util.Random;

public class Ghost {
    private int x, y;
    private int dx, dy;
    private Color color;
    private Color originalColor;
    private Random random;
    private int moveCounter;
    private float pixelX, pixelY;
    private final float SPEED = 2.5f; 
    private boolean isFleeing = false;
    private long powerUpEndTime = 0;
    private boolean isMoving = true;
    private long lastUpdateTime;
    
    public Ghost(int startX, int startY, Color color) {
        int spawnIndex = -1;
        for (int i = 0; i < GameConfig.GHOST_SPAWNS.length; i++) {
            if (GameConfig.GHOST_SPAWNS[i][0] == startX && GameConfig.GHOST_SPAWNS[i][1] == startY) {
                spawnIndex = i;
                break;
            }
        }
        
        if (spawnIndex != -1) {
            this.x = GameConfig.GHOST_SPAWNS[spawnIndex][0];
            this.y = GameConfig.GHOST_SPAWNS[spawnIndex][1];
        } else {
            this.x = GameConfig.GHOST_SPAWNS[0][0];
            this.y = GameConfig.GHOST_SPAWNS[0][1];
        }
        
        this.pixelX = this.x * GameConfig.TILE_SIZE;
        this.pixelY = this.y * GameConfig.TILE_SIZE;
        this.color = color;
        this.originalColor = color;
        this.random = new Random();
        this.lastUpdateTime = System.nanoTime();
        chooseValidDirection();
    }
    
    private void chooseValidDirection() {
        int[][] directions = {{1,0}, {-1,0}, {0,1}, {0,-1}};
        
        
        for (int i = 0; i < directions.length; i++) {
            int randomIndex = random.nextInt(directions.length);
            int[] temp = directions[i];
            directions[i] = directions[randomIndex];
            directions[randomIndex] = temp;
        }
        
        
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (GameConfig.isValidPosition(newX, newY)) {
                dx = dir[0];
                dy = dir[1];
                moveCounter = random.nextInt(8) + 6;
                isMoving = true;
                return;
            }
        }
        
        dx = 0;
        dy = 0;
        isMoving = false;
    }
    
    private boolean isAlignedWithGrid() {
        return Math.abs(pixelX - x * GameConfig.TILE_SIZE) < 1 && 
               Math.abs(pixelY - y * GameConfig.TILE_SIZE) < 1;
    }
    
    public void move() {
        if (!isMoving) {
            if (random.nextInt(20) == 0) {
                chooseValidDirection();
            }
            return;
        }
        
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0f;
        lastUpdateTime = currentTime;
        
        
        updatePowerUpState();
        
        moveCounter--;
        
        if (moveCounter <= 0 || (random.nextInt(100) < 15 && isAlignedWithGrid())) {
            chooseValidDirection();
        }
        
        
        float movement = SPEED * deltaTime * 60.0f;
        
        
        float newPixelX = pixelX + dx * movement;
        float newPixelY = pixelY + dy * movement;
        
        
        float centerX = newPixelX + GameConfig.TILE_SIZE / 2.0f;
        float centerY = newPixelY + GameConfig.TILE_SIZE / 2.0f;
        
        
        int targetGridX = (int)(centerX / GameConfig.TILE_SIZE);
        int targetGridY = (int)(centerY / GameConfig.TILE_SIZE);
        
        if (GameConfig.isValidPosition(targetGridX, targetGridY)) {
            pixelX = newPixelX;
            pixelY = newPixelY;
            
            
            int newGridX = (int)((pixelX + GameConfig.TILE_SIZE / 2.0f) / GameConfig.TILE_SIZE);
            int newGridY = (int)((pixelY + GameConfig.TILE_SIZE / 2.0f) / GameConfig.TILE_SIZE);
            
            if (newGridX != x || newGridY != y) {
                x = newGridX;
                y = newGridY;
            }
        } else {
            pixelX = x * GameConfig.TILE_SIZE;
            pixelY = y * GameConfig.TILE_SIZE;
            chooseValidDirection();
        }
    }
    
    private void updatePowerUpState() {
        if (isFleeing && System.currentTimeMillis() > powerUpEndTime) {
            isFleeing = false;
            color = originalColor;
        }
    }
    
    public void activatePowerUp() {
        isFleeing = true;
        color = GameConfig.POWER_UP_GHOST_COLOR;
        powerUpEndTime = System.currentTimeMillis() + GameConfig.POWER_UP_DURATION;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        g2d.setColor(color);
        int drawX = (int)pixelX + 2;
        int drawY = (int)pixelY + 2;
        int size = GameConfig.TILE_SIZE - 4;
        
        
        g2d.fillRoundRect(drawX, drawY, size, size, 15, 15);
        
        
        int[] xPoints = {drawX, drawX + size/4, drawX + size/2, drawX + 3*size/4, drawX + size};
        int[] yPoints = {drawY + size, drawY + 3*size/4, drawY + size, drawY + 3*size/4, drawY + size};
        g2d.fillPolygon(xPoints, yPoints, 5);
        
        
        g2d.setColor(Color.WHITE);
        g2d.fillOval(drawX + 6, drawY + 6, 8, 8);
        g2d.fillOval(drawX + size - 14, drawY + 6, 8, 8);
        
        g2d.setColor(Color.BLACK);
        int leftPupilX = drawX + 8;
        int rightPupilX = drawX + size - 12;
        int pupilY = drawY + 8;
        
        if (dx > 0) { 
            leftPupilX += 2;
            rightPupilX += 2;
        } else if (dx < 0) { 
            leftPupilX -= 2;
            rightPupilX -= 2;
        } else if (dy > 0) { 
            pupilY += 2;
        } else if (dy < 0) { 
            pupilY -= 2;
        }
        
        g2d.fillOval(leftPupilX, pupilY, 4, 4);
        g2d.fillOval(rightPupilX, pupilY, 4, 4);
        
        
        if (isFleeing && (powerUpEndTime - System.currentTimeMillis()) < 2000) {
            long timeLeft = powerUpEndTime - System.currentTimeMillis();
            if ((timeLeft / 200) % 2 == 0) {
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(drawX, drawY, size, size, 15, 15);
                g2d.fillPolygon(xPoints, yPoints, 5);
            }
        }
    }
    
    public int getGridX() { return x; }
    public int getGridY() { return y; }
    public float getPixelX() { return pixelX; }
    public float getPixelY() { return pixelY; }
    public boolean isFleeing() { return isFleeing; }
    
    public void respawn() {
        this.x = GameConfig.GHOST_SPAWNS[0][0];
        this.y = GameConfig.GHOST_SPAWNS[0][1];
        this.pixelX = this.x * GameConfig.TILE_SIZE;
        this.pixelY = this.y * GameConfig.TILE_SIZE;
        chooseValidDirection();
    }
}