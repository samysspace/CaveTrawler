package byog.Core;

import byog.TileEngine.TETile;

public interface WorldAsset {

    boolean hasGeneratedNeighbors();

    void draw(TETile[][] world);

    boolean canPlace(TETile[][] world);

    void generateNeighbors(TETile[][] world);

}
