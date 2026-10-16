package OOP.JigsawPuzzle3;

import java.util.List;
import java.util.Optional;

/**
 * Solves a jigsaw puzzle given N pieces and a black-box {@link EdgeMatcher}.
 *
 * Approach
 * --------
 * This is a Constraint Satisfaction Problem solved with depth-first
 * backtracking:
 *
 *  1. Shape discovery. R and C are unknown, only R*C = N is known, so we try
 *     every factorisation (R, C) of N and attempt to fill that grid.
 *
 *  2. Backtracking fill. Cells are filled in row-major order. For each empty
 *     cell we try every unused piece in every one of its 4 rotations. A
 *     candidate is accepted only if it matches its already-placed neighbours:
 *        - the piece above:  match(above.bottom, candidate.top)
 *        - the piece to left: match(left.right,  candidate.left)
 *     The right/bottom neighbours are still empty in row-major order, so they
 *     are validated later when those cells are filled.
 *
 *  3. Pruning. Filling row-major means a piece is checked against up to two
 *     fixed neighbours the moment it is placed, so impossible branches die
 *     immediately rather than after the whole grid is laid out. Because the
 *     matcher rejects FLAT edges, flat (border) edges are automatically forced
 *     to the grid boundary — no separate border rule is needed.
 *
 * Rotation
 * --------
 * Rotation is assumed allowed. Each piece reports its current rotation
 * (0..3 clockwise quarter-turns); the orientation that satisfied the
 * constraints is recorded in the returned {@link Placement}.
 *
 * No solution
 * -----------
 * If no factorisation can be completed, {@link #solve()} returns
 * {@link Optional#empty()}. The board is always restored on backtrack, so the
 * input pieces are left in their original orientation.
 *
 * Complexity
 * ----------
 * Let N be the piece count and d(N) the number of divisors of N. The search is
 * O( d(N) * (4N)^N ) in the absolute worst case (every piece * 4 rotations at
 * every cell). In practice neighbour pruning collapses this dramatically: most
 * candidates are rejected after a single match() call, so realistic puzzles
 * resolve far below the worst-case bound.
 */
public class JigsawSolver {
    private final List<Piece> pieces;
    private final EdgeMatcher matcher;

    public JigsawSolver(List<Piece> pieces, EdgeMatcher matcher) {
        this.pieces = pieces;
        this.matcher = matcher;
    }

    /** Try every R x C factorisation of N until one grid can be filled. */
    public Optional<Placement> solve() {
        int n = pieces.size();
        if (n == 0) return Optional.empty();

        for (int rows = 1; rows <= n; rows++) {
            if (n % rows != 0) continue;
            int cols = n / rows;

            Board board = new Board(rows, cols);
            boolean[] used = new boolean[n];
            if (fill(board, new Position(0, 0), used)) {
                return Optional.of(snapshot(board));
            }
        }
        return Optional.empty();
    }

    /** Backtracking fill of the board in row-major order. */
    private boolean fill(Board board, Position pos, boolean[] used) {
        if (pos.row() == board.getRows()) {
            return true; // every cell filled
        }

        Position next = (pos.col() == board.getCols() - 1) ? pos.nextRowStart() : pos.right();

        for (int i = 0; i < pieces.size(); i++) {
            if (used[i]) continue;
            Piece piece = pieces.get(i);

            for (int r = 0; r < 4; r++) {
                if (fits(board, piece, pos)) {
                    board.placePiece(pos, piece);
                    used[i] = true;

                    if (fill(board, next, used)) {
                        return true;
                    }

                    used[i] = false;
                    board.removePiece(pos);
                }
                piece.rotate(); // 4 rotations -> back to original orientation
            }
        }
        return false;
    }

    /** A piece fits if it matches the already-placed top and left neighbours. */
    private boolean fits(Board board, Piece piece, Position pos) {
        if (pos.row() > 0) {
            Piece above = board.getPiece(pos.up());
            if (!matcher.match(above.getEdge(Direction.DOWN), piece.getEdge(Direction.UP))) {
                return false;
            }
        }
        if (pos.col() > 0) {
            Piece left = board.getPiece(pos.left());
            if (!matcher.match(left.getEdge(Direction.RIGHT), piece.getEdge(Direction.LEFT))) {
                return false;
            }
        }
        return true;
    }

    /** Capture the solved board into an immutable Placement. */
    private Placement snapshot(Board board) {
        int rows = board.getRows();
        int cols = board.getCols();
        int[][] ids = new int[rows][cols];
        int[][] rotations = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Piece p = board.getPiece(new Position(r, c));
                ids[r][c] = p.getId();
                rotations[r][c] = p.getRotation();
            }
        }
        return new Placement(rows, cols, ids, rotations);
    }
}
