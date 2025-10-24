package pacmangame;

import java.awt.Color;

public class GameConfig {
    public static final int TILE_SIZE = 30;
    public static final int GRID_WIDTH = 21;
    public static final int GRID_HEIGHT = 21;
    public static final int TARGET_FPS = 60;
    public static final int GAME_SPEED = 1000 / TARGET_FPS;
    
    public static final int POWER_UP_DURATION = 5000;
    public static final int BERRY_RESPAWN_TIME = 60000;
    public static final Color POWER_UP_GHOST_COLOR = Color.BLUE;
    
    public static final int[][] MAZE = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,0,1},
        {1,0,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,0,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,0,1},
        {1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
        {1,1,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,1,1},
        {0,0,0,0,1,0,1,0,0,0,0,0,0,0,1,0,1,0,0,0,0},
        {1,1,1,1,1,0,1,0,1,1,0,1,1,0,1,0,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,1,0,2,0,1,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1},
        {0,0,0,0,1,0,1,0,0,0,0,0,0,0,1,0,1,0,0,0,0},
        {1,1,1,1,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
        {1,0,1,1,1,0,1,1,1,0,1,0,1,1,1,0,1,1,1,0,1},
        {1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,1},
        {1,1,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,0,1,1,1},
        {1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1},
        {1,0,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    public static final int[][] BERRY_POSITIONS = {
        {1, 1},   
        {19, 1},  
        {1, 19},  
        {19, 19}, 
        {10, 10}  
    };
    
    public static final int[] PACMAN_SPAWN = {1, 1};
    public static final int[][] GHOST_SPAWNS = {
        {9, 7}, {10, 7}, {11, 7}, {10, 8}
    };
    
    public static boolean isValidPosition(int x, int y) {
        if (x < 0 || x >= GRID_WIDTH || y < 0 || y >= GRID_HEIGHT) {
            return false;
        }
        return MAZE[y][x] != 1;
    }
}