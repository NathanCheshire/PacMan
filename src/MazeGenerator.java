import java.util.*;


public class MazeGenerator {

    //only method to return maze
    //returns char array so that we can use strings on it if wanted
    public char[][] getMaze(int size) {
        int xDimension = size;
        int yDimension = size;

        StringBuilder s = new StringBuilder(yDimension);

        for (int x = 0; x < xDimension; x++)
            s.append('1');

        char[][] maze = new char[xDimension][yDimension];

        for (int x = 0; x < xDimension; x++)
            maze[x] = s.toString().toCharArray();

        //randomly find a starting point
        Point start = new Point((int) (Math.random() * xDimension), (int) (Math.random() * yDimension), null);

        //Note: there is functionality in here to find a start and end point which we could use for placing pacman in controller in the future

        //start is of course pathable
        maze[start.x][start.y] = '0';

        //this is the stack that we push and pop elements from
        ArrayList<Point> nodeStack = new ArrayList<>();

        //valid neighbors of start, too lazy to add more checks so just ignore an exception
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x == 0 && y == 0 || x != 0 && y != 0)
                    continue;
                try {
                    if (maze[start.x + x][start.y + y] == '0')
                        continue;
                } catch (Exception e) {
                    continue;
                }

                nodeStack.add(new Point(start.x + x, start.y + y, start));
            }
        }

        Point last = null;

        //as long as stack has stuff
        while (!nodeStack.isEmpty()) {

            //get point and opposite of point
            Point randPoint = nodeStack.remove((int)(Math.random() * nodeStack.size()));
            Point opRandPoint = randPoint.pointOpp();

            try {
                //if it's a wall
                if (maze[randPoint.x][randPoint.y] == '1') {
                    //if opposite is also a wall
                    if (maze[opRandPoint.x][opRandPoint.y] == '1') {
                        //then we set them to pathable
                        maze[randPoint.x][randPoint.y] = '0';
                        maze[opRandPoint.x][opRandPoint.y] = '0';

                        //update last
                        last = opRandPoint;

                        //for valid enighbors
                        for (int x = -1; x <= 1; x++)
                            for (int y = -1; y <= 1; y++) {
                                if (x == 0 && y == 0 || x != 0 && y != 0)
                                    continue;

                                //if pathable ignore
                                try {
                                    if (maze[opRandPoint.x + x][opRandPoint.y + y] == '0')
                                        continue;
                                }

                                catch (Exception e) {
                                    continue;
                                }

                                //not pathable so add it to our stack
                                nodeStack.add(new Point(opRandPoint.x + x, opRandPoint.y + y, opRandPoint));
                            }
                    }
                }
            }

            catch (Exception ignored) {}

            //set the end to a path too
            if (nodeStack.isEmpty())
                maze[last.x][last.y] = '0';

        }

        return maze;
    }

    //point class (mainly for opposite function)
    private class Point {
        Integer x;
        Integer y;
        Point parent;

        public Point(int x, int y, Point p) {
            this.x = x;
            this.y = y;
            parent = p;
        }

        public Point pointOpp() {
            if (this.x.compareTo(parent.x) != 0)
                return new Point(this.x + this.x.compareTo(parent.x), this.y, this);

            if (this.y.compareTo(parent.y) != 0)
                return new Point(this.x, this.y + this.y.compareTo(parent.y), this);

            return null;
        }
    }
}