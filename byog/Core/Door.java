package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Door {
    public Door(Game game) {
        placeRandom(game);
    }

    private void placeRandom(Game game) {
        while (true) {
            int x = RandomUtils.uniform(game.getRandom(), 0, game.WIDTH);
            int y = RandomUtils.uniform(game.getRandom(), 0, game.HEIGHT);
            if (game.getFinalWorldFrame()[x][y].equals(Tileset.WALL) && isReasonable(x, y, game)) {
                game.setFinalWorldFrame(x, y, Tileset.LOCKED_DOOR);
                //System.out.println(x);
                //System.out.println(y);
                return;
            }
        }
    }

    private boolean isReasonable(int x, int y, Game game) {
        try {
            TETile[][] world = game.getFinalWorldFrame();
            for (int i = x - 1; i <= x + 1; i++) {
                if (world[i][y] == Tileset.FLOOR) {
                    return true;
                }
            }
            for (int i = y - 1; i <= y + 1; i++) {
                if (world[x][i] == Tileset.FLOOR) {
                    return true;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }
}
