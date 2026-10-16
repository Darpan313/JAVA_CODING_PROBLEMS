package OOP.Game2048;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
    Board owns the grid data, knows how to slide+merge one row,
    apply move in any direction, spawn tiles, and report state
    pure game mechanics.
 */
public class Board {
    private final int size;
    private int[][] grid;
    private final Random rng;
    private int lastMoveScore = 0;      // points earned by most recent move

    public Board(int size, Random rng) {
        this.size = size;
        this.rng = (rng != null) ? rng : new Random();
        grid = new int[size][size];
    }

    private int[] collapseLeft(int[] row, int[] gained) {
        int[] tiles = new int[size];

        int n=0;
        for(int v:row) {
            if(v!=0) {
                tiles[n++] = v;
            }
        }

        int[] merged = new int[size];

        int i=0;
        int m=0;

        while(i<n) {
            if(i+1 < n && tiles[i] == tiles[i+1]) {
                int newVal = 2*tiles[i];
                merged[m++] = newVal;
                gained[0] += newVal;
                i+=2;
            }
            else {
                merged[m++] = tiles[i];
                i++;
            }
        }
        return merged;
    }

    private int[][] transpose(int[][] g) {
        int[][] out = new int[size][size];
        for(int r=0; r<size; r++) {
            for(int c=0; c<size; c++) {
                out[c][r] = g[r][c];
            }
        }
        return out;
    }

    private int[][] reverseRows(int[][] g) {
        int[][] out = new int[size][size];
        for(int r=0; r<size;r++) {
            for(int c=0; c<size; c++) {
                out[r][c] = g[r][size-1-c];
            }
        }
        return out;
    }

    public boolean move(Direction direction) {
        int[][] original = deepCopy(grid);

        int[][] g = grid;
        switch (direction) {
            case UP:
                g = transpose(g);
                break;
            case DOWN:
                g = reverseRows(transpose(g));
                break;
            case RIGHT:
                g = reverseRows(g);
                break;
            case LEFT:
                break;
        }

        int[] gained = new int[]{0};
        int[][] collapsed = new int[size][size];
        for(int r=0; r<size; r++) {
            collapsed[r] = collapseLeft(g[r], gained);
        }

        switch (direction) {
            case UP:
                collapsed = transpose(g);
                break;
            case DOWN:
                collapsed = transpose(reverseRows(g));
                break;
            case RIGHT:
                collapsed = reverseRows(g);
                break;
            case LEFT:
                break;
        }

        this.grid = collapsed;
        this.lastMoveScore = gained[0];
        return !Arrays.deepEquals(this.grid, original);
    }

    public List<int[]> emptyCells() {
        List<int[]> empties = new ArrayList<>();
        for(int r=0; r<size; r++) {
            for(int c=0; c<size; c++) {
                if(grid[r][c] == 0) empties.add(new int[]{r,c});
            }
        }
        return empties;
    }

    public boolean spawnTile() {
        List<int[]> empties = emptyCells();
        if(empties.isEmpty()) return false;
        int[] cell = empties.get(rng.nextInt(empties.size()));
        grid[cell[0]][cell[1]] = (rng.nextDouble()<0.1) ? 4 : 2;
        return true;
    }

    public boolean canMove() {
        if(!emptyCells().isEmpty()) return true;
        for(int r=0; r<size; r++) {
            for(int c=0; c<size; c++) {
                int v = grid[r][c];
                if(c+1 < size && grid[r][c+1] == v) return true;
                if(r+1 < size && grid[r+1][c] == v) return true;
            }
        }
        return false;
    }

    private int[][] deepCopy(int[][] g) {
        int[][] copy = new int[size][size];
        for(int r=0; r<size; r++) copy[r] = g[r].clone();
        return copy;
    }
}
