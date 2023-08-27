package engine.board;

import com.sun.xml.internal.bind.v2.model.core.ID;
import engine.Utils;
import engine.board.representation.Bitboard;
import engine.board.representation.ColorBoard;
import engine.piece.PieceColor;
import engine.piece.PieceType;
import engine.piece.move.Move;
import engine.piece.move.generator.MoveGenerator;

import java.util.Stack;

public class Board {
    private final ColorBoard whitePieces = new ColorBoard();
    private final ColorBoard blackPieces = new ColorBoard();

    private Bitboard allPieces = new Bitboard();
    private Bitboard allAttackedSquares = new Bitboard();

    private final Bitboard firstMovePieces = new Bitboard();
    private PieceColor turn = PieceColor.WHITE;


    private BoardState boardState = new BoardState();

    private final MoveGenerator moveGenerator = new MoveGenerator(this);

    //This is used to store boardState and reverse it on undoMove if necessary
    private final Stack<BoardState> gameStateHistory = new Stack<>();
    private final Stack<Move> moveHistory = new Stack<>();
    private final Stack<Move> undoHistory = new Stack<>();

    public BoardState getBoardState() {
        return boardState;
    }

    public MoveGenerator getMoveGenerator() {
        return moveGenerator;
    }

    public ColorBoard getWhitePieces() {
        return whitePieces;
    }

    public ColorBoard getBlackPieces() {
        return blackPieces;
    }

    public Bitboard getAllPieces() {
        return allPieces;
    }

    public Bitboard getFirstMovePieces() {
        return firstMovePieces;
    }

    public Bitboard getAllAttackedSquares() {
        return allAttackedSquares;
    }

    public void setAllPieces(Bitboard allPieces) {
        this.allPieces = allPieces;
    }

    public void setAllAttackedSquares(Bitboard allAttackedSquares) {
        this.allAttackedSquares = allAttackedSquares;
    }

    public void changeTurn() {
        turn = turn.reverse();
    }

    public PieceColor getTurn() {
        return turn;
    }

    public void setTurn(PieceColor turn) {
        this.turn = turn;
    }

    public ColorBoard getSelectedTurnBoard(PieceColor color) {
        return color == PieceColor.WHITE ? whitePieces : blackPieces;
    }

    public Stack<BoardState> getGameStateHistory() {
        return gameStateHistory;
    }

    public Stack<Move> getMoveHistory() {
        return moveHistory;
    }

    public Stack<Move> getUndoHistory() {
        return undoHistory;
    }


    /**
     * Loads and sets board up from fen string
     * @param fen Fen string
     */
    public void loadBoardFromFen(String fen) {
        FENLoader fenLoader = new FENLoader(this);
        fenLoader.loadFromFen(fen);

        moveGenerator.preGenerateAttacks();
        moveGenerator.updateLists();
        Utils.printBitBoard(allPieces.getBitBoard());
    }

