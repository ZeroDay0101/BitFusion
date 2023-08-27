package engine.ai;

import engine.board.Board;
import engine.piece.move.Move;

import java.util.List;

public class Perft {

    private int startingDepth;

    private final Board board;

    public Perft(Board board) {
        this.board = board;
    }

    public long perft(int depth) {

        List<Move> moves = board.getMoveGenerator().generateLegalMoves();
        long nodes = 0;
        if (startingDepth == 0) startingDepth = depth;
        if (startingDepth == 1) moves.forEach(move -> System.out.println(move + ": 1"));
        if (depth == 1) return moves.size();

        for (Move move : moves) {
            board.move(move);
            long evalPos = perft(depth - 1);
            nodes += evalPos;
            if (depth == startingDepth) System.out.println(move + ": " + evalPos);
            board.undoMove(move);
        }
        return nodes;
    }
}
