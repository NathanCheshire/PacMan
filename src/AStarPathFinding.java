import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    //g = node to start
    //h = node to goal
    //f = g + h

    //graph is used for moving but we path find which one we should move to using a copy of it
    private static Node[][] graph;
    private Node[][] pathfindingGraph;
    private static Pac pac;
    private Ghost controlGhost;

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    private void stepUp() {
        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() - 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() - 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() - 10.0);
        controlGhost.setExactY(controlGhost.getExactY() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepDown() {
        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() + 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() + 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() + 10.0);
        controlGhost.setExactY(controlGhost.getExactY() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepLeft() {
        Node newNode = new Node(controlGhost.getExactX() - 1, controlGhost.getExactY());
        newNode.setNodeX(controlGhost.getExactX() - 1);
        newNode.setNodeY(controlGhost.getExactY());

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateX(controlGhost.getTranslateX() - 10.0);
        controlGhost.setExactX(controlGhost.getExactX() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepRight() {
        Node newNode = new Node(controlGhost.getExactX() + 1, controlGhost.getExactY());
        newNode.setNodeX(controlGhost.getExactX() + 1);
        newNode.setNodeY(controlGhost.getExactY());

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateX(controlGhost.getTranslateX() + 10.0);
        controlGhost.setExactX(controlGhost.getExactX() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    @Override
    public void refreshPath(Node[][] graph) {
        try {
            this.graph = graph;
            pathfindingGraph = new Node[40][40];

            for (int row = 0 ; row < 40 ; row++) {
                for (int col = 0 ; col < 40 ; col++) {
                    Node setMe = new Node(row,col);
                    setMe.setNodeX(row);
                    setMe.setNodeY(col);
                    setMe.setNodeType(graph[row][col].getNodeType());
                    pathfindingGraph[row][col] = setMe;
                }
            }

            int startX = controlGhost.getExactX();
            int startY = controlGhost.getExactY();

            int goalX = pac.getExactX();
            int goalY = pac.getExactY();

            PriorityQueue<Node> open = new PriorityQueue<>(new NodeComparator());

            pathfindingGraph[startX][startY].setgCost(0);
            pathfindingGraph[startX][startY].setHCost(dist(pathfindingGraph[startX][startY],pathfindingGraph[goalX][goalY]));
            open.add(pathfindingGraph[startX][startY]);

            while (!open.isEmpty()) {
                Node min = open.poll();
                open.remove(min);

                System.out.println("polled: " + min + " with costs g:" + min.getGCost() + " h:" + min.getHCost());

                if (min.getNodeX() == goalX && min.getNodeY() == goalY || nextTo(min.getNodeX(), min.getNodeY(), goalX, goalY)) {
                    System.out.println("Path found");
                    pathfindingGraph[goalX][goalY].setNodeParent(min);

                    int x = pathfindingGraph[goalX][goalY].getNodeParent().getNodeX();
                    int y = pathfindingGraph[goalX][goalY].getNodeParent().getNodeY();

                    while (!nextTo(x,y,startX,startY)) {
                        Controller.showPath(x,y);

                        int copyX = x;
                        int copyY = y;

                        x = pathfindingGraph[copyX][copyY].getNodeParent().getNodeX();
                        y = pathfindingGraph[copyX][copyY].getNodeParent().getNodeY();
                    }

                    //todo color ends of path

                    //todo cells get stuck at a certain color sometimes

                    //todo only let usre move at speed of game in game timer

                    //todo take step

                    return;
                }

                for (int i = min.getNodeX() - 1 ; i < min.getNodeX() + 2 ; i++) {
                    for (int j = min.getNodeY() - 1 ; j < min.getNodeY() + 2 ; j++) {
                        if (i >= 0 && j >= 0 && i < 40 && j < 40 && pathfindingGraph[i][j].getNodeType() == Node.PATHABLE) {
                            if (i == min.getNodeX() - 1 && j == min.getNodeY() - 1)
                                continue;
                            if (i == min.getNodeX() + 1 && j == min.getNodeY() + 1)
                                continue;
                            if (i == min.getNodeX() + 1 && j == min.getNodeY() - 1)
                                continue;
                            if (i == min.getNodeX() - 1 && j == min.getNodeY() + 1)
                                continue;

                            double newH = dist(pathfindingGraph[i][j], pathfindingGraph[goalX][goalY]);

                            if (newH < pathfindingGraph[i][j].getHCost()) {
                                pathfindingGraph[i][j].setHCost(newH);
                                pathfindingGraph[i][j].setNodeParent(min);
                                pathfindingGraph[i][j].setgCost(min.getGCost() + dist(pathfindingGraph[i][j], min));

                                if (!contains(pathfindingGraph[i][j], open))
                                    open.add(pathfindingGraph[i][j]);
                            }

                            //todo what if not in grid
                            //this is where the error of not finding a path around walls comes from
                            //the algorithm doesn't want to look at nodes that have a higher score
                        }
                    }
                }
            }

            System.out.println("No path found from (" + startX + "," + startY + ") to (" + goalX + "," + goalY + ")");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //prints the visual representation of a 2d array of nodes
    private void printGraph(Node[][] NodeArr) {
        for (int col = 0 ; col < 40 ; col++) {
            for (int row = 0 ; row < 40 ; row++) {
                //only go through nodes that are Node.PATHABLE
                if (NodeArr[row][col].getNodeType() == Node.PATHABLE)
                    System.out.print("- ");

                else if (NodeArr[row][col].getNodeType() == Node.PAC)
                    System.out.print("* ");

                else if (NodeArr[row][col].getNodeType() == Node.INKY)
                    System.out.print("I ");

                else if (NodeArr[row][col].getNodeType() == Node.BLINKY)
                    System.out.print("B ");

                else if (NodeArr[row][col].getNodeType() == Node.PINKY)
                    System.out.print("P ");

                else if (NodeArr[row][col].getNodeType() == Node.CLYDE)
                    System.out.print("C ");

                else if (NodeArr[row][col].getNodeType() == Node.WALL)
                    System.out.print("W ");
            }

            System.out.println();
        }

        System.out.println("\n\n\n");
    }

    //this method tests weather or not a priority queue has a node with the same coordinates
    private boolean contains(Node testNode , PriorityQueue pq) {
        Iterator<Node> iterator = pq.iterator();

        while (iterator.hasNext()) {
            Node compare = iterator.next();
            if (compare.getNodeX() == testNode.getNodeX() && compare.getNodeY() == testNode.getNodeY()) {
                return true;
            }

        }

        return false;
    }

    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
            if (node1.getFCost() > node2.getFCost())
                return 1;
            else if (node1.getFCost() < node2.getFCost())
                return -1;
            else
                return 0;
        }
    }

    //distance function for Nodes, max should be 56.56
    private double dist(Node one, Node two) {
        if (one.getNodeX() == two.getNodeType() && one.getNodeY() == two.getNodeY()) return 0;
        return Math.sqrt(Math.pow((one.getNodeX() - two.getNodeX()), 2) + Math.pow((one.getNodeY() - two.getNodeY()), 2));
    }

    private boolean nextTo(int x1, int y1, int x2, int y2) {
        if (Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 0)
            return true;
        return Math.abs(x1 - x2) == 0 && Math.abs(y1 - y2) == 1;
    }
}