    public void move(Move move) {
        //Get bitboard of moved piece
        Bitboard movedPieceBitboard = getSelectedTurnBoard(turn).getBoardFromPiece()[move.getPieceType().ordinal()].getBoard();

        //Save boardState before move to undo it if necessary
        addHistory();


        //Clear bits from moves piece, all pieces, turn  bb
        movedPieceBitboard.clearBit(move.getFromSquare());
        getSelectedTurnBoard(turn).getBoard().clearBit(move.getFromSquare());
        allPieces.clearBit(move.getFromSquare());

        if (move.getPromotionPiece() != null) {
            //If promotion  is happening change moved piece bb to promoted piece.
            movedPieceBitboard.setBitPositions();
            movedPieceBitboard = getSelectedTurnBoard(turn).getBoardFromPiece()[move.getPromotionPiece().ordinal()].getBoard();
        }
        //Set bits from moves piece, all pieces, turn  bb
        movedPieceBitboard.setBit(move.getToSquare());
        getSelectedTurnBoard(turn).getBoard().setBit(move.getToSquare());
        allPieces.setBit(move.getToSquare());

        //If piece got captured then add and remove from lists of that piece and all pieces etc.
        if (move.getCapturedPieceType() != null) {
            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getBoard().clearBit(move.getToSquare());
            getSelectedTurnBoard(getTurn().reverse()).getBoard().clearBit(move.getToSquare());
            //getAllPieces().clearBit(move.getToSquare());
            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getBoard().getBitSetPositions().removeIf(integer -> integer == move.getToSquare());
           // getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getAttackedSquares().setBitBoard(0);

            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getAttackedSquares().setBitBoard(getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getAttackedSquares().getBitBoard() & ~moveGenerator.getCorespondingToPieceMoves(move.getCapturedPieceType(),getTurn().reverse())[move.getToSquare()]);
        }
        //If piece got captured with enPassant wthen add and remove from lists of that pawn.
        if (move.getEnPassantCaptureSquare() > 0) {
            int ordinal = PieceType.PAWN.ordinal();
            int toSquare = move.getEnPassantCaptureSquare();

            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[ordinal].getBoard().clearBit(toSquare);
            getSelectedTurnBoard(getTurn().reverse()).getBoard().clearBit(toSquare);
            getAllPieces().clearBit(toSquare);
            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[ordinal].getBoard().getBitSetPositions().removeIf(integer -> integer == toSquare);
            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[ordinal].getAttackedSquares().setBitBoard(getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[ordinal].getAttackedSquares().getBitBoard()& ~moveGenerator.getCorespondingToPieceMoves(PieceType.PAWN,getTurn().reverse())[toSquare]);
        }

        if (move.isKingSideCastle()) {
            int fromSquare = turn == PieceColor.WHITE ? 7 : 63;
            int toSquare = turn == PieceColor.WHITE ? 5 : 61;

            move(new Move(fromSquare | toSquare << 6 | PieceType.ROOK.ordinal() << 12 | 0 << 15 | 0 << 18 | 0 << 21 | 0 << 24 | 0 << 30 | 0 << 31));
            removeHistory();
            changeTurn();
        }
        if (move.isQueenSideCastle()) {
            int fromSquare = turn == PieceColor.WHITE ? 0 : 56;
            int toSquare = turn == PieceColor.WHITE ? 3 : 59;

            move(new Move(fromSquare | toSquare << 6 | PieceType.ROOK.ordinal() << 12 | 0 << 15 | 0 << 18 | 0 << 21 | 0 << 24 | 0 << 30 | 0 << 31));
            removeHistory();
            changeTurn();
        }
        if (move.getPieceType() == PieceType.KING || move.getPieceType() == PieceType.ROOK) {
            if (turn == PieceColor.WHITE) {
                if (move.getPieceType() == PieceType.KING || move.getFromSquare() == 7)
                    getBoardState().setWhiteKingSideCastle(false);
                if (move.getPieceType() == PieceType.KING || move.getFromSquare() == 0)
                    getBoardState().setWhiteQueenSideCastle(false);
            } else {
                if (move.getPieceType() == PieceType.KING || move.getFromSquare() == 63)
                    getBoardState().setBlackKingSideCastle(false);
                if (move.getPieceType() == PieceType.KING || move.getFromSquare() == 56)
                    getBoardState().setBlackQueenSideCastle(false);
            }
        }

        movedPieceBitboard.setBitPositions();

        changeTurn();
        int oneCol = turn == PieceColor.WHITE ? -8 : 8;
        if (move.isDoublePawnPush()) {
            boardState.setEnPasantSquare(move.getToSquare() - oneCol);
        } else {
            boardState.setEnPasantSquare(-1);
        }




    }

    private void addHistory() {
        gameStateHistory.add(new BoardState(boardState.isWhiteKingSideCastle(), boardState.isBlackKingSideCastle(), boardState.isWhiteQueenSideCastle(), boardState.isBlackQueenSideCastle(), boardState.getEnPasantSquare(), boardState.getHalfMoveClock(), boardState.getGameCycleClock()));
    }

    private void removeHistory() {
        boardState = gameStateHistory.pop();
    }

