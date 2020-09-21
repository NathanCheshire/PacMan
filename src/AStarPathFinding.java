import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    //g cost is distance from start node
    //h cost is distance from end node
    //f cost = g cost + h cost

    //graph is used for moving but we path find which one we should move to using a copy of it
    private static Node[][] graph;
    private Node[][] localGraph;
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

            //first lets print our graph [r,c]
            printGraph(graph);

            //todo 1: path find from (ghostX, ghostY) to (pacX, pacY).
            //todo 2: if theres a path, draw it from the edge of the ghost to the edge of pac man
            //todo 3: take a step in the right direction

            //open and closed queues so we do not check a node more than once for A*
            PriorityQueue<Node> open = new PriorityQueue<>(new NodeComparator());
            PriorityQueue<Node> closed = new PriorityQueue<>(new NodeComparator());

            //local graph init so that we can set parents and g costs without changing the main graph
            localGraph = graph;
            localGraph[startX][startY].setgCost(0);
            open.add(localGraph[startX][startY]);

            while (!open.isEmpty()) {
                Node min = open.poll();

                if (min.getNodeX() == goalX && min.getNodeY() == goalY) {
                    System.out.println("Path found");
                    //todo reconstruct path
                    return;
                }

                closed.add(min);

                for (int i = min.getNodeX() - 1 ; i < min.getNodeX() + 2 ; i++) {
                    for (int j = min.getNodeY() - 1 ; j < min.getNodeY() + 2 ; j++) {
                        if (i >= 0 && j >= 0 && i < 40 && j < 40 && localGraph[i][j].getNodeType() == Node.PATHABLE) {
                            //valid neighbors are found here
                            System.out.println("Neighbor for (" + min.getNodeX() + "," + min.getNodeY() + ") found: (" + i + "," + j + ")");

                            if (!contains(localGraph[i][j], open) && !contains(localGraph[i][j],closed)) {
                                localGraph[i][j].setgCost(min.getGCost() + dist(min, localGraph[i][j]));
                                localGraph[i][j].setNodeParent(min);
                                open.add(localGraph[i][j]);
                            }

                            else if (min.getGCost() + dist(min, localGraph[i][j]) < localGraph[i][j].getGCost()) {
                                localGraph[i][j].setgCost(min.getGCost() + dist(min, localGraph[i][j]));
                                localGraph[i][j].setNodeParent(min);
                            }
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
