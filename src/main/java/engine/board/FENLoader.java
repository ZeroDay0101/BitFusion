package engine.board;

import engine.piece.PieceColor;
import engine.piece.PieceType;

public class FENLoader {
    private final Board board;

    public FENLoader(Board board) {
        this.board = board;
    }


    public void loadFromFen(String fen) {
        int pos = 63 - 7;
        int count = 0;
        boolean firstTimeInCol = true;
        boolean fenEnd = false;
        boolean isTurnSet = false;

        for (int i = 0; i < fen.toCharArray().length; i++) {
            char c = fen.toCharArray()[i];
            if (c == ' ') {
                fenEnd = true;
                continue;
            }
            if (Character.isDigit(c)) {
                pos += Integer.parseInt(String.valueOf(c));
            } else if (c == '/') {
                pos -= 16;
                firstTimeInCol = true;
                continue;
            } else {
                if (fenEnd) {
                    if ((c == 'w' || c == 'b') && !isTurnSet) {
                        if (c == 'w')
                            board.setTurn(PieceColor.WHITE);
                        if (c == 'b')
                            board.setTurn(PieceColor.BLACK);
                        isTurnSet = true;
                    } else if (Character.toUpperCase(c) == 'K') {
                        if (Character.isUpperCase(c)) {
                            board.getBoardState().setWhiteKingSideCastle(true);
                        } else {
                            board.getBoardState().setBlackKingSideCastle(true);
                        }
                    } else if (Character.toUpperCase(c) == 'Q') {
                        if (Character.isUpperCase(c)) {
                            board.getBoardState().setWhiteQueenSideCastle(true);
                        } else {
                            board.getBoardState().setBlackQueenSideCastle(true);
                        }
                    } else if (Character.isLetter(c)) {
                        board.getBoardState().setEnPasantSquare(positionToBit(c + String.valueOf(fen.toCharArray()[i + 1])));
                    }
                    continue;
                }

                board.getSelectedTurnBoard(Character.isUpperCase(c) ? PieceColor.WHITE : PieceColor.BLACK).getBoardFromPiece()[getPieceTypeFromChar(c).ordinal()].getBoard().setBit(pos);
                pos++;
            }
            if (firstTimeInCol) firstTimeInCol = false;


        }
    }


    private PieceType getPieceTypeFromChar(char c) {
        if (Character.toLowerCase(c) == 'p') return PieceType.PAWN;
        if (Character.toLowerCase(c) == 'r') return PieceType.ROOK;
        if (Character.toLowerCase(c) == 'n') return PieceType.KNIGHT;
        if (Character.toLowerCase(c) == 'b') return PieceType.BISHOP;
        if (Character.toLowerCase(c) == 'q') return PieceType.QUEEN;
        if (Character.toLowerCase(c) == 'k') return PieceType.KING;
        throw new IllegalArgumentException("Invalid piece character: " + c);
    }

    private int positionToBit(String position) {
        if (position == null || position.length() != 2) {
            throw new IllegalArgumentException("Invalid position string");
        }

        char col = position.charAt(0);
        char row = position.charAt(1);

        int colIndex = col - 'a';
        int rowIndex = row - '1';

        if (colIndex < 0 || colIndex > 7 || rowIndex < 0 || rowIndex > 7) {
            throw new IllegalArgumentException("Invalid position string");
        }

        // Calculate the bit position from column and row indices

        // Create a long value with the bit set at the calculated position
        return rowIndex * 8 + colIndex;
    }
}
