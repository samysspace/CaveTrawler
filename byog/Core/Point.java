package byog.Core;

import byog.TileEngine.TETile;

public class Point {
    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean inMapBorder(TETile[][] world) {
        int worldWidth = world.length;
        int worldHeight = world[0].length;
        if (this.x < 1 || this.y < 1) {
            return false;
        }
        if (this.y >= worldHeight - 1 || this.x >= worldWidth - 1) {
            return false;
        }
        return true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
