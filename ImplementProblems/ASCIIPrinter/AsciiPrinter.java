package ImplementProblems.ASCIIPrinter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AsciiPrinter {
    public record Point(int x, int y) {}

    static class Canvas {
        Map<Point, Character> grid;
        private int minX, maxX, minY, maxY;

        public Canvas() {
            this.grid = new HashMap<>();

            this.minX = Integer.MAX_VALUE;
            this.maxX = Integer.MIN_VALUE;
            this.minY = Integer.MAX_VALUE;
            this.maxY = Integer.MIN_VALUE;
        }

        private void updateBounds(int x, int y) {
            minX = Math.min(x, minX);
            maxX = Math.max(x, maxX);
            minY = Math.min(y, minY);
            maxY = Math.max(y, maxY);
        }

        public void put(int x, int y, char c) {
            grid.put(new Point(x,y), c);
            updateBounds(x,y);
        }

        public void text(int x, int y, String str) {
            for(int i=0; i<str.length(); i++) {
                put(x+i, y, str.charAt(i));
            }
        }

        public void line(int x1, int y1, int x2, int y2, char c) {
            if (x1 == x2) {
                // Vertical Line
                int startY = Math.min(y1, y2);
                int endY = Math.max(y1, y2);
                for (int y = startY; y <= endY; y++) {
                    put(x1, y, c);
                }
            } else if (y1 == y2) {
                // Horizontal Line
                int startX = Math.min(x1, x2);
                int endX = Math.max(x1, x2);
                for (int x = startX; x <= endX; x++) {
                    put(x, y1, c);
                }
            } else {
                // Ignore or throw exception for diagonal lines as per constraints
                System.err.println("Only strictly horizontal or vertical lines are supported.");
            }
        }

        public void render() {
            if(grid.isEmpty()) {
                return;
            }

            for(int y=minY; y<=maxY; y++) {
                StringBuilder row = new StringBuilder();
                for(int x=minX; x<=maxX; x++) {
                    Point p = new Point(x, y);
                    row.append(grid.getOrDefault(p, ' '));
                }
                System.out.println(row.toString().stripTrailing());
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        if(!sc.hasNextInt()) return;

        int numOfCommands = sc.nextInt();
        sc.nextLine();

        Canvas canvas = new Canvas();

        for(int i=0; i<numOfCommands; i++) {
            String line = sc.nextLine();
            if(line.isEmpty()) continue;

            String[] parts = line.split("\\s");
            String command = parts[0];

            switch (command) {
                case "PUT":
                    int putX = Integer.parseInt(parts[1]);
                    int putY = Integer.parseInt(parts[2]);
                    char putChar = parts[3].charAt(0);
                    canvas.put(putX, putY, putChar);
                    break;
                case "TEXT":
                    int textX = Integer.parseInt(parts[1]);
                    int textY = Integer.parseInt(parts[2]);
                    String textStr = line.substring(line.indexOf(parts[3]));
                    canvas.text(textX, textY, textStr);
                    break;
                case "LINE":
                    int x1 = Integer.parseInt(parts[1]);
                    int y1 = Integer.parseInt(parts[2]);
                    int x2 = Integer.parseInt(parts[3]);
                    int y2 = Integer.parseInt(parts[4]);
                    char lineChar = parts[5].charAt(0);
                    canvas.line(x1, y1, x2, y2, lineChar);
                    break;
                default:
                    System.out.println("UNKNOWN COMMAND");
            }
        }
        canvas.render();
        sc.close();
    }
}
