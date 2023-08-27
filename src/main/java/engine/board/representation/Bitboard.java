package engine.board.representation;

import java.util.ArrayList;
import java.util.List;

/**
 * Bitboard representation with helper methods
 */
public class Bitboard {
    private long bitBoard = 0L;

    private List<Integer> bitSetPositions = new ArrayList<>();

    public List<Integer> getBitSetPositions() {
        return bitSetPositions;
    }

    public void setBitSetPositions(List<Integer> bitSetPositions) {
        this.bitSetPositions = bitSetPositions;
    }

    public Bitboard(long bitBoard) {
        this.bitBoard = bitBoard;

        // setBitPositions();
    }

    public Bitboard() {

    }

    public long getBitBoard() {
        return bitBoard;
    }


    public void setBitBoard(long bitBoard) {
        this.bitBoard = bitBoard;
    }

    public void setBit(int square) {
        bitBoard |= 1L << square;
    }

    public void setBits(long bitBoard) {
        this.bitBoard |= bitBoard;
    }

    public void clearBit(int square) {
        bitBoard &= ~(1L << square);
    }

//    public boolean getBit(int square) {
//        return ((bitBoard >> square) & 1L) == 1L;
//    }

    public boolean isBitSet(int position) {
        return ((bitBoard & (1L << position)) != 0);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                int position = rank * 8 + file;
                stringBuilder.append((bitBoard & (1L << position)) != 0 ? "1 " : "0 ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public int get_lsb() {
        return Long.numberOfTrailingZeros(bitBoard);
    }

    /**
     * This method is used to get all bits in this bb and put it inside a list located in this object so later it can be used to quickly loop through bb.
     */
    public void setBitPositions() {
        bitSetPositions.clear();

        //double x = (double)(bitBoard & -bitBoard);
        //int exp = (int) (Double.doubleToLongBits(x) >>> 52);
        //bitSetPositions.add((exp & 2047) - 1023);

        long bitBoard = this.bitBoard;
        int count = Long.bitCount(bitBoard);
        for (int i = 0; i < count; i++) {
            double x = (double) (bitBoard & -bitBoard);
            int exp = (int) (Double.doubleToLongBits(x) >>> 52);
            bitSetPositions.add((exp & 2047) - 1023);
            bitBoard &= bitBoard - 1;
        }
    }
}
