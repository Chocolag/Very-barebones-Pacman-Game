package pacmangame;

import java.awt.*;

public class Pacman {
    private int x, y;
    private int dx, dy;
    private int nextDx, nextDy;
    private int animationState = 0;
    private float pixelX, pixelY;
    private final float SPEED = 4.0f;
    private boolean isMoving = false;
    private long lastUpdateTime;
    
    public Pacman(int startX, int startY) {
        this.x = GameConfig.PACMAN_SPAWN[0];
        this.y = GameConfig.PACMAN_SPAWN[1];
        this.pixelX = this.x * GameConfig.TILE_SIZE;
        this.pixelY = this.y * GameConfig.TILE_SIZE;
        this.dx = 1;
        this.dy = 0;
        this.isMoving = true;
        this.lastUpdateTime = System.nanoTime();
    }
    
    public void setNextDirection(int dx, int dy) {
        this.nextDx = dx;
        this.nextDy = dy;
    }
    
    private boolean isAlignedWithGrid() {
        return Math.abs(pixelX - x * GameConfig.TILE_SIZE) < 1 && 
               Math.abs(pixelY - y * GameConfig.TILE_SIZE) < 1;
    }
    
    public void move() {
        if (!isMoving) return;
        
        long currentTime = System.nanoTime();
        float deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0f;
        lastUpdateTime = currentTime;
        

        float movement = SPEED * deltaTime * 30.0f; 
        

        if ((nextDx != 0 || nextDy != 0) && isAlignedWithGrid()) {
            int newGridX = x + nextDx;
            int newGridY = y + nextDy;
            if (GameConfig.isValidPosition(newGridX, newGridY)) {
                dx = nextDx;
                dy = nextDy;
            }
        }
        
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
        }
        
        animationState = (animationState + 1) % 2;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.YELLOW);
        
        
        int mouthAngle = 30 - Math.abs(animationState) * 15;
        int startAngle = 0;
        int arcAngle = 360 - mouthAngle * 2;
        

        if (dx > 0) { 
            startAngle = mouthAngle;
        } else if (dx < 0) { 
            startAngle = 180 + mouthAngle;
        } else if (dy > 0) {
            startAngle = 90 + mouthAngle;
        } else if (dy < 0) { 
            startAngle = 270 + mouthAngle;
        } else {
            startAngle = mouthAngle;
        }
        
        g2d.fillArc((int)pixelX + 2, (int)pixelY + 2, 
                   GameConfig.TILE_SIZE - 4, GameConfig.TILE_SIZE - 4, 
                   startAngle, arcAngle);
    }
    
    public int getGridX() { return x; }
    public int getGridY() { return y; }
    public float getPixelX() { return pixelX; }
    public float getPixelY() { return pixelY; }
    
    public int getDx() { return dx; }
    public int getDy() { return dy; }
}