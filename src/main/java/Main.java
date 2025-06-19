import engine.ai.Perft;
import engine.board.Board;

public class Main {

    public static void main(String[] args) {


        Board board = new Board();
        board.loadBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        Perft perft = new Perft(board);
        long startTime = System.currentTimeMillis();
        System.out.println(perft.perft(6));
        System.out.println("Time elapsed: " + (System.currentTimeMillis() - startTime) + " ms");
    }

}
