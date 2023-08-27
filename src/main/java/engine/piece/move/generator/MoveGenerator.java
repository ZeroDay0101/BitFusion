package engine.piece.move.generator;

import engine.board.Board;
import engine.board.representation.Bitboard;
import engine.piece.PieceColor;
import engine.piece.PieceType;
import engine.piece.move.Move;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoveGenerator {

    private final Board board;
    private final long FULL_BOARD = 0xFFFFFFFFFFFFFFFFL;
    private final long notAFile = 0xFEFEFEFEFEFEFEFEL;
    private final long notABFile = 0xFCFCFCFCFCFCFCFCL;
    private final long notHFile = 0x7F7F7F7F7F7F7F7FL;
    private final long notGHFile = 0x3F3F3F3F3F3F3F3FL;
    private PieceColor currentTurn;
    private PieceColor enemyTurn;
    private List<Move> generatedMoves;
    public final MagicBitboards magicBitboards = new MagicBitboards(this);
    public final long[] kingMoves = new long[64];
    public final long[] knightMoves = new long[64];

    private final long[] pawnMovesWhite = new long[64];
    private final long[] pawnMovesBlack = new long[64];
    private final long[] pawnCapturesWhite = new long[64];
    private final long[] pawnCapturesBlack = new long[64];
    final CheckState checkState = new CheckState();
    private final Map<Dimension, Long> rookMoves = new HashMap<>();

    public Map<Dimension, Long> getRookMoves() {
        return rookMoves;
    }

    private final Map<Dimension, Long> bishopMoves = new HashMap<>();

    public Map<Dimension, Long> getBishopMoves() {
        return bishopMoves;
    }

    private final Map<Dimension, Long> queenMoves = new HashMap<>();

    public Map<Dimension, Long> getQueenMoves() {
        return queenMoves;
    }

    private final HashMap<Integer, Long> pinMap = new HashMap<>();

    private long pawnEnPassantPin = 0;

    public List<Move> generateLegalMoves() {
        generatedMoves = new LinkedList<>();
        currentTurn = board.getTurn();
        enemyTurn = board.getTurn().reverse();

        //updateAttackTable();

        updateAttacksAndMoves();

        return generatedMoves;
    }

    /**
     * Update all color pieces boards, so they can be used by move generator also clears attacked squares because new ones will be generated.
     */
    public void updateLists() {
        for (PieceType pieceType : PieceType.values()) {
            board.getWhitePieces().getBoard().setBitBoard(board.getWhitePieces().getBoard().getBitBoard() | board.getWhitePieces().getBoardFromPiece()[pieceType.ordinal()].getBoard().getBitBoard());
            board.getBlackPieces().getBoard().setBitBoard(board.getBlackPieces().getBoard().getBitBoard() | board.getBlackPieces().getBoardFromPiece()[pieceType.ordinal()].getBoard().getBitBoard());
            board.getBlackPieces().getBoardFromPiece()[pieceType.ordinal()].getBoard().setBitPositions();
            board.getWhitePieces().getBoardFromPiece()[pieceType.ordinal()].getBoard().setBitPositions();
        }

        board.setAllPieces(new Bitboard(board.getWhitePieces().getBoard().getBitBoard() | board.getBlackPieces().getBoard().getBitBoard()));
        board.getAllPieces().setBitPositions();


        board.getFirstMovePieces().setBitBoard((board.getAllPieces().getBitBoard()) & 0xFFFF00000000FFFFL);
    }

    public MoveGenerator(Board board) {
        this.board = board;
    }

    private void generateKingMoves(PieceColor turn) {
        Bitboard kingBitBoard = board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.KING.ordinal()].getBoard();
        if (turn == enemyTurn)
            board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.KING.ordinal()].getAttackedSquares().setBitBoard(0);

        for (Integer pos : kingBitBoard.getBitSetPositions()) {
            //Add castling moves
            if (turn == currentTurn) {
                if (checkState.checkingPiecePosition == 0) {
                    if (turn == PieceColor.WHITE) {
                        if (board.getBoardState().isWhiteKingSideCastle()) {
                            long whiteKingSideSquaresBetweenKingAndRook = 3L << 5;
                            //Check if Squares between rook and king aren't attacked and are clear of pieces.
                            if (board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.ROOK.ordinal()].getBoard().isBitSet(7) && (board.getSelectedTurnBoard(enemyTurn).getAttackedSquares().getBitBoard() & whiteKingSideSquaresBetweenKingAndRook) == 0 && (board.getAllPieces().getBitBoard() & (whiteKingSideSquaresBetweenKingAndRook)) == 0) {
                                addLegalMovesOrAttackedSquares(turn, pos, (1L << pos) << 2, PieceType.KING, false, false, null, false, false, true, false);
                            }
                        }
                        if (board.getBoardState().isWhiteQueenSideCastle()) {
                            long whiteQueenSideSquaresBetweenKingAndRook = 7L << 1;
                            //Check if Squares between rook and king aren't attacked and are clear of pieces.
                            if (board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.ROOK.ordinal()].getBoard().isBitSet(0) && (board.getSelectedTurnBoard(enemyTurn).getAttackedSquares().getBitBoard() & 6L << 1) == 0 && (board.getAllPieces().getBitBoard() & (whiteQueenSideSquaresBetweenKingAndRook)) == 0) {
                                addLegalMovesOrAttackedSquares(turn, pos, (1L << pos) >> 2, PieceType.KING, false, false, null, false, false, false, true);
                            }
                        }
                    } else {
                        if (board.getBoardState().isBlackKingSideCastle()) {
                            long whiteKingSideSquaresBetweenKingAndRook = (3L << 5) << 8 * 7;
                            //Check if Squares between rook and king aren't attacked and are clear of pieces.
                            if (board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.ROOK.ordinal()].getBoard().isBitSet(63) && (board.getSelectedTurnBoard(enemyTurn).getAttackedSquares().getBitBoard() & whiteKingSideSquaresBetweenKingAndRook) == 0 && (board.getAllPieces().getBitBoard() & (whiteKingSideSquaresBetweenKingAndRook)) == 0) {
                                addLegalMovesOrAttackedSquares(turn, pos, (1L << pos) << 2, PieceType.KING, false, false, null, false, false, true, false);
                            }
                        }
                        if (board.getBoardState().isBlackQueenSideCastle()) {
                            long whiteQueenSideSquaresBetweenKingAndRook = (7L << 1) << 8 * 7;
                            //Check if Squares between rook and king aren't attacked and are clear of pieces.
                            if (board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.ROOK.ordinal()].getBoard().isBitSet(56) && (board.getSelectedTurnBoard(enemyTurn).getAttackedSquares().getBitBoard() & (6L << 1) << 8 * 7) == 0 && (board.getAllPieces().getBitBoard() & (whiteQueenSideSquaresBetweenKingAndRook)) == 0) {
                                addLegalMovesOrAttackedSquares(turn, pos, (1L << pos) >> 2, PieceType.KING, false, false, null, false, false, false, true);
                            }
                        }
                    }
                }
            }
            addLegalMovesOrAttackedSquares(turn, pos, kingMoves[pos], PieceType.KING, false, false, null, false, false, false, false);
        }
    }

    private void generateKnightMoves(PieceColor turn) {
        Bitboard kingBitBoard = board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.KNIGHT.ordinal()].getBoard();
        if (turn == enemyTurn)
            board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.KNIGHT.ordinal()].getAttackedSquares().setBitBoard(0);
        for (Integer pos : kingBitBoard.getBitSetPositions()) {
            addLegalMovesOrAttackedSquares(turn, pos, knightMoves[pos], PieceType.KNIGHT, false, false, null, false, false, false, false);
        }
    }

    private void generateRookMoves(PieceColor turn) {
        Bitboard rookBitBoard = board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.ROOK.ordinal()].getBoard();
        if (turn == enemyTurn)
            board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.ROOK.ordinal()].getAttackedSquares().setBitBoard(0);
        for (Integer pos : rookBitBoard.getBitSetPositions()) {

            int kingPos = 0;
            if (turn != currentTurn)
                kingPos = board.getSelectedTurnBoard(currentTurn).getBoardFromPiece()[PieceType.KING.ordinal()].getBoard().getBitSetPositions().get(0);
            addLegalMovesOrAttackedSquares(turn, pos, rookMoves.get(new Dimension(pos, magicBitboards.getKey(pos, board.getAllPieces().getBitBoard() & ~(1L << kingPos), PieceType.ROOK))), PieceType.ROOK, false, false, null, false, false, false, false);
            // if (turn != currentTurn) board.getAllPieces().setBit(kingPos);
        }
    }

    private void generateBishopMoves(PieceColor turn) {
        Bitboard rookBitBoard = board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.BISHOP.ordinal()].getBoard();

        if (turn == enemyTurn) board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.BISHOP.ordinal()].getAttackedSquares().setBitBoard(0);
        for (Integer pos : rookBitBoard.getBitSetPositions()) {
            int kingPos = 0;
            if (turn != currentTurn) kingPos = board.getSelectedTurnBoard(currentTurn).getBoardFromPiece()[PieceType.KING.ordinal()].getBoard().getBitSetPositions().get(0);
            addLegalMovesOrAttackedSquares(turn, pos, bishopMoves.get(new Dimension(pos, magicBitboards.getKey(pos, board.getAllPieces().getBitBoard() & ~(1L << kingPos), PieceType.BISHOP))), PieceType.BISHOP, false, false, null, false, false, false, false);
            // if (turn != currentTurn) board.getAllPieces().setBit(kingPos);
        }
    }

    private void generateQueenMoves(PieceColor turn) {
        if (turn == enemyTurn)
            board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.QUEEN.ordinal()].getAttackedSquares().setBitBoard(0);
        Bitboard queenBitBoard = board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.QUEEN.ordinal()].getBoard();
        for (Integer pos : queenBitBoard.getBitSetPositions()) {
            int kingPos = 0;
            if (turn != currentTurn)
                kingPos = board.getSelectedTurnBoard(currentTurn).getBoardFromPiece()[PieceType.KING.ordinal()].getBoard().getBitSetPositions().get(0);
            addLegalMovesOrAttackedSquares(turn, pos, rookMoves.get(new Dimension(pos, magicBitboards.getKey(pos, board.getAllPieces().getBitBoard() & ~(1L << kingPos), PieceType.ROOK))) | bishopMoves.get(new Dimension(pos, magicBitboards.getKey(pos, board.getAllPieces().getBitBoard() & ~(1L << kingPos), PieceType.BISHOP))), PieceType.QUEEN, false, false, null, false, false, false, false);
            // if (turn != currentTurn) board.getAllPieces().setBit(kingPos);
        }
    }

    private void generatePawnMoves(PieceColor turn) {
        Bitboard rookBitBoard = board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.PAWN.ordinal()].getBoard();

        if (turn == enemyTurn)
            board.getSelectedTurnBoard(turn).getBoardFromPiece()[PieceType.PAWN.ordinal()].getAttackedSquares().setBitBoard(0);

        for (Integer pos : rookBitBoard.getBitSetPositions()) {
            long b = 0;

            int oneCol = turn == PieceColor.WHITE ? 8 : -8;
            int twoCol = turn == PieceColor.WHITE ? 16 : -16;

            long[] movesTable = turn == PieceColor.WHITE ? pawnMovesWhite : pawnMovesBlack;
            long[] capturesTable = turn == PieceColor.WHITE ? pawnCapturesWhite : pawnCapturesBlack;

            //int whiteOriginalRank = 1,blackOriginalRank = 6;

            int originalRank = turn == PieceColor.WHITE ? 1 : 6;

            int promotionRank = turn == PieceColor.WHITE ? 6 : 1;

            if (turn == enemyTurn) {
                addLegalMovesOrAttackedSquares(turn, pos, (capturesTable[pos]), PieceType.PAWN, false, true, null, false, false, false, false);
                continue;
            }
            long moves = 0L;

            long colPlusOne = 1L << (pos + oneCol);
            long colPlusTwo = 1L << (pos + twoCol);

            //Handle promotions
            if (pos/8 == promotionRank) { //Pawn can promote on the next move.
                for (PieceType pieceType : PieceType.values()) {
                    if (pieceType == PieceType.PAWN || pieceType == PieceType.KING) continue;
                    //Add promotion forward moves
                    if ((colPlusOne & board.getAllPieces().getBitBoard()) == 0) //No blockers, pawn can push one col
                        addLegalMovesOrAttackedSquares(turn, pos, (colPlusOne), PieceType.PAWN, true, false, pieceType, false, false, false, false);

                    //Handle capture promotions
                    addLegalMovesOrAttackedSquares(turn, pos, (capturesTable[pos]), PieceType.PAWN, false, true, pieceType, false, false, false, false);
                }
            } else { //If pawn is on promotion square forward moves have already been handled and double pawns push is impossible. That's why we are using else.
                if ((colPlusOne & board.getAllPieces().getBitBoard()) == 0) { //No blockers, pawn can push one col
                    addLegalMovesOrAttackedSquares(turn, pos, (colPlusOne), PieceType.PAWN, true, false, null, false, false, false, false);
                    if (pos / 8 == originalRank && (colPlusTwo & board.getAllPieces().getBitBoard()) == 0) { //No blockers and pawn didn't move earlier, pawn can push two col
                        addLegalMovesOrAttackedSquares(turn, pos, colPlusTwo, PieceType.PAWN, true, false, null, true, false, false, false);
                    }
                }

                //Handle captures
                addLegalMovesOrAttackedSquares(turn, pos, (capturesTable[pos]), PieceType.PAWN, false, true, null, false, false, false, false);

                //Handle enpaesant
                if (board.getBoardState().getEnPasantSquare() > 0) {
                    long enPassant = (capturesTable[pos] & (1L << board.getBoardState().getEnPasantSquare()));
                    if (enPassant != 0)
                        addLegalMovesOrAttackedSquares(turn, pos, (enPassant), PieceType.PAWN, false, true, null, false, true, false, false);
                }
            }

        }

    }

    public void preGenerateAttacks() {
        for (int i = 0; i < 64; i++) {
            long position = 1L << i;
            //King moves
            long allMoves = (position << 1) & notAFile | (position << 9) & notAFile | (position >>> 7) & notAFile | (position << 7) & notHFile | (position >>> 1) & notHFile | (position >>> 9) & notHFile | (position << 8) | (position >>> 8);
            // Filter out moves that exceed the board boundaries
            kingMoves[i] = allMoves;
            //Knight moves
            allMoves = (position << 15) & notHFile | (position << 17) & notAFile | (position >>> 17) & notHFile | (position >>> 15) & notAFile | (position << 6) & notGHFile | (position >>> 10) & notGHFile | (position << 10) & notABFile | (position >>> 6) & notABFile;
            knightMoves[i] = allMoves;


            pawnMovesWhite[i] = position << 8 | position << 16;
            pawnMovesBlack[i] = position >> 8 | position >> 16;
            pawnCapturesWhite[i] = (position << 7) & notHFile | (position << 9) & notAFile;
            pawnCapturesBlack[i] = (position >> 7) & notAFile | (position >> 9) & notHFile;

            magicBitboards.initMoves(i, PieceType.ROOK);
            magicBitboards.initMoves(i, PieceType.BISHOP);
        }
    }

    /**
     * Adds legal-moves if piece that moves are generated for is the same color as board turn. If not, attacks of the piece are added;
     *
     * @param color             - Color of the piece moves are generated for
     * @param pos               - Position of a piece
     * @param legalMoves        - Pseudo legal moves of a piece
     * @param isMoveQuiet
     * @param isCaptureOnly
     * @param promotionPiece
     * @param isDoublePawnPush
     * @param isEnPassant
     * @param isCastleKingSide
     * @param isCastleQueenSide
     */
    private void addLegalMovesOrAttackedSquares(PieceColor color, Integer pos, long legalMoves, PieceType pieceType, boolean isMoveQuiet, boolean isCaptureOnly, PieceType promotionPiece, boolean isDoublePawnPush, boolean isEnPassant, boolean isCastleKingSide, boolean isCastleQueenSide) {
        if (legalMoves == 0L) return;
        if (color == currentTurn) {
            if (pieceType == PieceType.KING)
                legalMoves &= ~board.getSelectedTurnBoard(enemyTurn).getAttackedSquares().getBitBoard(); //Filter moves that are allowing king to walk into check
            else if (checkState.checkingPiecePosition != 0) { //If piece is not king, allow only moves that can break a check (Block or capture attacking piece). If it's a double check, only king moves are allowed.
                if (checkState.doubleCheck) return;
                if (checkState.pieceType.isLeaping()) {
                    long enPassant = (pieceType == PieceType.PAWN && board.getBoardState().getEnPasantSquare() > 0) ? 1L << (Math.max(board.getBoardState().getEnPasantSquare(), 0)) : 0;
                    legalMoves &= checkState.checkingPiecePosition | enPassant;
                }
                else {
                    legalMoves &= checkState.checkMask | checkState.checkingPiecePosition;
                }
            }

            if (isEnPassant) {
                int enPassantSquare = board.getBoardState().getEnPasantSquare();
                if (enPassantSquare >= 0 && (legalMoves & 1L << enPassantSquare) != 0) {
                    legalMoves &= 1L << enPassantSquare | board.getSelectedTurnBoard(enemyTurn).getBoard().getBitBoard();
                }
            } else if (isCaptureOnly) {
                legalMoves &= board.getSelectedTurnBoard(enemyTurn).getBoard().getBitBoard();
            } else
                legalMoves &= color == PieceColor.WHITE ? ~board.getWhitePieces().getBoard().getBitBoard() : ~board.getBlackPieces().getBoard().getBitBoard(); //Filter moves that are attacking allay piece.


            if (pinMap.get(pos) != null)
                legalMoves &= (pinMap.get(pos));
            if (isEnPassant && pawnEnPassantPin != 0 && pos == Long.numberOfTrailingZeros(pawnEnPassantPin)) {
                return;
            }


            Bitboard bitboard = new Bitboard(legalMoves);
            bitboard.setBitPositions(); //Add all set bits to list to later loop and encode through them.

            int enPassantSquare = 0;
            if (isEnPassant) {
                if (color == PieceColor.WHITE) enPassantSquare = (Long.numberOfTrailingZeros(legalMoves) - 8) + 1;
                else enPassantSquare = (Long.numberOfTrailingZeros(legalMoves) + 8) + 1;
            }
            addMovesToLists(pos, bitboard, pieceType, promotionPiece, isDoublePawnPush, enPassantSquare, isCastleKingSide, isCastleQueenSide);
        } else {

            if (isMoveQuiet) return; //Move is quiet so no attacked squares are added
            board.getSelectedTurnBoard(color).getBoardFromPiece()[pieceType.ordinal()].getAttackedSquares().setBits(legalMoves);
            board.getSelectedTurnBoard(color).getAttackedSquares().setBits(legalMoves);

            if ((legalMoves & board.getKing(currentTurn).getBitBoard()) != 0)
                checkState.updateCheckMask(pieceType, pos);

            if (!pieceType.isLeaping()) {
                Bitboard kingBB = board.getKing(board.getTurn());
                long moves = pieceType == PieceType.ROOK ? rookMoves.get(new Dimension(pos, 0)) : (pieceType == PieceType.BISHOP) ? bishopMoves.get(new Dimension(pos, 0)) : (rookMoves.get(new Dimension(pos, 0)) | bishopMoves.get(new Dimension(pos, 0)));
                if (moves == 0)
                    moves = rookMoves.get(new Dimension(pos, 0)) | bishopMoves.get(new Dimension(pos, 0)); //?
                if ((moves & kingBB.getBitBoard()) != 0)
                    handlePins(pos, kingBB.get_lsb());
            }
        }
    }

    private void handlePins(int piecePos, int kingPos) {
        long diff = checkState.getSquaresBetweenPieces(piecePos, kingPos);
        if ((diff & board.getSelectedTurnBoard(currentTurn).getBoard().getBitBoard()) == 0) return;
        Bitboard piecesOnTheWay = new Bitboard(diff & board.getAllPieces().getBitBoard());
        if (Long.bitCount(piecesOnTheWay.getBitBoard()) > 1) {
            long currentTurnPawns = board.getSelectedTurnBoard(currentTurn).getBoardFromPiece()[PieceType.PAWN.ordinal()].getBoard().getBitBoard();
            long enemyTurnPawns = board.getSelectedTurnBoard(enemyTurn).getBoardFromPiece()[PieceType.PAWN.ordinal()].getBoard().getBitBoard();

            boolean isFirstEncounteredPieceAPawn = ((1L << piecesOnTheWay.get_lsb() & currentTurnPawns) != 0) || ((1L << piecesOnTheWay.get_lsb() & enemyTurnPawns) != 0);
            if (board.getBoardState().getEnPasantSquare() > -1 && isFirstEncounteredPieceAPawn  && (currentTurnPawns &  piecesOnTheWay.getBitBoard()) != 0) { //If the first piece on the way is enemy pawn then do enpassant check when 2 pieces (enemy pawn and current turn pawn) can disappear from same diagonal simultaneously.
                int eps = currentTurn == PieceColor.BLACK ? -7 : 7;
                int eps1 = currentTurn == PieceColor.BLACK ? -9 : 9;
                int enp = new Bitboard(piecesOnTheWay.getBitBoard() & currentTurnPawns).get_lsb();

                if (enp + eps == board.getBoardState().getEnPasantSquare()) {
                    int enPassantMadePawn = currentTurn == PieceColor.BLACK ? enp + eps + 8 : enp + eps - 8;
                    long p = 1L << enPassantMadePawn | 1L << enp;
                    if ((piecesOnTheWay.getBitBoard() & ~(p)) == 0) {
                        pawnEnPassantPin = 1L << enp;
                    }
                }
                if (enp + eps1 == board.getBoardState().getEnPasantSquare()) {
                    int enPassantMadePawn = currentTurn == PieceColor.BLACK ? enp + eps1 + 8 : enp + eps1 - 8;
                    long p = 1L << enPassantMadePawn | 1L << enp;
                    if ((piecesOnTheWay.getBitBoard() & ~(p)) == 0) {
                        pawnEnPassantPin = 1L << enp;
                    }
                }
                //There are 2 or more pieces on the way. No pins
            }
        } else {
            pinMap.put(piecesOnTheWay.get_lsb(), diff | 1L << piecePos);
        }

    }

    /**
     * Encode and adds moves to list.
     *
     * @param fromSquare        Original position to encode
     * @param legalMoves        Legal moves to loop through one by one to encode.
     * @param pieceType         Piece type to encode
     * @param isCastleKingSide
     * @param isCastleQueenSide
     */
    private void addMovesToLists(Integer fromSquare, Bitboard legalMoves, PieceType pieceType, PieceType promotionPiece, boolean isDoublePawnPush, int enPassantCaptureSquare, boolean isCastleKingSide, boolean isCastleQueenSide) {
        int pieceTypeIndex = pieceType.ordinal();
        int promotionPieceIndex = promotionPiece != null ? promotionPiece.ordinal() + 1 : 0;
        int isDoublePawnPushIndex = isDoublePawnPush ? 1 : 0;
        int isCastleKingSideIndex = isCastleKingSide ? 1 : 0;
        int isCastleQueenSideIndex = isCastleQueenSide ? 1 : 0;
        for (Integer toSquare : legalMoves.getBitSetPositions()) {
            generatedMoves.add(new Move(fromSquare | toSquare << 6 | pieceTypeIndex << 12 | board.findTypeOfPieceStandingOnSelectedSquare(toSquare) << 15 | isDoublePawnPushIndex << 18 | (promotionPieceIndex) << 21 | enPassantCaptureSquare << 24 | isCastleKingSideIndex << 30 | isCastleQueenSideIndex << 31));
        }
    }

    private void updateAttacksAndMoves() {
        //Reset check mask and double check boolean
        checkState.checkMask = 0;
        checkState.doubleCheck = false;
        checkState.checkingPiecePosition = 0;
        checkState.pieceType = null;
        pinMap.clear();
        pawnEnPassantPin = 0;
        //Set attack board to 0 (enemy turn)
        board.getSelectedTurnBoard(enemyTurn).getAttackedSquares().setBitBoard(0);


        //Update attack board (enemy turn)
        generateAttacks(enemyTurn);

        // checkState.updateCheckMasks();

        //Generate moves for current turn
        generateLegalMoves(currentTurn);
    }


    class CheckState {

        private boolean doubleCheck;
        private long checkMask; //Squares between piece and the king

        private long checkingPiecePosition;
        private PieceType pieceType;

        /**
         * Update check state.
         * @param pieceType
         * @param position
         */
        public void updateCheckMask(PieceType pieceType, int position) {
            if (checkingPiecePosition == 0) {
                checkMask = getSquaresBetweenPieces(board.getKing(currentTurn).get_lsb(), position);
                checkingPiecePosition = 1L << position;
                this.pieceType = pieceType;
            } else {
                doubleCheck = true;
            }
        }

        private long getSquaresBetweenPieces(int pieceOnePos, int pieceTwoPos) {
            Bitboard toReturn = new Bitboard();

            boolean isSameRank = false;
            boolean isSameFile = false;
            boolean isSameDiagonal = false;

            if (pieceOnePos / 8 == pieceTwoPos / 8) {
                isSameRank = true;
            } else if (pieceOnePos % 8 == pieceTwoPos % 8) {
                isSameFile = true;
            } else if (Math.abs(pieceOnePos / 8 - pieceTwoPos / 8) == Math.abs(pieceOnePos % 8 - pieceTwoPos % 8)) {
                isSameDiagonal = true;
            }

            if (isSameRank) {
                if (pieceOnePos + 1 < pieceTwoPos) {
                    for (int i = pieceOnePos + 1; i < pieceTwoPos; i++) {
                        toReturn.setBit(i);
                    }
                }
                if (pieceOnePos - 1 > pieceTwoPos) {
                    for (int i = pieceOnePos - 1; i > pieceTwoPos; i--) {
                        toReturn.setBit(i);
                    }
                }
            } else if (isSameFile) {
                if (pieceOnePos + 8 < pieceTwoPos) {
                    for (int i = pieceOnePos + 8; i < pieceTwoPos; i += 8) {
                        toReturn.setBit(i);
                    }
                }
                if (pieceOnePos - 8 > pieceTwoPos) {
                    for (int i = pieceOnePos - 8; i > pieceTwoPos; i -= 8) {
                        toReturn.setBit(i);
                    }
                }
            } else if (isSameDiagonal) {
                int x1 = pieceOnePos / 8;
                int y1 = pieceOnePos % 8;
                int x2 = pieceTwoPos / 8;
                int y2 = pieceTwoPos % 8;

                int deltaX = x2 - x1;
                int deltaY = y2 - y1;
                int signX = Integer.signum(deltaX);
                int signY = Integer.signum(deltaY);


                int x = x1 + signX;
                int y = y1 + signY;

                while (x != x2 && y != y2) {
                    int pos = x * 8 + y;
                    toReturn.setBitBoard(toReturn.getBitBoard() | 1L << pos);
                    x += signX;
                    y += signY;
                }

            }


            return toReturn.getBitBoard();
        }
    }

    /**
     * Generate moves or attacks for every single piece of a selected color.
     *
     * @param color Color of pieces that moves or attacks are generated for.
     */
    private void generateLegalMoves(PieceColor color) {
        //  boolean isWhite = color == PieceColor.WHITE;
        generateKingMoves(color);

        if (checkState.doubleCheck) return; //Is king is in double check only king moves are valid.

        generateRookMoves(color);
        generateBishopMoves(color);
        generateQueenMoves(color);

        generateKnightMoves(color);

        generatePawnMoves(color);


    }

    private void generateAttacks(PieceColor color) {
        boolean isWhite = color == PieceColor.WHITE;

        generateRookMoves(color);
        generateBishopMoves(color);
        generateQueenMoves(color);



            generateKingMoves(color);



            generateKnightMoves(color);


                generatePawnMoves(color);



        board.getUndoHistory().clear();
        board.getMoveHistory().clear();


    }

    /**
     * Get corresponding to piece pre-calculated attacks.
     * @param pieceType Type of piece
     * @param color Color of piece
     * @return pre-calculated attacks
     */
    public long[] getCorespondingToPieceMoves(PieceType pieceType, PieceColor color) {
        if (pieceType == PieceType.PAWN) {
            if (color == PieceColor.WHITE) return pawnCapturesWhite;
            else return pawnCapturesBlack;
        } else if (pieceType == PieceType.KNIGHT)
            return knightMoves;
        else
            return kingMoves;

    }
}


