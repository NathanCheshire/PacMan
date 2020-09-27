import java.util.*;

public class MazeGenerator {

    MazeGenerator() {}

    public char[][] getMaze(int size) {
        int r = size;
        int c = size;

        StringBuilder s = new StringBuilder(c);

        for (int x = 0; x < c; x++)
            s.append('1');

        char[][] maz = new char[r][c];

        for (int x = 0; x < r; x++)
            maz[x] = s.toString().toCharArray();

        Point st = new Point((int)(Math.random() * r), (int)(Math.random() * c), null);

        maz[st.r][st.c] = '0';

        ArrayList < Point > frontier = new ArrayList<>();

        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || x != 0 && y != 0)
                    continue;
                try {
                    if (maz[st.r + x][st.c + y] == '0')
                        continue;
                }

                catch (Exception e) {
                    continue;
                }

                frontier.add(new Point(st.r + x, st.c + y, st));
            }

        Point last = null;

        while (!frontier.isEmpty()) {
            Point cu = frontier.remove((int)(Math.random() * frontier.size()));
            Point op = cu.opposite();

            try {
                if (maz[cu.r][cu.c] == '1') {
                    if (maz[op.r][op.c] == '1') {
                        maz[cu.r][cu.c] = '0';
                        maz[op.r][op.c] = '0';

                        last = op;

                        for (int x = -1; x <= 1; x++)
                            for (int y = -1; y <= 1; y++) {
                                if (x == 0 && y == 0 || x != 0 && y != 0)
                                    continue;
                                try {
                                    if (maz[op.r + x][op.c + y] == '0') continue;
                                }

                                catch (Exception e) {
                                    continue;
                                }

                                frontier.add(new Point(op.r + x, op.c + y, op));
                            }
                    }
                }
            }

            catch (Exception ignored) {}

            if (frontier.isEmpty())
                maz[last.r][last.c] = '0';

        }

        return maz;
    }

    private class Point {
        Integer r;
        Integer c;
        Point parent;

        public Point(int x, int y, Point p) {
            r = x;
            c = y;
            parent = p;
        }

        public Point opposite() {
            if (this.r.compareTo(parent.r) != 0)
                return new Point(this.r + this.r.compareTo(parent.r), this.c, this);

            if (this.c.compareTo(parent.c) != 0)

                return new Point(this.r, this.c + this.c.compareTo(parent.c), this);
            return null;
        }
    }
}