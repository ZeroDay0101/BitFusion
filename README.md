♟️ Java Chess Move Generator (Magic Bitboards)
A high-performance chess move generator written in Java, utilizing magic bitboards, bitshifting, and precomputed magic numbers to efficiently generate legal moves, especially for sliding pieces like rooks, bishops, and queens.

🚀 Key Features

⚡ Fast move generation using 64-bit long bitboards

🧠 Magic bitboards for constant-time rook and bishop attack generation

🔧 Bitwise operations and shifting for optimal performance

📐 Modular, clean Java architecture ready for engine integration

✅ Legal move filtering – ensures moves don’t leave the king in check

🧪 Perft testing framework – verifies move correctness via node counting

🔗 UCI move serialization – outputs standard strings like e2e4, e7e8q

🧪 Six JUnit tests featuring complex, bug-catching positions to ensure accuracy and robustness

🤖 Suitable for integration into chess engines and AI

📚 What Are Magic Bitboards?
Magic Bitboards are a technique to speed up attack generation for sliding pieces. Instead of scanning all directions manually, this method:

Masks relevant squares around a piece.

Multiplies the occupancy by a special "magic number".

Shifts the result to generate a unique index.

Uses that index to look up a precomputed attack bitboard.

This enables near-constant time move generation with no loops.

🛠

✅ Pawns (non-magic, bitwise forward capture/generate)

✅ Knights (bitmask lookups)

✅ Kings (bitmask lookups)

✅ Bishops (magic bitboards)

✅ Rooks (magic bitboards)

✅ Queens (combo of rook + bishop)

⚡ Performance

Rook/Bishop/Queen moves in O(1) time

📝 License
MIT License – free to use, modify, and distribute.

