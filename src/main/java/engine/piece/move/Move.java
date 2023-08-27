package engine.piece.move;

import engine.piece.PieceType;

public class Move {
    private final int move;

    public Move(int move) {
        this.move = move;
    }

    public int getFromSquare() {
        return move & 0x3F;
    }

    public int getToSquare() {
        return (move >>> 6) & 0x3F;
    }

    public PieceType getPieceType() {
        return PieceType.values()[((move >>> 12) & 0x7)];
    }

    public PieceType getCapturedPieceType() {
        int index = ((move >>> 15) & 0x7) - 1;
        return index < 0 ? null : PieceType.values()[index];
    }

    public boolean isDoublePawnPush() {
        return ((move >>> 18) & 0x1) != 0;
    }

    public PieceType getPromotionPiece() {
        int index = ((move >>> 21) & 0x7) - 1;
        return index < 0 ? null : PieceType.values()[index];
    }

    public int getEnPassantCaptureSquare() {
        return ((move >>> 24) & 0x3F) - 1;
    }
    public boolean isKingSideCastle() {
        return ((move >>> 30) & 0x1) != 0;
    }
    public boolean isQueenSideCastle() {
        return ((move >>> 31) & 0x1) != 0;
    }


    @Override
    public String toString() {
        String toReturn = "";

        if (getFromSquare() % 8 == 0) toReturn += "a";
        else if (getFromSquare() % 8 == 1) toReturn += "b";
        else if (getFromSquare() % 8 == 2) toReturn += "c";
        else if (getFromSquare() % 8 == 3) toReturn += "d";
        else if (getFromSquare() % 8 == 4) toReturn += "e";
        else if (getFromSquare() % 8 == 5) toReturn += "f";
        else if (getFromSquare() % 8 == 6) toReturn += "g";
        else if (getFromSquare() % 8 == 7) toReturn += "h";

        toReturn += (getFromSquare() / 8) + 1;

        if (getToSquare() % 8 == 0) toReturn += "a";
        else if (getToSquare() % 8 == 1) toReturn += "b";
        else if (getToSquare() % 8 == 2) toReturn += "c";
        else if (getToSquare() % 8 == 3) toReturn += "d";
        else if (getToSquare() % 8 == 4) toReturn += "e";
        else if (getToSquare() % 8 == 5) toReturn += "f";
        else if (getToSquare() % 8 == 6) toReturn += "g";
        else if (getToSquare() % 8 == 7) toReturn += "h";
        toReturn += (getToSquare() / 8) + 1;

        if (getPromotionPiece() != null)
            toReturn += getCharFromPieceType(getPromotionPiece());
        return toReturn;

    }

    private char getCharFromPieceType(PieceType pieceType) {
        if (pieceType == PieceType.PAWN) return 'p';
        if (pieceType == PieceType.ROOK) return 'r';
        if (pieceType == PieceType.KNIGHT) return 'n';
        if (pieceType == PieceType.BISHOP) return 'b';
        if (pieceType == PieceType.QUEEN) return 'q';
        if (pieceType == PieceType.KING) return 'k';

        throw new IllegalArgumentException("Invalid piece character: ");
    }
}