    public void undoMove(Move move) {
        Bitboard movedPieceBitboard = move.getPromotionPiece() != null ? getSelectedTurnBoard(turn.reverse()).getBoardFromPiece()[move.getPromotionPiece().ordinal()].getBoard() : getSelectedTurnBoard(turn.reverse()).getBoardFromPiece()[move.getPieceType().ordinal()].getBoard();
        movedPieceBitboard.clearBit(move.getToSquare());
        getSelectedTurnBoard(turn.reverse()).getBoard().clearBit(move.getToSquare());
        allPieces.clearBit(move.getToSquare());
        //  movedPieceBitboard.getBitSetPositions().removeIf(integer -> integer == move.getToSquare());
        if (move.getPromotionPiece() != null) {
            movedPieceBitboard.setBitPositions();
            movedPieceBitboard = getSelectedTurnBoard(turn.reverse()).getBoardFromPiece()[move.getPieceType().ordinal()].getBoard();
        }

        movedPieceBitboard.setBit(move.getFromSquare());
        getSelectedTurnBoard(turn.reverse()).getBoard().setBit(move.getFromSquare());
        allPieces.setBit(move.getFromSquare());
        //  movedPieceBitboard.getBitSetPositions().add(move.getFromSquare());

        if (move.getCapturedPieceType() != null) {
            getSelectedTurnBoard(getTurn()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getBoard().setBit(move.getToSquare());
            getSelectedTurnBoard(getTurn()).getBoard().setBit(move.getToSquare());
            getAllPieces().setBit(move.getToSquare());
            getSelectedTurnBoard(getTurn()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getBoard().getBitSetPositions().add(move.getToSquare());
            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[move.getCapturedPieceType().ordinal()].getAttackedSquares().setBitBoard(moveGenerator.getCorespondingToPieceMoves(move.getCapturedPieceType(), getTurn().reverse())[move.getToSquare()]);

        }
        if (move.getEnPassantCaptureSquare() > 0) {
            int ordinal = PieceType.PAWN.ordinal();
            int toSquare = move.getEnPassantCaptureSquare();
            getSelectedTurnBoard(getTurn()).getBoardFromPiece()[ordinal].getBoard().setBit(toSquare);
            getSelectedTurnBoard(getTurn()).getBoard().setBit(toSquare);
            getAllPieces().setBit(toSquare);
            getSelectedTurnBoard(getTurn()).getBoardFromPiece()[ordinal].getBoard().getBitSetPositions().add(toSquare);

            getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[PieceType.PAWN.ordinal()].getAttackedSquares().setBitBoard(getSelectedTurnBoard(getTurn().reverse()).getBoardFromPiece()[PieceType.PAWN.ordinal()].getAttackedSquares().getBitBoard() & ~moveGenerator.getCorespondingToPieceMoves(PieceType.PAWN, getTurn().reverse())[move.getEnPassantCaptureSquare()]);
        }

        if (move.isKingSideCastle()) {
            int fromSquare = turn == PieceColor.BLACK ? 7 : 63;
            int toSquare = turn == PieceColor.BLACK ? 5 : 61;
            addHistory();
            undoMove(new Move(fromSquare | toSquare << 6 | PieceType.ROOK.ordinal() << 12 | 0 << 15 | 0 << 18 | 0 << 21 | 0 << 24 | 0 << 30 | 0 << 31));
            changeTurn();
        }
        if (move.isQueenSideCastle()) {
            int fromSquare = turn == PieceColor.BLACK ? 0 : 56;
            int toSquare = turn == PieceColor.BLACK ? 3 : 59;
            addHistory();
            undoMove(new Move(fromSquare | toSquare << 6 | PieceType.ROOK.ordinal() << 12 | 0 << 15 | 0 << 18 | 0 << 21 | 0 << 24 | 0 << 30 | 0 << 31));
            changeTurn();
        }
        movedPieceBitboard.setBitPositions();
        changeTurn();
        removeHistory();

    }



    public int findTypeOfPieceStandingOnSelectedSquare(int square) {
        if (!getBlackPieces().getBoard().isBitSet(square) && !getWhitePieces().getBoard().isBitSet(square))
            return 0;
        for (PieceType pieceType : PieceType.values()) {
            if (getWhitePieces().getBoardFromPiece()[pieceType.ordinal()].getBoard().isBitSet(square) || getBlackPieces().getBoardFromPiece()[pieceType.ordinal()].getBoard().isBitSet(square))
                return pieceType.ordinal() + 1;
        }
        return 0;
    }

    public int isThisPieceFirstMove(int pieceType, int square) {
        return (PieceType.PAWN.ordinal() == pieceType && getFirstMovePieces().isBitSet(square)) ? 1 : 0;
    }

    public Bitboard getKing(PieceColor pieceColor) {
        return getSelectedTurnBoard(pieceColor).getBoardFromPiece()[PieceType.KING.ordinal()].getBoard();
    }


}
