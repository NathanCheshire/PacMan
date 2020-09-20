import java.util.*;

public class AStarPathFinding extends PathFinder {
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

    class nodeComparator implements Comparator<Node>{
        @Override
        public int compare(Node n1, Node n2) {
            if (n1.getGCost() > n2.getGCost())
                return 1;
            else if (n1.getGCost() < n2.getGCost())
                return -1;
            return 0;
        }
    }

    @Override
    public void resfreshPath(Node[][] graph) {
        try {
            PriorityQueue<Node> pq = new PriorityQueue<>(new nodeComparator());

            int ghostX = controlGhost.getExactX();
            int ghostY = controlGhost.getExactY();

            int pacX = pac.getExactX();
            int pacY = pac.getExactY();

            //todo use localGraph and conduct A* on, also remove the old path and draw the new one if drawpaths is enabled for the main grid

            Node startNode = graph[ghostX][ghostY];
            startNode.setgCost(0);
            startNode.setNodeParent(null);
            pq.add(startNode);

            //while priority queue is not empty
            while (!pq.isEmpty()) {
                Node minNode = pq.poll();

                if (minNode.getNodeX() == pacX && minNode.getNodeY() == pacY) {
                    //make a step depending on the parent of Node[controlGhost.getExactX()][controlGhost.getExactY()]
                    System.out.println("Path found");
                }


                for (Node neighbor : getNeighbors(minNode)) {
                    System.out.println(neighbor.getNodeX() + "," + neighbor.getNodeY());
                    if (!pq.contains(neighbor)) {
//                    double g = minNode.getGCost() + Math.sqrt(Math.pow((neighbor.getNodeX() - minNode.getNodeX()), 2) +
//                                                    Math.pow((neighbor.getNodeY() - minNode.getNodeY()), 2));
//                    neighbor.setgCost(g);
//                    neighbor.setNodeParent(minNode);
                      pq.add(neighbor);
                    }

//                else if (minNode.getGCost() + Math.sqrt(Math.pow((neighbor.getNodeX() - minNode.getNodeX()), 2) +
//                                                        Math.pow((neighbor.getNodeY() - minNode.getNodeY()), 2)) < neighbor.getGCost()) {
//
//                    neighbor.setgCost(minNode.getGCost() + Math.sqrt(Math.pow((neighbor.getNodeX() - minNode.getNodeX()), 2) +
//                                                           Math.pow((neighbor.getNodeY() - minNode.getNodeY()), 2)));
//                    neighbor.setNodeParent(minNode);
//                }
                }
            }

            System.out.println("Path DNE");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    //todo redo getNeighbors in other algorithms

    //todo make sure this returns the whole 8 if it can
    public List<Node> getNeighbors(Node node) {
        List<Node> ret = new ArrayList<>();

        for (int x = node.getNodeX() - 1 ; x < node.getNodeX() + 2; x++) {
            for (int y = node.getNodeY() - 1 ; y < node.getNodeY() + 2 ; y++) {
                if (x == node.getNodeX() && node.getNodeY() == y)
                    continue;

                if (x > 0 && y > 0 && x < 40 && y < 40) {
                    graph[x][y] = new Node(x,y);
                    graph[x][y].setNodeX(x);
                    graph[x][y].setNodeY(y);
                    System.out.println("Neighbor found: " + graph[x][y].getNodeX() + "," + graph[x][y].getNodeY());
                    ret.add(graph[x][y]);
                }
            }
        }

        return ret;
    }
}
