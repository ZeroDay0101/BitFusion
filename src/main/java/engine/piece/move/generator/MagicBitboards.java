package engine.piece.move.generator;

import engine.board.representation.Bitboard;
import engine.piece.PieceType;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MagicBitboards {

    public final long[] rookMagic = {0x80102040008000L, 0x8400A20001001C0L, 0x100200010090042L, 0x2080040800801000L, 0x200204850840200L, 0x200100104080200L, 0x200020001408804L, 0x8200010080402A04L, 0x11C800040002081L, 0x41804000806008L, 0x863001020010044L, 0x102000A20104201L, 0x1001008010004L, 0x400800200040080L, 0xA00808002000100L, 0x881000894422100L, 0x8288004400081L, 0x4848020004000L, 0x4101090020004010L, 0x404220010400A00L, 0xA3010008000410L, 0x180808004000200L, 0x4400098A1810L, 0x4200020000890844L, 0x10A0208080004003L, 0x2880200040005000L, 0x8420002100410010L, 0x2200080080100080L, 0x200040080800800L, 0x40080800200L, 0x4004010080800200L, 0x2000004200008104L, 0x40004262800080L, 0x30004002402001L, 0x800802000801000L, 0x20C1002009001004L, 0x2040802402800800L, 0xA0004D2001008L, 0x2040488104001002L, 0x3004082000104L, 0x802040008000L, 0x820100841254000L, 0x3820041001868020L, 0x9001011004210008L, 0x20080004008080L, 0x5100040002008080L, 0x2090508102040028L, 0x1400010040820004L, 0x121800040122A80L, 0xC204009008300L, 0x401001444200100L, 0x20815000080180L, 0x222000410082200L, 0x980040002008080L, 0x4106220110486400L, 0x211000042008100L, 0x6000144081002202L, 0x8040001B006381L, 0x88402000100901L, 0x200081000210055L, 0x102002008100402L, 0x201A000408011082L, 0x1000589008010204L, 0x80A518621004C02L};
    public final long[] bishopMagic = {0x40040844404084L, 0x2004208a004208L, 0x10190041080202L, 0x108060845042010L, 0x581104180800210L, 0x2112080446200010L, 0x1080820820060210L, 0x3c0808410220200L, 0x4050404440404L, 0x21001420088L, 0x24d0080801082102L, 0x1020a0a020400L, 0x40308200402L, 0x4011002100800L, 0x401484104104005L, 0x801010402020200L, 0x400210c3880100L, 0x404022024108200L, 0x810018200204102L, 0x4002801a02003L, 0x85040820080400L, 0x810102c808880400L, 0xe900410884800L, 0x8002020480840102L, 0x220200865090201L, 0x2010100a02021202L, 0x152048408022401L, 0x20080002081110L, 0x4001001021004000L, 0x800040400a011002L, 0xe4004081011002L, 0x1c004001012080L, 0x8004200962a00220L, 0x8422100208500202L, 0x2000402200300c08L, 0x8646020080080080L, 0x80020a0200100808L, 0x2010004880111000L, 0x623000a080011400L, 0x42008c0340209202L, 0x209188240001000L, 0x400408a884001800L, 0x110400a6080400L, 0x1840060a44020800L, 0x90080104000041L, 0x201011000808101L, 0x1a2208080504f080L, 0x8012020600211212L, 0x500861011240000L, 0x180806108200800L, 0x4000020e01040044L, 0x300000261044000aL, 0x802241102020002L, 0x20906061210001L, 0x5a84841004010310L, 0x4010801011c04L, 0xa010109502200L, 0x4a02012000L, 0x500201010098b028L, 0x8040002811040900L, 0x28000010020204L, 0x6000020202d0240L, 0x8918844842082200L, 0x4010011029020020L};
    int random_state = 1804289383;

    private final MoveGenerator moveGenerator;

    public MagicBitboards(MoveGenerator moveGenerator) {
        this.moveGenerator = moveGenerator;
    }


    private void findAndInitMagicNumber(int square, PieceType pieceType) {
        long attackMask = pieceType == PieceType.ROOK ? mask_rook_attacks(square) : mask_bishop_attacks(square);
        int bitCount = Long.bitCount(attackMask);

        //init occupancies and corresponding to those occupancies attacks.
        long[] occupancies = new long[1 << bitCount];
        long[] attacks = new long[1 << bitCount];
        Map<Integer, Long> indexedAttacks = new HashMap<>();

        MagicMoveGenerator moveGenerator = new MagicMoveGenerator();

        for (int i = 0; i < occupancies.length; i++) {
            occupancies[i] = set_occupancy(i, attackMask);
            attacks[i] = pieceType == PieceType.ROOK ? moveGenerator.generateRookMoves(square, occupancies[i]) : moveGenerator.generateBishopMoves(square, occupancies[i]);
        }

        for (int random_count = 0; random_count < 100000000; random_count++) {
            // generate magic number candidate
            long magic_number = generateMagicNumber();

            indexedAttacks.clear();
            // skip inappropriate magic numbers
            if (Long.bitCount((attackMask * magic_number) & 0xFF00000000000000L) < 6) continue;
            boolean fail = false;
            for (int i = 0; i < occupancies.length && !fail; i++) {
                int key = (int) ((occupancies[i] * magic_number) >> (64 - bitCount));

                if (indexedAttacks.get(key) == null)
                    indexedAttacks.put(key, attacks[i]);
                else {
                    fail = true;
                }
            }
            if (!fail) {
                rookMagic[square] = magic_number;
                System.out.printf("0x%08X%n", magic_number);
                return;
            }
        }
    }

    public int getKey(int square, long occupancy, PieceType pieceType) {
        long attackMask = pieceType == PieceType.ROOK ? mask_rook_attacks(square) : mask_bishop_attacks(square);
        int bitCount = Long.bitCount(attackMask);
        occupancy &= attackMask;
        long[] magic = pieceType == PieceType.ROOK ? rookMagic : bishopMagic;
        return (int) ((occupancy * magic[square]) >> 64 - bitCount);
    }

    public void initMoves(int square, PieceType pieceType) {
        long attackMask = pieceType == PieceType.ROOK ? mask_rook_attacks(square) : mask_bishop_attacks(square);
        int bitCount = Long.bitCount(attackMask);

        //init occupancies and corresponding to those occupancies attacks.
        long[] occupancies = new long[1 << bitCount];
        long[] attacks = new long[1 << bitCount];

        MagicMoveGenerator moveGenerator = new MagicMoveGenerator();

        for (int i = 0; i < occupancies.length; i++) {
            occupancies[i] = set_occupancy(i, attackMask);
            attacks[i] = pieceType == PieceType.ROOK ? moveGenerator.generateRookMoves(square, occupancies[i]) : moveGenerator.generateBishopMoves(square, occupancies[i]);
        }

        long[] magic = pieceType == PieceType.ROOK ? rookMagic : bishopMagic;
        for (int i = 0; i < occupancies.length; i++) {
            int key = (int) ((occupancies[i] * magic[square]) >> 64 - bitCount);
            Map<Dimension, Long> moveList = pieceType == PieceType.ROOK ? this.moveGenerator.getRookMoves() : this.moveGenerator.getBishopMoves();
            assert moveList.get(new Dimension(square, key)) == null;
            moveList.put(new Dimension(square, key), attacks[i]);
        }
    }

    /**
     * What this function does is replace bits in attack-mask with bits from "index" number.
     * Used in loop to generate every possible occupancy and so every possible index number until there is no more
     * numbers (possible occupancies).
     *
     * @param index
     * @param attack_mask
     * @return Occupancy bitboard.
     */
    private long set_occupancy(int index, long attack_mask) {
        long occupancy = 0L;

        int bits_in_mask = Long.bitCount(attack_mask);

        for (int i = 0; i < bits_in_mask; i++) {
            int square = Long.numberOfTrailingZeros(attack_mask);

            attack_mask &= attack_mask - 1;

            if ((index & (1 << i)) != 0) {
                occupancy |= 1L << square;
            }
        }
        return occupancy;
    }

    public long mask_rook_attacks(int square) {
        // attacks bitboard
        long attacks = 0L;

        // init files & ranks
        int f, r;

        // init target files & ranks
        int tr = square / 8;
        int tf = square % 8;

        // generate attacks
        for (r = tr + 1; r <= 6; r++) attacks |= (1L << (r * 8 + tf));
        for (r = tr - 1; r >= 1; r--) attacks |= (1L << (r * 8 + tf));
        for (f = tf + 1; f <= 6; f++) attacks |= (1L << (tr * 8 + f));
        for (f = tf - 1; f >= 1; f--) attacks |= (1L << (tr * 8 + f));

        // return attack map for bishop on a given square
        return attacks;
    }

    long mask_bishop_attacks(int square) {
        // result attacks bitboard
        long attacks = 0L;

        // init ranks & files
        int r, f;

        // init target rank & files
        int tr = square / 8;
        int tf = square % 8;

        // mask relevant bishop occupancy bits
        for (r = tr + 1, f = tf + 1; r <= 6 && f <= 6; r++, f++) attacks |= (1L << (r * 8 + f));
        for (r = tr - 1, f = tf + 1; r >= 1 && f <= 6; r--, f++) attacks |= (1L << (r * 8 + f));
        for (r = tr + 1, f = tf - 1; r <= 6 && f >= 1; r++, f--) attacks |= (1L << (r * 8 + f));
        for (r = tr - 1, f = tf - 1; r >= 1 && f >= 1; r--, f--) attacks |= (1L << (r * 8 + f));

        // return attack map
        return attacks;
    }

    long mask_queen_attacks(int square) {
        return mask_rook_attacks(square) | mask_bishop_attacks(square);
    }

    private long generateMagicNumber() {
        return get_random_long_number() & get_random_long_number() & get_random_long_number();
    }

    private long get_random_long_number() {
        // define 4 random numbers
        long n1, n2, n3, n4;

        // init random numbers slicing 16 bits from MS1B side
        n1 = (long) (get_random_U32_number()) & 0xFFFF;
        n2 = (long) (get_random_U32_number()) & 0xFFFF;
        n3 = (long) (get_random_U32_number()) & 0xFFFF;
        n4 = (long) (get_random_U32_number()) & 0xFFFF;

        // return random number
        return n1 | (n2 << 16) | (n3 << 32) | (n4 << 48);
    }

    private int get_random_U32_number() {
        // get current state
        int number = random_state;

        // XOR shift algorithm
        number ^= number << 13;
        number ^= number >> 17;
        number ^= number << 5;

        // update random number state
        random_state = number;

        // return random number
        return number;
    }

}

