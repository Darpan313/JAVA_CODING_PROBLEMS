package OOP.JigSawPuzzle2;

public class test {

//        private boolean solve(int row, int col) {
//            if (row == board.getSize()) {
//                return true; // Whole puzzle successfully filled
//            }
//
//            // Calculate next cell positions
//            int nextRow = (col == board.getSize() - 1) ? row + 1 : row;
//            int nextCol = (col == board.getSize() - 1) ? 0 : col + 1;
//
//            for (int i = 0; i < remainingPieces.size(); i++) {
//                Piece piece = remainingPieces.get(i);
//
//                // Try all 4 rotations for the piece
//                for (int r = 0; r < 4; r++) {
//                    if (isValidPlacement(piece, row, col)) {
//                        board.placePiece(piece, row, col);
//                        remainingPieces.remove(i);
//
//                        if (solve(nextRow, nextCol)) {
//                            return true;
//                        }
//
//                        // Backtrack
//                        remainingPieces.add(i, piece);
//                        board.removePiece(row, col);
//                    }
//                    piece.rotate();
//                }
//            }
//            return false;
//        }
//
//        private boolean isValidPlacement(Piece piece, int row, int col) {
//            // Check structural outer boundary constraints
//            if (row == 0 && piece.getEdge(Direction.NORTH).getType() != EdgeType.FLAT) return false;
//            if (col == 0 && piece.getEdge(Direction.WEST).getType() != EdgeType.FLAT) return false;
//            if (row == board.getSize() - 1 && piece.getEdge(Direction.SOUTH).getType() != EdgeType.FLAT) return false;
//            if (col == board.getSize() - 1 && piece.getEdge(Direction.EAST).getType() != EdgeType.FLAT) return false;
//
//            // Check fit with existing top neighbor
//            if (row > 0) {
//                Piece topNeighbor = board.getPiece(row - 1, col);
//                if (topNeighbor != null && !piece.getEdge(Direction.NORTH).fitsWith(topNeighbor.getEdge(Direction.SOUTH))) {
//                    return false;
//                }
//            }
//
//            // Check fit with existing left neighbor
//            if (col > 0) {
//                Piece leftNeighbor = board.getPiece(row, col - 1);
//                if (leftNeighbor != null && !piece.getEdge(Direction.WEST).fitsWith(leftNeighbor.getEdge(Direction.EAST))) {
//                    return false;
//                }
//            }
//
//            return true;
//        }

}
