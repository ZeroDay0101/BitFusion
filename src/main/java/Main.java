import engine.ai.Perft;
import engine.board.Board;

public class Main {
    public static int c;

    public static void main(String[] args) {


        Board board = new Board();
        board.loadBoardFromFen("1b6/pbQ3p1/1p1q4/3B1K2/PBn3Pp/2p4k/8/8 w - - 0 1");

        Perft perft = new Perft(board);
        long startTime = System.currentTimeMillis();
        System.out.println(perft.perft(5));
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + " ms");
    }

}
