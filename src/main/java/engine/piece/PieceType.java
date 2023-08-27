package engine.piece;

public enum PieceType {
    PAWN,
    KNIGHT,
    BISHOP,
    ROOK,
    QUEEN,
    KING;

    public boolean isLeaping() {
        return this == KNIGHT || this == PAWN || this == KING;
    }
}
