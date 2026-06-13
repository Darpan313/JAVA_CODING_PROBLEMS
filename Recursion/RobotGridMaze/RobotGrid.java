package Recursion.RobotGridMaze;

import java.awt.*;
import java.util.Set;

public class RobotGrid {
    private RobotGrid() {
        throw new AssertionError("Cannot be instantiated");
    }

    public static boolean computePath(int m, int n, int r, int c, boolean[][]maze, Set<Point> path) {
        if(path == null || maze == null) {
            throw new IllegalArgumentException("Path and maze cannot be null");
        }

        if(m>=r || n>=c) {
            return false;
        }

        if(maze[m][n]) {
            return false;
        }

        if(((m==r-1) && (n==c-1) ||
                computePath(m, n+1, r, c, maze, path) ||
                computePath(m+1, n, r, c, maze, path))) {
            path.add(new Point(m,n));
            return true;
        }
        return false;
    }

    public static boolean computePath(int m, int n, int r, int c, boolean[][] maze, Set<Point> path, Set<Point> visitFailed) {
        if(path == null || maze == null || visitFailed == null) {
            throw new IllegalArgumentException("Path, maze, and visitFailed cannot be null");
        }

        if(m>=r || n>=c) {
            return false;
        }

        if(maze[m][n]) {
            return false;
        }

        Point cell = new Point(m,n);

        if(visitFailed.contains(cell)) {
            return false;
        }

        if(((m==r-1) && (n==c-1)) ||
        computePath(m, n+1, r, c, maze, path, visitFailed) ||
        computePath(m+1, n, r, c, maze, path, visitFailed)) {
            path.add(cell);
            return true;
        }

        visitFailed.add(cell);
        return false;
    }
}
