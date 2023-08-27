package engine.board;

public class BoardState {
    private boolean whiteKingSideCastle =  false;
    private boolean blackKingSideCastle =  false;
    private boolean whiteQueenSideCastle = false;
    private boolean blackQueenSideCastle = false;

    private int enPasantSquare = -1;

    private int halfMoveClock = 0;

    private int gameCycleClock = 0;

    public BoardState(boolean whiteKingSideCastle, boolean blackKingSideCastle, boolean whiteQueenSideCastle, boolean blackQueenSideCastle, int enPasantSquare, int halfMoveClock, int gameCycleClock) {
        this.whiteKingSideCastle = whiteKingSideCastle;
        this.blackKingSideCastle = blackKingSideCastle;
        this.whiteQueenSideCastle = whiteQueenSideCastle;
        this.blackQueenSideCastle = blackQueenSideCastle;
        this.enPasantSquare = enPasantSquare;
        this.halfMoveClock = halfMoveClock;
        this.gameCycleClock = gameCycleClock;
    }

    public BoardState() {
    }

    public boolean isWhiteKingSideCastle() {
        return whiteKingSideCastle;
    }

    public void setWhiteKingSideCastle(boolean whiteKingSideCastle) {
        this.whiteKingSideCastle = whiteKingSideCastle;
    }

    public boolean isBlackKingSideCastle() {
        return blackKingSideCastle;
    }

    public void setBlackKingSideCastle(boolean blackKingSideCastle) {
        this.blackKingSideCastle = blackKingSideCastle;
    }

    public boolean isWhiteQueenSideCastle() {
        return whiteQueenSideCastle;
    }

    public void setWhiteQueenSideCastle(boolean whiteQueenSideCastle) {
        this.whiteQueenSideCastle = whiteQueenSideCastle;
    }

    public boolean isBlackQueenSideCastle() {
        return blackQueenSideCastle;
    }

    public void setBlackQueenSideCastle(boolean blackQueenSideCastle) {
        this.blackQueenSideCastle = blackQueenSideCastle;
    }

    public int getEnPasantSquare() {
        return enPasantSquare;
    }

    public void setEnPasantSquare(int enPasantSquare) {
        this.enPasantSquare = enPasantSquare;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public void setHalfMoveClock(int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
    }

    public int getGameCycleClock() {
        return gameCycleClock;
    }

    public void setGameCycleClock(int gameCycleClock) {
        this.gameCycleClock = gameCycleClock;
    }
}
