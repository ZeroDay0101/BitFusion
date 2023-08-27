package engine;

public class Utils {
    private static String toBinaryString(long num) {
        return String.format("%" + 64 + "s", Long.toBinaryString(num)).replace(' ', '0');
    }

    public static void printBitBoard(long bitboard) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
            for (int file = 0; file < 8; file++) {
                int position = rank * 8 + file;
                stringBuilder.append((bitboard & (1L << position)) != 0 ? "1 " : "0 ");
            }
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder);
    }

    public static void displayBinaryWithZeros(int number) {
        String binaryString = Integer.toBinaryString(number);
        int leadingZeros = Integer.SIZE - binaryString.length();
        String paddedBinary = String.format("%0" + leadingZeros + "d%s", 0, binaryString);

        System.out.println(paddedBinary);
    }

    public static void displayBinaryWithZerosSH(short number) {
        String binaryString = Integer.toBinaryString(number & 0xFFFF);
        int leadingZeros = Short.SIZE - binaryString.length();
        String paddedBinary = String.format("%0" + leadingZeros + "d%s", 0, binaryString);

        System.out.println(paddedBinary);
    }
}
