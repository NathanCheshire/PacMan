import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    //g cost is distance from start node
    //h cost is distance from end node
    //f cost = g cost + h cost

    //graph is used for moving but we path find which one we should move to using a copy of it
    private static Node[][] graph;
    private static Pac pac;
    private Ghost controlGhost;

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    private void stepUp() {
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = null;
        controlGhost.setTranslateY(controlGhost.getTranslateY() - 10.0);
        controlGhost.setExactY(controlGhost.getExactY() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepDown() {
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = null;
        controlGhost.setTranslateY(controlGhost.getTranslateY() + 10.0);
        controlGhost.setExactY(controlGhost.getExactY() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepLeft() {
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = null;
        controlGhost.setTranslateX(controlGhost.getTranslateX() - 10.0);
        controlGhost.setExactX(controlGhost.getExactX() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepRight() {
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = null;
        controlGhost.setTranslateX(controlGhost.getTranslateX() + 10.0);
        controlGhost.setExactX(controlGhost.getExactX() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    @Override
    public void refreshPath(Node[][] graph) {
        try {
            this.graph = graph;

            int startX = controlGhost.getExactX();
            int startY = controlGhost.getExactY();

            int goalX = pac.getExactX();
            int goalY = pac.getExactY();

            //todo path find from (ghostX, ghostY) to (pacX, pacY). If possible, take a step in the best direction
            //can only go through nodes that are marked as null in graph

            //first lets print our graph (row,column)
            for (int col = 0 ; col < 40 ; col++) {
                 for (int row = 0 ; row < 40 ; row++) {
                    if (graph[row][col] == null) {
                        System.out.print("- ");
                    }

                    else if (graph[row][col].getType() == Node.PAC){
                        System.out.print("* ");
                    }

                    else if (graph[row][col].getType() == Node.INKY) {
                        System.out.print("I ");
                    }

                    else if (graph[row][col].getType() == Node.BLINKY) {
                        System.out.print("B ");
                    }

                    else if (graph[row][col].getType() == Node.PINKY) {
                        System.out.print("P ");
                    }

                    else if (graph[row][col].getType() == Node.CLYDE) {
                        System.out.print("C ");
                    }

                    else if (graph[row][col].getType() == Node.WALL) {
                        System.out.print("W ");
                    }
                }

                System.out.println();
            }

            System.out.println();
            System.out.println();
            System.out.println();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
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

    //this comparator is a comp based on the Node's FCost
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
        return Math.sqrt(Math.pow((one.getNodeX() - two.getNodeX()), 2) + Math.pow((one.getNodeY() - two.getNodeY()), 2));
    }
}