class MagicMoveGenerator {
    public long generateRookMoves(int square, long occupancy) {

        Bitboard occupancyBitBoard = new Bitboard(occupancy);
        occupancyBitBoard.setBitPositions();
        Bitboard legalMoves = new Bitboard(0L);
        for (int i = square + 8; i <= 63; i += 8) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }
        for (int i = square - 8; i >= 0; i -= 8) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }

        for (int i = square + 1; i < 8 - (square % 8) + square; i++) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }
        for (int i = square + -1; i >= (square / 8) * 8; i--) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }
        return legalMoves.getBitBoard();
    }

    public long generateBishopMoves(int square, long occupancy) {
        Bitboard occupancyBitBoard = new Bitboard(occupancy);
        occupancyBitBoard.setBitPositions();
        Bitboard legalMoves = new Bitboard(0L);

        // Generate moves in the northeast direction
        for (int i = square + 9; i <= 63 && i % 8 != 0; i += 9) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }

        // Generate moves in the northwest direction
        for (int i = square + 7; i <= 63 && i % 8 != 7; i += 7) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }

        // Generate moves in the southeast direction
        for (int i = square - 7; i >= 0 && i % 8 != 0; i -= 7) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }

        // Generate moves in the southwest direction
        for (int i = square - 9; i >= 0 && i % 8 != 7; i -= 9) {
            legalMoves.setBit(i);
            if (occupancyBitBoard.isBitSet(i)) break;
        }

        return legalMoves.getBitBoard();
    }


}
