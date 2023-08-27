package engine.board.representation;

/**
 * Class used for encapsulation of arrays for each piece type and occupied squares for each color
 */
public class PieceBoard {
    private final Bitboard board = new Bitboard();
    private Bitboard attackedSquares = new Bitboard();


    public Bitboard getBoard() {
        return board;
    }

    public Bitboard getAttackedSquares() {
        return attackedSquares;
    }

    public void setAttackedSquares(Bitboard attackedSquares) {
        this.attackedSquares = attackedSquares;
    }
}
