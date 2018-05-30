package byog.Core;

import byog.TileEngine.Tileset;

import java.io.Serializable;

public class LightSource implements Serializable {
    public LightSource(Game game) {
        for(int i = 0; i < 16; i++) {
            placeRandom(game);
        }
    }

    private void placeRandom(Game game) {
        while (true) {
            int x = RandomUtils.uniform(game.getRandom(), 0, game.WIDTH);
            int y = RandomUtils.uniform(game.getRandom(), 0, game.HEIGHT);
            if (game.getFinalWorldFrame()[x][y].equals(Tileset.FLOOR)) {
                game.setFinalWorldFrame(x, y, Tileset.FLOWER);
                return;
            }
        }
    }
}
