package engine.board.representation;

import engine.piece.PieceType;

public class ColorBoard {

    private final Bitboard board = new Bitboard();

    private Bitboard attackedSquares = new Bitboard();
    private PieceBoard[] boardFromPiece = new PieceBoard[PieceType.values().length];

    public PieceBoard[] getBoardFromPiece() {
        return boardFromPiece;
    }

    public void setBoardFromPiece(PieceBoard[] boardFromPiece) {
        this.boardFromPiece = boardFromPiece;
    }

    public Bitboard getBoard() {
        return board;
    }

    public Bitboard getAttackedSquares() {
        return attackedSquares;
    }

    public void setAttackedSquares(Bitboard attackedSquares) {
        this.attackedSquares = attackedSquares;
    }

    public ColorBoard() {
        for (int i = 0; i < boardFromPiece.length; i++) boardFromPiece[i] = new PieceBoard();
    }
}
