import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    private List<Node> open;
    private List<Node> closed;

    //g cost is distance from start node
    //h cost is distance from end node
    //f cost = g cost + h cost

    private static Node[][] graph;
    private static Pac pac;
    private Ghost controlGhost;

    //todo update path to pac if it exists, path find backwards to ghosts position, move ghost to it's node's parent

    @Override
    public void resfreshPath() {
        System.out.println(controlGhost.getType() + " update position at: " + controlGhost.getExactX() + "," + controlGhost.getExactY());

//        controlGhost.getPosition().setgCost(0);
//        controlGhost.getPosition().setNodeParent(null);
//        PriorityQueue<Node> pq = new PriorityQueue<>();
//        pq.add(controlGhost.getPosition());
//
//        while (!pq.isEmpty()) {
//            Node n = pq.poll();
//
//            if (n.getType() == Node.WALL || (n.getNodeX() == controlGhost.getNodeX() && n.getNodeY() == controlGhost.getNodeY()))
//                continue;
//
//            if (n.getNodeX() == pac.getNodeX() && n.getNodeY() == pac.getNodeY()) {
//                n = n.getNodeParent(); //avoid coloring over goal
//
//                while (n.getNodeX() != n.getNodeX() && n.getNodeY() != n.getNodeY()) {
//                    n.setType(Node.PATH);
//                    n = n.getNodeParent();
//                }
//
//                return;
//            }
//
//            n.setType(Node.HAS_CHECKED);
//
//            for (Node neighbor: getNeighbors(n)) {
//                if (!pq.contains(neighbor)) {
//                    neighbor.setgCost(n.getGCost() + Math.sqrt(Math.pow(n.getNodeX() - neighbor.getNodeX(), 2) + Math.pow(n.getNodeY() - neighbor.getNodeY(), 2)));
//                    neighbor.setNodeParent(n);
//                    pq.add(neighbor);
//                    neighbor.setType(Node.TO_CHECK);
//                }
//
//                else if (n.getGCost() + Math.sqrt(Math.pow(n.getNodeX() - neighbor.getNodeX(), 2) + Math.pow(n.getNodeY() - neighbor.getNodeY(), 2)) < neighbor.getGCost()) {
//                    neighbor.setgCost(n.getGCost() + Math.sqrt(Math.pow(n.getNodeX() - neighbor.getNodeX(), 2) + Math.pow(n.getNodeY() - neighbor.getNodeY(), 2)));
//                    neighbor.setNodeParent(n);
//                }
//            }
//        }
    }

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    public List<Node> getNeighbors(Node node) {
        List<Node> ret = new ArrayList<>();

        for (int x = -1 + node.getNodeX(); x <= 1 + node.getNodeY() ; x++) {
            for (int y = -1 + node.getNodeY(); y <= 1 + node.getNodeY() ; y++) {
                if (x == 0 && y == 0)
                    continue;
                if (x > 0 && y > 0 && x < graph.length && y < graph.length)
                    ret.add(graph[x][y]);

            }
        }

        return ret;
    }
}
