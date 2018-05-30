package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.awt.Color;
import java.util.Random;

public class Game implements Serializable {
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int MAXATTEMPTS = 12;
    public static final int MAXENEMIES = 6;
    private static long seed;
    private boolean[][] isIlluminated;
    private boolean[][] isVisible;

    private static Random RANDOM = new Random(seed);
    private boolean isWon = false;
    private TETile[][] finalWorldFrame;
    private Game loadGame;
    private Player player;
    private TERenderer ter;
    private int yOffset = 2;
    private boolean isLost = false;
    private int numEnemies = 0;
    private Enemy[] enemies = new Enemy[MAXENEMIES];

    public boolean isHard;

    public Game() {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        isIlluminated = new boolean[WIDTH][HEIGHT];
        isVisible = new boolean[WIDTH][HEIGHT];

    }

    public static Random getRandom() {
        return RANDOM;
    }

    private static Game loadGame() {
        File f = new File("./game.txt");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Game loadWorld = (Game) os.readObject();
                os.close();
                return loadWorld;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        /* In the case no World has been saved yet, we return a new one. */
        return new Game();
    }

    private static void saveGame(Game w) {
        File f = new File("./game.txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(w);
            os.close();
        } catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */

    private void setUp() {
        if (loadGame == null) {
            finalWorldFrame = createEmptyWorld();
            Direction randDir = Direction.getRandomDirection();
            Room seedRoom = Room.generateValidRoom(finalWorldFrame,
                    generateSeedLocation(), randDir, 1000);
            seedRoom.generateNeighbors(finalWorldFrame);
            drawWalls(finalWorldFrame);
            new Door(this);
            if(isHard) {
                new LightSource(this);
            }
            player = new Player(this);
        } else {
            this.finalWorldFrame = loadGame.getFinalWorldFrame();
            this.enemies = loadGame.enemies;
            this.numEnemies = loadGame.getNumEnemies();
            this.isHard = loadGame.isHard;
            this.player = loadGame.player;
            this.isIlluminated = loadGame.isIlluminated;
            this.isVisible = loadGame.isVisible;
        }
    }

    public void playWithKeyboard() {
        int menuWidth = 40;
        int menuHeight = 40;
        StdDraw.setCanvasSize(menuWidth * 16, menuHeight * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, menuWidth);
        StdDraw.setYscale(0, menuHeight);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        drawLoadingFrame();
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT + yOffset, 0, yOffset);
        setUp();
        updateVisibility();
        ter.renderFrame(finalWorldFrame, this);
        while (!isWon && !isLost) {
            if (StdDraw.hasNextKeyTyped()) {
                boolean playerMoved = false;
                char c = StdDraw.nextKeyTyped();
                switch (c) {
                    case ':':
                        while (true) {
                            if (StdDraw.hasNextKeyTyped()) {
                                if (StdDraw.nextKeyTyped() == 'q'
                                        || StdDraw.nextKeyTyped() == 'Q') {
                                    saveGame(this);
                                    System.exit(0);
                                    break;
                                }
                            }
                        }
                        break;
                    case 'w':
                        playerMoved = player.move(this, Direction.NORTH);
                        break;
                    case 's':
                        playerMoved = player.move(this, Direction.SOUTH);
                        break;
                    case 'a':
                        playerMoved = player.move(this, Direction.WEST);
                        break;
                    case 'd':
                        playerMoved = player.move(this, Direction.EAST);
                        break;
                    default:
                }
                if (playerMoved) {
                    updateVisibility();
                    for (int i = 0; i < numEnemies; i++) {
                        enemies[i].pathFindStep(this, player);
                    }
                }

            }
            ter.renderFrame(finalWorldFrame, mouseOutput(), this);
        }
        ResultFrame frame = new ResultFrame(isWon);
    }

    private Point generateSeedLocation() {
        int x = RandomUtils.uniform(Game.RANDOM, WIDTH / 4 - 1, WIDTH * 3 / 4 + 1);
        int y = RandomUtils.uniform(Game.RANDOM, HEIGHT / 4 - 1, HEIGHT * 3 / 4 + 1);
        //System.out.println(x + " " + y);
        return new Point(x, y);
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        char[] arrInput = input.toCharArray();
        int seedEndInd = -1;
        if (arrInput.length <= 1) {
            return null;
        } else if (arrInput[0] == 'l' || arrInput[0] == 'L') {
            this.loadGame = loadGame();
            seedEndInd = 0;
        } else if (arrInput[0] != 'n' && arrInput[0] != 'N') {
            return null;
        } else {
            for (int i = 1; i < arrInput.length; i++) {
                if ((arrInput[i] == 's' || arrInput[i] == 'S') && seedEndInd < 0) {
                    seedEndInd = i;
                    break;
                }
            }
            this.seed = Long.parseLong(input.substring(1, seedEndInd));
            this.RANDOM = new Random(seed);
        }

        setUp();

        System.out.println(this.seed);

        System.out.println(input.substring(seedEndInd + 1, input.length()));
        char[] moveArr = input.substring(seedEndInd + 1, input.length()).toCharArray();
        for (int i = 0; i < moveArr.length; i++) {
            switch (moveArr[i]) {
                case 'w':
                    player.move(this, Direction.NORTH);
                    break;
                case 's':
                    player.move(this, Direction.SOUTH);
                    break;
                case 'a':
                    player.move(this, Direction.WEST);
                    break;
                case 'd':
                    player.move(this, Direction.EAST);
                    break;
                case ':':
                    if (i + 1 < moveArr.length
                            && (moveArr[i + 1] == 'q'
                            || moveArr[i + 1] == 'Q')) {
                        saveGame(this);
                        return finalWorldFrame;
                    }
                    break;
                default:
                    break;
            }
        }

        return finalWorldFrame;
    }

