import java.util.*;

public class AStarPathFinding extends PathFinder {
    //g cost is distance from start node
    //h cost is distance from end node
    //f cost = g cost + h cost

    //graph is used for moving but we path find which one we should move to using a copy of it
    private static Node[][] graph;
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

//    class NodePriorityQueue extends PriorityQueue {
//        public boolean contains(Node node) {
//            System.out.println("inside of contains");
//            Iterator<Node> iterator = this.iterator();
//
//            while (iterator.hasNext()) {
//                Node testNode = iterator.next();
//
//                if (testNode.getNodeX() == node.getNodeX() && testNode.getNodeY() == node.getNodeY())
//                    return true;
//            }
//
//            return false;
//        }
//    }

    public class NodePriorityQueue extends PriorityQueue<Node> {
        @Override
        public boolean contains(Object obj) {
            if (!(obj instanceof Node)) {
                return false;
            }

            Node node = (Node) obj;

            System.out.println("inside of contains");

            for (Node testNode : this) {
                if (testNode.getNodeX() == node.getNodeX() && testNode.getNodeY() == node.getNodeY())
                    return true;
            }

            return false;
        }
    }

    @Override
    public void resfreshPath(Node[][] graph) {
        try {
            NodePriorityQueue pq = new NodePriorityQueue();

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

                for (int x = minNode.getNodeX() - 1 ; x < minNode.getNodeX() + 2; x++) {
                    for (int y = minNode.getNodeY() - 1 ; y < minNode.getNodeY() + 2 ; y++) {
                        if (x == minNode.getNodeX() && minNode.getNodeY() == y)
                            continue;

                        if (x > 0 && y > 0 && x < 40 && y < 40) {
                            //is a neighbor here
                            if (!pq.contains(graph[x][y])) {

//                                double g = minNode.getGCost() + Math.sqrt(Math.pow((graph[x][y].getNodeX() - minNode.getNodeX()), 2) +
//                                                   Math.pow((graph[x][y].getNodeY() - minNode.getNodeY()), 2));
//                                graph[x][y].setgCost(g);
//                                graph[x][y].setNodeParent(minNode);
//
                                graph[x][y] = new Node(x,y);
                                graph[x][y].setNodeX(x);
                                graph[x][y].setNodeY(y);
                                System.out.println("adding: " + graph[x][y]);
                                pq.add(graph[x][y]);
                            }

//                            else if (minNode.getGCost() + Math.sqrt(Math.pow((graph[x][y].getNodeX() - minNode.getNodeX()), 2) +
//                                    Math.pow((graph[x][y].getNodeY() - minNode.getNodeY()), 2)) < graph[x][y].getGCost()) {
//
//                                graph[x][y].setgCost(minNode.getGCost() + Math.sqrt(Math.pow((graph[x][y].getNodeX() - minNode.getNodeX()), 2) +
//                                        Math.pow((graph[x][y].getNodeY() - minNode.getNodeY()), 2)));
//                                graph[x][y].setNodeParent(minNode);
//                            }
                        }
                    }
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
}
