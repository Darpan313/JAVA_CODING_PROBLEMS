package Recursion.RobotGridMaze;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        /* 0 - robot, # - maze, X - target
 (0, 0) +           +
        |0|0|0|0|0|0|
        | | | | | |0|
        |#| | | | |0|
        |#|#|#|#| |0|
        | | | | | |0|
        | | | | | |X|
        +           + (m, n)
        */

        // we store the path in a LinkedHashSet
        Set<Point> path = new LinkedHashSet<>();

        int totalRow = 6;
        int totalCol = 6;

        boolean[][] maze = new boolean[6][6];
        maze[2][0]=true;
        maze[3][0]=true;
        maze[3][1]=true;
        maze[3][2]=true;
        maze[3][3]=true;

        // we compute and display the path
        RobotGrid.computePath(0, 0, totalRow, totalCol, maze, path);

        System.out.println("Computed path (plain recursion):");
        path.forEach(System.out::println);

        // we define a Set for storing the visited cells
        Set<Point> visited = new HashSet<>();

        // we compute and display the path
        RobotGrid.computePath(0, 0, totalRow, totalCol, maze, path, visited);

        System.out.println("\nComputed path (Memoization):");
        path.forEach(System.out::println);
    }
}
