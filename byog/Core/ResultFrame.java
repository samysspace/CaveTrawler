package byog.Core;

import edu.princeton.cs.introcs.StdAudio;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;

public class ResultFrame {
    public ResultFrame(boolean isWin) {
        int menuWidth = 40;
        int menuHeight = 40;
        StdDraw.setCanvasSize(menuWidth * 16, menuHeight * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.setXscale(0, menuWidth);
        StdDraw.setYscale(0, menuHeight);
        String output = "You lose.";
        String second = "Press Q to exit or R to restart.";
        //StdDraw.picture(20,20,"byog/Core/ScaryFace.jpg");
        if (isWin) {
            output = "You win!";
        }
        StdDraw.text(menuWidth / 2, menuHeight / 2, output);
        StdDraw.text(menuWidth / 2, (menuHeight/2) - 10, second);
        //double[] x = StdAudio.read("byog/Core/scary.wav");
        //StdAudio.play(x);
        while (true) {
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char s = StdDraw.nextKeyTyped();
                switch (s) {
                    case 'q':
                        System.exit(0);
                        return;
                    case 'Q':
                        System.exit(0);
                        return;
                    case 'r':
                        Game game = new Game();
                        game.playWithKeyboard();
                        return;
                    case 'R':
                        Game gameR = new Game();
                        gameR.playWithKeyboard();
                        return;
                    default:
                        return;
                }
            }
        }
    }
}
