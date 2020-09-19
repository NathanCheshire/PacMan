import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class DijkstrasPathFinding extends PathFinder {

    private static Node[][] graph;
    private static Pac pac;
    private static Ghost controlGhost;

    @Override
    public void resfreshPath() {
        System.out.println(controlGhost.getType() + " update position at: " + controlGhost.getExactX() + "," + controlGhost.getExactY());

//        PriorityQueue<Node> pq = new PriorityQueue<>();
//        pac.getPosition().setDistance(0);
//        pq.add(pac.getPosition());
//
//        while (!pq.isEmpty()) {
//            Node small = pq.poll();
//
//            if (small.getType() == Node.WALL)
//                continue;
//
//            if (small.getNodeX() == pac.getNodeX() && small.getNodeY() == pac.getNodeY()) {
//                pac.getPosition().setNodeParent(small.getNodeParent());
//                break;
//            }
//
//            small.setType(Node.HAS_CHECKED);
//
//            for (Node neighbor : getNeighbors(small)) {
//                double distance = small.getDistance();
//                double alt = small.getDistance() + (Math.sqrt(Math.pow((small.getNodeX() - neighbor.getNodeX()),2) +
//                        Math.pow((small.getNodeY() - neighbor.getNodeY()),2)));
//
//                if (!pq.contains(neighbor)) {
//                    neighbor.setDistance(alt);
//                    neighbor.setNodeParent(small);
//                    neighbor.setType(Node.TO_CHECK);
//                    pq.add(neighbor);
//                }
//
//                else if (alt < distance) {
//                    neighbor.setDistance(alt);
//                    neighbor.setNodeParent(small);
//                }
//            }
//        }
//
//        Node current = pac.getPosition().getNodeParent();
//
//        while (current.getNodeX() != pac.getPosition().getNodeX() && current.getNodeY() != pac.getPosition().getNodeY()) {
//            current.setType(Node.PATH);
//            current = current.getNodeParent();
//        }
    }

    //modified dijkastras for start and goal node
    DijkstrasPathFinding(Node[][] graph, Pac pac, Ghost ghost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = ghost;
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
