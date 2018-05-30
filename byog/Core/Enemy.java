package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class Enemy implements Serializable {

    private TETile dispTile;
    private int x;
    private int y;

    public Enemy(Game game) {
        dispTile = TETile.colorVariant(Tileset.MOUNTAIN, 150,150,150, Game.getRandom());
        placeRandom(game);
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

    private boolean move(Game game, Direction dir) {
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
            return true;
        }
        else if (game.getFinalWorldFrame()[newX][newY].equals(Player.getDispTile())) {
            game.setLost(true);
            return true;
        }
        else if (game.getFinalWorldFrame()[newX][newY].equals(Tileset.MOUNTAIN)) {
            return true;
        } else {
            if (move(game, Direction.getRandNormDirection(dir))) {
                return true;
            }
            return false;
        }

    }

    public void pathFindStep(Game g, Player p){
        int xdiff = p.getX() - this.x;
        int ydiff = p.getY() - this.y;
        int mag = (int) (Math.sqrt(Math.pow(xdiff,2) + Math.pow(ydiff,2)));

        Direction[] yOrder = new Direction[2];
        Direction[] xOrder = new Direction[2];
        if (xdiff <= 0) {
            xOrder[0] = Direction.WEST;
            xOrder[1] = Direction.EAST;
        } else {
            xOrder[1] = Direction.WEST;
            xOrder[0] = Direction.EAST;
        }

        if (ydiff <= 0) {
            yOrder[0] = Direction.SOUTH;
            yOrder[1] = Direction.NORTH;
        } else {
            yOrder[1] = Direction.SOUTH;
            yOrder[0] = Direction.NORTH;
        }

        Direction[] dirOrder = new Direction[4];

        if (Math.abs(xdiff/mag) <= Math.abs(ydiff/mag)) {
            dirOrder[0] = yOrder[0];
            dirOrder[1] = yOrder[1];
            dirOrder[2] = xOrder[0];
            dirOrder[3] = xOrder[1];
        } else {
            dirOrder[0] = xOrder[0];
            dirOrder[1] = xOrder[1];
            dirOrder[2] = yOrder[0];
            dirOrder[3] = yOrder[1];
        }

        for (Direction dir : dirOrder) {
            boolean moved = move(g, dir);
            if (moved) {
                break;
            }
        }

    }


}
