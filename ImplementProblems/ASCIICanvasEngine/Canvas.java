package ImplementProblems.ASCIICanvasEngine;

public class Canvas {
    int row;
    int col;
    Pixel[][] pixels;

    public Canvas(int row, int col) {
        this.row = row;
        this.col = col;
        this.pixels = new Pixel[row][col];
    }

    public Pixel getPixel(Position pos) {
        if(isValid(pos)) {
            return pixels[pos.getX()][pos.getY()];
        }
        throw new ASCIICanvasEngineException("Out of bound row/col");
    }

    void placePixel(Position pos, Pixel pixel) {
        if(isValid(pos)) {
            pixels[pos.getX()][pos.getY()] = pixel;
        }
        else {
            throw new ASCIICanvasEngineException("Out of bound row/col");
        }
    }

    void clearPixel(Position pos) {
        pixels[pos.getX()][pos.getY()] = null;
    }

    void render() {
        for(int i=0; i<row; i++) {
            for(int j=0; j<col; j++) {
                Pixel p = pixels[i][j];
                if(p==null) {
                    System.out.printf("%c%s", ' ', Color.NONE.getCode());
                }
                else {
                    System.out.printf("%s%c%s", p.getColor().getCode(), p.getC(), Color.NONE.getCode());
                }
                System.out.printf(" | ");
            }
            System.out.println();
        }
    }

    private boolean isValid(Position pos) {
        return pos.getX()>=0 && pos.getX()<row && pos.getY()>=0 && pos.getY()<col;
    }
}
