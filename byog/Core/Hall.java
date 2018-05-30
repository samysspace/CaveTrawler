package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

public class Hall implements WorldAsset {

    private static final int MAXLENGTH = 10;
    private static final int MINLENGTH = 3;
    private Point start;
    private Point end;
    private boolean generatedNeighbors = false;

    private WorldAsset terminal;

    private Direction direction;

    public Hall(Point anchor, Direction sourceDirection, boolean fromHall) {
        //System.out.println("Hall constructor called");
        this.start = anchor;
        int length = RandomUtils.uniform(Game.getRandom(), MINLENGTH, MAXLENGTH);

        if (!fromHall) {
            this.direction = sourceDirection;
        } else {
            this.direction = Direction.getValidBranchDirection(sourceDirection);
        }

        switch (this.direction) {
            case NORTH:
                this.end = new Point(start.getX(), start.getY() + length);
                break;
            case SOUTH:
                this.end = new Point(start.getX(), start.getY() - length);
                break;
            case WEST:
                this.end = new Point(start.getX() - length, start.getY());
                break;
            default:
                this.end = new Point(start.getX() + length, start.getY());
                break;
        }

    }

    public static Hall generateValidHall(TETile[][] world, Point start,
                                         Direction sourceDirection, boolean fromHall) {
        Hall ret;
        int attempts = 0;
        do {
            //System.out.println("Hall generation attempt" + attempts);
            ret = new Hall(start, sourceDirection, fromHall);
            attempts++;
            if (attempts > Game.MAXATTEMPTS) {
                return null;
            }
        } while (!ret.canPlace(world));
        ret.draw(world);
        return ret;
    }

    private WorldAsset getRandomObject(TETile[][] world) {
        int code = RandomUtils.uniform(Game.getRandom(), 0, 8);
        Point anchor = getTerminalAnchor();
        switch (code) {
            case 1:
                return Room.generateValidRoom(world, anchor, this.direction);
            case 2:
                return Room.generateValidRoom(world, anchor, this.direction);
            case 3:
                return Room.generateValidRoom(world, anchor, this.direction);
            /*
            case 4:
                return null;
            */
            default:
                return Hall.generateValidHall(world, anchor, this.direction, true);

        }
    }

    private Point getTerminalAnchor() {
        switch (this.direction) {
            case WEST:
                return new Point(end.getX() - 1, end.getY());
            case EAST:
                return new Point(end.getX() + 1, end.getY());
            case NORTH:
                return new Point(end.getX(), end.getY() + 1);
            default:
                return new Point(end.getX(), end.getY() - 1);
        }
    }

    @Override
    public void generateNeighbors(TETile[][] world) {
        generatedNeighbors = true;
        terminal = this.getRandomObject(world);

        if (terminal != null) {
            terminal.generateNeighbors(world);
        }

    }

    @Override
    public boolean hasGeneratedNeighbors() {
        return generatedNeighbors;
    }

    @Override
    public boolean canPlace(TETile[][] world) {
        if (!clearsBorder(world)) {
            return false;
        }
        for (int posX = start.getX(); posX <= end.getX(); posX++) {
            for (int posY = start.getY(); posY <= end.getY(); posY++) {
                if (world[posX][posY] != Tileset.NOTHING) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean clearsBorder(TETile[][] world) {
        return this.start.inMapBorder(world) && this.end.inMapBorder(world);

    }

    @Override
    public void draw(TETile[][] world) {
        //System.out.println("Hall: " + start.getX() + " " + start.getY()
        //        + " " + end.getX() + " " + end.getY());

        for (int posX = Math.min(start.getX(), end.getX());
             posX <= Math.max(start.getX(), end.getX()); posX++) {
            for (int posY = Math.min(start.getY(), end.getY());
                 posY <= Math.max(start.getY(), end.getY()); posY++) {
                world[posX][posY] = Tileset.FLOOR;
            }
        }
        //Locating halls for testing purposes
        //world[start.getX()][start.getY()] = Tileset.TREE;

    }


}
