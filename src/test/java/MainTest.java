import engine.ai.Perft;
import engine.board.Board;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class MainTest {


    @Test
    public void startPosTest() {
     //Fens from chess programing wiki
        long res = 0;

        Board board = new Board();
        Perft perft = new Perft(board);

        board.loadBoardFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");

        res = perft.perft(6);

        assertEquals(res,119060324);
    }
    @Test
    public void position2Test() {
        //Fens from chess programing wiki
        long res = 0;

        Board board = new Board();
        Perft perft = new Perft(board);

        board.loadBoardFromFen("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq");

        res = perft.perft(5);

        assertEquals(res,193690690);
    }
    @Test
    public void position3Test() {
        //Fens from chess programing wiki
        long res = 0;

        Board board = new Board();
        Perft perft = new Perft(board);

        board.loadBoardFromFen("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - ");

        res = perft.perft(7);

        assertEquals(res,178633661);
    }
    @Test
    public void position4Test() {
        //Fens from chess programing wiki
        long res = 0;

        Board board = new Board();
        Perft perft = new Perft(board);

        board.loadBoardFromFen("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");

        res = perft.perft(6);

        assertEquals(res,706045033);
    }
    @Test
    public void position5Test() {
        //Fens from chess programing wiki
        long res = 0;

        Board board = new Board();
        Perft perft = new Perft(board);

        board.loadBoardFromFen("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");

        res = perft.perft(5);

        assertEquals(res,89941194);
    }
    @Test
    public void position6Test() {
        //Fens from chess programing wiki
        long res = 0;

        Board board = new Board();
        Perft perft = new Perft(board);

        board.loadBoardFromFen("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - -");

        res = perft.perft(5);

        assertEquals(res,164075551);
    }
}