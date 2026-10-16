package ImplementProblems.ASCIICanvasEngine;

public class Editor {
    Canvas canvas;

    public Editor() {
        this.canvas = new Canvas(10, 6);
    }

    void drawRectangle(char ch, int leftX, int topY, int rightX, int bottomY, Color color) {
        for(int r = leftX; r<=rightX; r++) {
            for(int c=topY; c<=bottomY; c++) {
                if(r==leftX || r==rightX || c==topY || c==bottomY) {
                    canvas.placePixel(new Position(r, c), new Pixel(ch, color));
                }
            }
        }
    }

    void dragAndDrop(int selectX, int selectY, int toX, int toY) {
        Pixel p = canvas.getPixel(new Position(selectX, selectY));
        canvas.placePixel(new Position(toX, toY), p);
        canvas.clearPixel(new Position(selectX, selectY));
    }

    void eraseArea(int fromX, int toX, int fromY, int toY) {
        for(int r=fromX; r<=toX; r++) {
            for(int c=fromY; c<=toY; c++) {
                canvas.clearPixel(new Position(r,c));
            }
        }
    }

    void print() {
        canvas.render();
    }
}
