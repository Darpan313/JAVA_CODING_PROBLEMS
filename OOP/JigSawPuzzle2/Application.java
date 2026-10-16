package OOP.JigSawPuzzle2;

import java.util.*;

public class Application {
    public static void main(String[] args) {
        Board board = new Board(2);
        List<Piece> pieces = List.of(
          new Piece(3, EdgeType.FLAT, EdgeType.OUTER, EdgeType.INNER, EdgeType.FLAT),
                new Piece(1, EdgeType.FLAT, EdgeType.OUTER, EdgeType.FLAT, EdgeType.OUTER),
                new Piece(2, EdgeType.INNER, EdgeType.FLAT, EdgeType.FLAT, EdgeType.OUTER),
                new Piece(4, EdgeType.INNER, EdgeType.FLAT, EdgeType.INNER, EdgeType.FLAT)
        );
        JigsawSolver jigsawSolver = new JigsawSolver(board, pieces);
        System.out.println(jigsawSolver.solve(0,0));
    }
}
