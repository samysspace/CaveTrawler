package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class Player implements Serializable {
    private int steps = 0;
    private static TETile dispTile = Tileset.PLAYER;
    private static int spawnBreak = 10;
    private int x;
    private int y;
    private TETile[][] former;
    private boolean[][] isIlluminated;

    public Player() {

    }

    public Player(Game game) {
        placeRandom(game);
        former = new TETile[game.WIDTH][game.HEIGHT];
        isIlluminated = new boolean[game.WIDTH][game.HEIGHT];

    }

    private void placeRandom(Game game) {
        while (true) {
            x = RandomUtils.uniform(game.getRandom(), 0, game.WIDTH);
            y = RandomUtils.uniform(game.getRandom(), 0, game.HEIGHT);
            if (game.getFinalWorldFrame()[x][y].equals(Tileset.FLOOR)) {
                game.setFinalWorldFrame(x, y, dispTile);
                return;
            }
        }
    }


    public boolean move(Game game, Direction dir) {
        int newX = x;
        int newY = y;
        switch (dir) {
            case NORTH:
                newY += 1;
                break;
            case SOUTH:
                newY -= 1;
                break;
            case WEST:
                newX -= 1;
                break;
            case EAST:
                newX += 1;
                break;
            default:
                break;
        }
        if (game.getFinalWorldFrame()[newX][newY].equals(Tileset.FLOOR)) {
            game.setFinalWorldFrame(x, y, Tileset.FLOOR);
            game.setFinalWorldFrame(newX, newY, dispTile);

            x = newX;
            y = newY;

            if (game.getNumEnemies() < Game.MAXENEMIES && steps % spawnBreak == 0) {
                game.addEnemy(new Enemy(game));
            }
            steps += 1;

            return true;
        }
        else if (game.getFinalWorldFrame()[newX][newY].equals(Tileset.LOCKED_DOOR)) {
            game.setWin(true);
            return false;
        }
        else if (game.getFinalWorldFrame()[newX][newY].equals(Tileset.FLOWER)) {
            game.updateIllumination(newX, newY);
            game.setFinalWorldFrame(x, y, Tileset.FLOOR);
            game.setFinalWorldFrame(newX, newY, dispTile);
            x = newX;
            y = newY;
            if (game.getNumEnemies() < Game.MAXENEMIES && steps % spawnBreak == 0) {
                game.addEnemy(new Enemy(game));
            }
            steps += 1;

        }

        else if (game.getFinalWorldFrame()[newX][newY].equals(Tileset.MOUNTAIN)){
            game.setLost(true);
            return false;
        }

        return false;
    }

    public static TETile getDispTile(){
        return dispTile;
    }

    public int getX(){
        return x;
    }
    public int getY() {
        return  y;
    }


}
