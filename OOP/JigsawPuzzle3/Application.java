package OOP.JigsawPuzzle3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Demo driver for the edge-match jigsaw solver.
 *
 * It builds a genuinely solvable R x C grid in which every interior seam has a
 * UNIQUE complementary code, then scrambles the pieces (order + rotation) so
 * the solver has to rediscover the arrangement using only the black-box
 * {@link EdgeMatcher}. Unique seams mean the original R x C is the only shape
 * that can be assembled, so the solver reconstructs the true picture.
 */
public class Application {

    public static void main(String[] args) {
        EdgeMatcher matcher = new ComplementaryEdgeMatcher();

        // --- Solvable case: 12 pieces that form a 3 x 4 grid -----------------
        System.out.println("=== N=12 (expecting a 3x4 grid) ===");
        List<Piece> pieces = scramble(generateGrid(3, 4), new Random(42));
        Optional<Placement> result = new JigsawSolver(pieces, matcher).solve();
        printResult(result);

        // --- No-solution case: pieces that cannot connect to one another -----
        System.out.println("\n=== Unsolvable set (no factorisation fits) ===");
        List<Piece> broken = List.of(
                new Piece(1, Edge.FLAT, Edge.FLAT, Edge.FLAT, Edge.FLAT),
                new Piece(2, Edge.FLAT, Edge.FLAT, Edge.FLAT, Edge.FLAT),
                new Piece(3, Edge.FLAT, Edge.FLAT, Edge.FLAT, Edge.FLAT)
        );
        printResult(new JigsawSolver(new ArrayList<>(broken), matcher).solve());
    }

    private static void printResult(Optional<Placement> result) {
        if (result.isPresent()) {
            System.out.print(result.get());
        } else {
            System.out.println("No solution exists for the given pieces.");
        }
    }

    /**
     * Build a solvable grid. Each interior seam gets a unique code k on one
     * side and -k on the other, so adjacent pieces connect only along their
     * intended seam. The outer border is FLAT (code 0). Edges are listed in
     * (up, right, down, left) order.
     */
    private static List<Piece> generateGrid(int rows, int cols) {
        int[][] up = new int[rows][cols];
        int[][] right = new int[rows][cols];
        int[][] down = new int[rows][cols];
        int[][] left = new int[rows][cols];

        int code = 1;
        // Vertical seams: right edge of (r,c) connects left edge of (r,c+1).
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                right[r][c] = code;
                left[r][c + 1] = -code;
                code++;
            }
        }
        // Horizontal seams: bottom edge of (r,c) connects top edge of (r+1,c).
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols; c++) {
                down[r][c] = code;
                up[r + 1][c] = -code;
                code++;
            }
        }

        List<Piece> pieces = new ArrayList<>();
        int id = 1;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pieces.add(new Piece(id++, up[r][c], right[r][c], down[r][c], left[r][c]));
            }
        }
        return pieces;
    }

    /** Shuffle piece order and apply a random rotation to each piece. */
    private static List<Piece> scramble(List<Piece> pieces, Random rng) {
        Collections.shuffle(pieces, rng);
        for (Piece p : pieces) {
            int turns = rng.nextInt(4);
            for (int i = 0; i < turns; i++) p.rotate();
        }
        return pieces;
    }
}
