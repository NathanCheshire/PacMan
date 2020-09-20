import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    private List<Node> open;
    private List<Node> closed;

    //g cost is distance from start node
    //h cost is distance from end node
    //f cost = g cost + h cost

    //graph is used for moving but we path find which one we should move to using a copy of it
    private static Node[][] graph;
    private Node[][] localGraph;

    private static Pac pac;
    private Ghost controlGhost;

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

    class NodeComparator implements Comparator<Node> {
        public int compare(Node n1, Node n2) {
            if (n1.getFCost() < n2.getFCost())
                return 1;
            else if (n1.getFCost() > n2.getFCost())
                return -1;
            return 0;
        }
    }

    @Override
    public void resfreshPath(Node[][] graph) {
        System.out.println("Refreshing A* path and attempting to step towards (" + pac.getExactX() + "," + pac.getExactY() + ")");

        int ghostX = controlGhost.getExactX();
        int ghostY = controlGhost.getExactY();

        int pacX = pac.getExactX();
        int pacY = pac.getExactY();

        localGraph = new Node[40][40];

        //make local graph for pathable vs non pathable squares

        for (int j = 0 ; j < graph[0].length ; j++) {
            for (int i = 0 ; i < graph.length ; i++) {
                if (graph[i][j] == null || (i == ghostX && j == ghostY)) {
                    Node add = new Node(i,j);
                    add.setType(Node.PATHABLE);
                    add.setNodeX(i);
                    add.setNodeY(j);
                    localGraph[i][j] = add;
                    System.out.print("-");
                }

                else {
                    Node add = new Node(i,j);
                    add.setType(Node.WALL);
                    add.setNodeX(i);
                    add.setNodeY(j);
                    localGraph[i][j] = add;
                    System.out.print("*");
                }
            }
            System.out.println();
        }

        //okay now use this localGraph to conduct it on, also remove the old path and draw the new one if drawpaths is enabled

        localGraph[ghostX][ghostY].setgCost(0);
        localGraph[ghostX][ghostY].setNodeParent(null);

        PriorityQueue<Node> pq = new PriorityQueue<>(new NodeComparator());
        pq.add(localGraph[ghostX][ghostY]);

        System.out.println("ghost x,y: " + ghostX + "," + ghostY);

        while (!pq.isEmpty()) {
            Node minNode = pq.poll();
            System.out.println(pq.size() + ", " + minNode.getNodeX() + "," + minNode.getNodeY());

            if (minNode.getNodeX() == pacX && minNode.getNodeY() == pacY) {
                //make a step depending on the parent of Node[controlGhost.getExactX()][controlGhost.getExactY()]
                System.out.println("Path found");
            }

            System.out.println("\n\n");

            System.out.println("min node coords before get neighbor call: " + minNode.getNodeX() + "," + minNode.getNodeY());
            for (Node neighbor : getNeighbors(minNode)) {
                System.out.println(pq.contains(neighbor));
//                if (!pq.contains(neighbor)) {
////                    double g = minNode.getGCost() + Math.sqrt(Math.pow((neighbor.getNodeX() - minNode.getNodeX()), 2) +
////                                                    Math.pow((neighbor.getNodeY() - minNode.getNodeY()), 2));
////                    neighbor.setgCost(g);
////                    neighbor.setNodeParent(minNode);
//                    pq.add(neighbor);
//                }

//                else if (minNode.getGCost() + Math.sqrt(Math.pow((neighbor.getNodeX() - minNode.getNodeX()), 2) +
//                                                        Math.pow((neighbor.getNodeY() - minNode.getNodeY()), 2)) < neighbor.getGCost()) {
//
//                    neighbor.setgCost(minNode.getGCost() + Math.sqrt(Math.pow((neighbor.getNodeX() - minNode.getNodeX()), 2) +
//                                                           Math.pow((neighbor.getNodeY() - minNode.getNodeY()), 2)));
//                    neighbor.setNodeParent(minNode);
//                }
            }
            System.out.println("\n\n");
        }

//        //if here then path does not exist

        //path through null nodes until you get to pac if you can
        //once you get to pac, use getParent to path all the way back to the ghost
        //once at ghost figure out if the ghost's node's parent is up down left or right
        //then call the corresponding step function
    }

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    //todo redo getNeighbors in other algorithms

    public List<Node> getNeighbors(Node node) {
        List<Node> ret = new ArrayList<>();
        System.out.println("in get neighbor" + node.getNodeX() + "," + node.getNodeY());

        for (int x = node.getNodeX() - 1 ; x < node.getNodeX() + 2; x++) {
            for (int y = node.getNodeY() - 1 ; y < node.getNodeY() + 2 ; y++) {
                if (x == node.getNodeX() && node.getNodeY() == 0)
                    continue;
                if (x > 0 && y > 0 && x < 40 && y < 40)
                    ret.add(localGraph[x][y]);

            }
        }
        System.out.println("\n\nneighbor size: " + ret.size() + "\n\n");

        return ret;
    }
}