    private TETile[][] createEmptyWorld() {
        //@Source RandomWorldDemo.java from lab5
        //Init board
        TETile[][] tiles = new TETile[WIDTH][HEIGHT];

        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
        return tiles;
    }

    private void drawWalls(TETile[][] world) {
        for (int i = 1; i < WIDTH - 1; i++) {
            for (int j = 1; j < HEIGHT - 1; j++) {
                if (world[i][j].equals(Tileset.FLOOR)) {
                    colorValidNeighbors(world, new Point(i, j));
                }
            }
        }
    }

    private void colorValidNeighbors(TETile[][] world, Point p) {
        for (int i = p.getX() - 1; i < p.getX() + 2; i++) {
            for (int j = p.getY() - 1; j < p.getY() + 2; j++) {
                //System.out.println(i + " " + j);
                if (world[i][j].equals(Tileset.NOTHING)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }

    public void drawLoadingFrame() {
        int width = 20;
        int height = 20;
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.text(width, height + 10, "Cave Trawler");
        font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(font);
        StdDraw.text(width, height, "New Game Easy (N)");
        StdDraw.text(width, height - 5, "New Game Hard (H)");
        StdDraw.text(width, height - 10, "Load Game (L)");
        StdDraw.text(width, height - 15, "Quit Game (Q)");
        while (true) {
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char letter = StdDraw.nextKeyTyped();
                switch (letter) {
                    case 'n':
                        drawSeedFrame("Choose a seed (Press s to start):", 40, 40);
                        return;
                    case 'N':
                        drawSeedFrame("Choose a seed (Press s to start):", 40, 40);
                        return;
                    case 'h':
                        isHard = true;
                        drawSeedFrame("Choose a seed (Press s to start):", 40, 40);
                        return;
                    case 'H':
                        isHard = true;
                        drawSeedFrame("Choose a seed (Press s to start):", 40, 40);
                        return;
                    case 'l':
                        loadGame = loadGame();
                        return;
                    case 'L':
                        loadGame = loadGame();
                        return;
                    case 'q':
                        System.exit(0);
                        return;
                    case 'Q':
                        System.exit(0);
                        return;
                    default:
                }
            }
        }
    }

    public void drawSeedFrame(String s, int width, int height) {
        StdDraw.clear();
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(StdDraw.BLUE);
        StdDraw.text(width / 2, height / 2, s);
        String seedString = "";
        while (true) {
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char add = StdDraw.nextKeyTyped();
                if (add == 's' || add == 'S') {
                    try {
                        //System.out.println(seed);
                        seed = Long.parseLong(seedString);
                        RANDOM = new Random(seed);
                        return;
                    } catch (RuntimeException e) {
                        StdDraw.clear(Color.RED);
                        StdDraw.text(width / 2, height / 2, "Enter a number please!");
                        seedString = "";
                    }
                } else {
                    StdDraw.clear();
                    seedString += Character.toString(add);
                    StdDraw.text(width / 2, height / 2, seedString);
                }
            }
        }
    }

    public TETile[][] getFinalWorldFrame() {
        return finalWorldFrame;
    }

    public void setFinalWorldFrame(int x, int y, TETile t) {
        finalWorldFrame[x][y] = t;
    }

    private String mouseOutput() {
        int x = (int) (Math.floor(StdDraw.mouseX()));
        int y = (int) (Math.floor(StdDraw.mouseY())) - yOffset;
        TETile tile;
        try {
            if(checkVisibility(x,y) || checkIllumination(x,y) || !isHard) {
                tile = getFinalWorldFrame()[x][y];
            }
            else {
                tile = Tileset.NOTHING;
            }
            return "Tile: " + tile.description();
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    public void setWin(boolean value) {
        isWon = value;
    }

    public void setLost(boolean val) {
        isLost = val;
    }

    public void addEnemy(Enemy e) {
        enemies[numEnemies] = e;
        numEnemies++;
    }

    public int getNumEnemies(){
        return numEnemies;
    }

    public void updateVisibility(){
        isVisible = new boolean[WIDTH][HEIGHT];
        for (int i = -2 + player.getX(); i <= 2 + player.getX(); i++) {
            for (int j = -2 + player.getY(); j <= 2 + player.getY(); j++) {
                if (i >= 0 && j >= 0 && i < Game.WIDTH && j < Game.HEIGHT) {
                    isVisible[i][j] = true;
                }
            }
        }
    }

    public boolean checkIllumination(int x, int y) {
        return isIlluminated[x][y];
    }

    public boolean checkVisibility(int x, int y) {
        return isVisible[x][y];
    }


    public void updateIllumination(int x, int y) {
        for (int i = -4 + x; i <= 4 + x; i++) {
            for (int j = -4 + y; j <= 4 + y; j++) {
                if (i >= 0 && j >= 0 && i < Game.WIDTH && j < Game.HEIGHT) {
                    //System.out.println(former[i][j].description());
                    isIlluminated[i][j] = true;
                }
            }
        }
    }
}
