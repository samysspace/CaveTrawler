package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Room implements WorldAsset {

    private static final int MAXWIDTH = 8;
    private static final int MAXHEIGHT = 8;

    private static final int MINWIDTH = 3;
    private static final int MINHEIGHT = 3;

    private boolean generatedNeighbors = false;

    //Number of connections limited to sides of the room
    private WorldAsset[] connections = new WorldAsset[Direction.values().length];

    //Centralized to bottom left corner
    private Point location;

    private int width;

    private int height;

    public Room(Point anchor, Direction sourceDirection) {
        //Create a random room that contains the anchor point
        this.width = RandomUtils.uniform(Game.getRandom(), MINWIDTH, MAXWIDTH);
        this.height = RandomUtils.uniform(Game.getRandom(), MINHEIGHT, MAXHEIGHT);

        //randomly adjust room displacement from anchor
        int offsetX = RandomUtils.uniform(Game.getRandom(), 0, width + 1);
        int offsetY = RandomUtils.uniform(Game.getRandom(), 0, height + 1);

        //for rooms, the source direction is the direction that the room must be placed in.
        switch (sourceDirection) {
            case NORTH: //Do not vary y, only vary x
                this.location = new Point(anchor.getX() - offsetX, anchor.getY());
                break;
            case SOUTH: //Do not vary y, only vary x but place room at bottom by subtracting height
                this.location = new Point(anchor.getX() - offsetX, anchor.getY() - height + 1);
                break;
            case WEST:
                this.location = new Point(anchor.getX() - width + 1, anchor.getY() - offsetY);
                break;
            case EAST:
                this.location = new Point(anchor.getX(), anchor.getY() - offsetY);
                break;
            default:
                this.location = null;
        }

    }

    public static Room generateValidRoom(TETile[][] world,
                                         Point anchor, Direction sourceDirection) {
        return generateValidRoom(world, anchor, sourceDirection, Game.MAXATTEMPTS);
    }

    public static Room generateValidRoom(TETile[][] world,
                                         Point anchor, Direction sourceDirection, int maxAttempts) {
        Room ret;
        int attempts = 0;
        do {
            ret = new Room(anchor, sourceDirection);
            attempts++;
            if (attempts > maxAttempts) {
                //System.out.println("Run out of attempts on Room");
                return null;
            }
        } while (!ret.canPlace(world));
        ret.draw(world);
        return ret;
    }

    @Override
    public boolean hasGeneratedNeighbors() {
        return generatedNeighbors;
    }

    @Override
    public void generateNeighbors(TETile[][] world) {
        generatedNeighbors = true;
        Direction[] dirs = Direction.values();
        for (int i = 0; i < connections.length; i++) {
            connections[i] = getRandomObject(world, dirs[i]);
        }

        for (WorldAsset w : connections) {
            if (w != null) {
                w.generateNeighbors(world);
            }
        }
    }

    private WorldAsset getRandomObject(TETile[][] world, Direction d) {
        int code = RandomUtils.uniform(Game.getRandom(), 0, 9);
        Point anchor = generateAnchor(d);
        //System.out.println("Anchor: " + anchor.getX() + " " + anchor.getY());

        switch (code) {
            case 0:
                return Room.generateValidRoom(world, anchor, d);
                /*
            case 1:
                return null;
                */
            default:
                return Hall.generateValidHall(world, anchor, d, false);
        }
    }

    @Override
    public boolean canPlace(TETile[][] world) {
        if (!clearsBorder(world)) {
            return false;
        }
        for (int posX = location.getX(); posX < location.getX() + width; posX++) {
            for (int posY = location.getY(); posY < location.getY() + height; posY++) {
                if (world[posX][posY] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean clearsBorder(TETile[][] world) {
        Point opposite = new Point(this.location.getX() + this.width,
                this.location.getY() + this.height);
        return this.location.inMapBorder(world) && opposite.inMapBorder(world);
    }

    @Override
    public void draw(TETile[][] world) {
        //System.out.println("Room: " + location.getX() + " "
        //        + location.getY() + " " + width + " " + height);
        for (int posX = location.getX(); posX < location.getX() + width; posX++) {
            for (int posY = location.getY(); posY < location.getY() + height; posY++) {
                world[posX][posY] = Tileset.FLOOR;
            }
        }
        //Locating halls for testing purposes
        //world[location.getX()][location.getY()] = Tileset.WATER;

    }

    private Point generateAnchor(Direction d) {
        int offsetX = RandomUtils.uniform(Game.getRandom(), 0, width);
        int offsetY = RandomUtils.uniform(Game.getRandom(), 0, height);

        switch (d) {
            case EAST:
                return new Point(location.getX() + width, location.getY() + offsetY);
            case WEST:
                return new Point(location.getX() - 1, location.getY() + offsetY);
            case NORTH:
                return new Point(location.getX() + offsetX, location.getY() + height);
            case SOUTH:
                return new Point(location.getX() + offsetX, location.getY() - 1);
            default:
                return null;
        }
    }


}
