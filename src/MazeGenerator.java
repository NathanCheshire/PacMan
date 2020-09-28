import java.util.*;

public class MazeGenerator {

    MazeGenerator() {}

    public char[][] getMaze(int size) {
        int xDimension = size;
        int yDimension = size;

        StringBuilder s = new StringBuilder(yDimension);

        for (int x = 0; x < yDimension; x++)
            s.append('1'); //wall

        char[][] maze = new char[xDimension][yDimension];

        for (int x = 0; x < xDimension; x++)
            maze[x] = s.toString().toCharArray();

        Point start = new Point((int)(Math.random() * xDimension), (int)(Math.random() * yDimension), null);
        maze[start.x][start.y] = '0';
        ArrayList <Point> front = new ArrayList<>();

        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || x != 0 && y != 0)
                    continue;
                try {
                    if (maze[start.x + x][start.y + y] == '0')
                        continue;
                }

                catch (Exception e) {
                    continue;
                }

                front.add(new Point(start.x + x, start.y + y, start));
            }

        Point last = null;

        while (!front.isEmpty()) {
            Point cu = front.remove((int)(Math.random() * front.size()));
            Point op = cu.opposite();

            try {
                if (maze[cu.x][cu.y] == '1') {
                    if (maze[op.x][op.y] == '1') {
                        maze[cu.x][cu.y] = '0';
                        maze[op.x][op.y] = '0';

                        last = op;

                        for (int x = -1; x <= 1; x++)
                            for (int y = -1; y <= 1; y++) {
                                if (x == 0 && y == 0 || x != 0 && y != 0)
                                    continue;
                                try {
                                    if (maze[op.x + x][op.y + y] == '0') continue;
                                }

                                catch (Exception e) {
                                    continue;
                                }

                                front.add(new Point(op.x + x, op.y + y, op));
                            }
                    }
                }
            }

            catch (Exception ignored) {}

            if (front.isEmpty())
                maze[last.x][last.y] = '0';

        }

        return maze;
    }

    private class Point {
        Integer x;
        Integer y;
        Point parent;

        public Point(int x, int y, Point p) {
            this.x = x;
            this.y = y;
            parent = p;
        }

        public Point opposite() {
            if (this.x.compareTo(parent.x) != 0)
                return new Point(this.x + this.x.compareTo(parent.x), this.y, this);

            if (this.y.compareTo(parent.y) != 0)

                return new Point(this.x, this.y + this.y.compareTo(parent.y), this);
            return null;
        }
    }
}