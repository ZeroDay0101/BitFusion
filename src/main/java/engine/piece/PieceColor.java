package engine.piece;

public enum PieceColor {
    WHITE,
    BLACK;

    /**
     * @return Reversed color
     */
    public PieceColor reverse() {
        if (this == WHITE) return BLACK;
        else return WHITE;
    }
}
