package ImplementProblems.ASCIICanvasEngine;

public class Application {
    public static void main(String[] args) {
        Editor editor = new Editor();
        editor.drawRectangle('*', 1, 1, 4, 3, Color.RED);
        editor.print();
        System.out.println("-------------------------------");
        editor.dragAndDrop(1, 1, 3, 2);
        editor.print();
        System.out.println("-------------------------------");
        editor.eraseArea(3,4,2,2);
        editor.print();
        System.out.println("-------------------------------");
    }
}
