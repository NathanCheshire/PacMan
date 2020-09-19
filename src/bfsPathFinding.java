import java.util.ArrayList;
import java.util.List;

public class bfsPathFinding extends PathFinder {

    private static Node[][] graph;
    private static Pac pac;
    private Ghost controlGhost;

    @Override
    public void resfreshPath() {

        //todo make a step in any cardinal direction methods for each algorithm

//        if (controlGhost.getExactY() + 1 < 40 && graph[controlGhost.getExactX()][controlGhost.getExactY() + 1] == null) {
//            graph[controlGhost.getExactX()][controlGhost.getExactY()] = null;
//            controlGhost.setTranslateY(controlGhost.getTranslateY() + 10.0);
//            controlGhost.setExactY(controlGhost.getExactY() + 1);
//            graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
//        }

//        Queue<Node> queue = new LinkedList<>();
//        controlGhost.getPosition().setVisited(true);
//        queue.add(controlGhost.getPosition());
//
//        while (!queue.isEmpty()) {
//            Node v = queue.poll();
//
//            if (v.getType() == Node.WALL)
//                continue;
//
//            if (v.getNodeX() == pac.getNodeX() && v.getNodeY() == pac.getNodeY()) {
//                Node current = v;
//                pac.getPosition().setNodeParent(v.getNodeParent());
//                current = v.getNodeParent();
//
//                while (current.getNodeX() != pac.getPosition().getNodeX() && current.getNodeY() != pac.getPosition().getNodeY()) {
//                    current.setType(Node.PATH);
//                    current = current.getNodeParent();
//                }
//            }
//
//            v.setType(Node.HAS_CHECKED);
//
//            for(Node w: getNeighbors(v)) {
//                if (!w.isVisited() && w.getType() != Node.WALL) {
//                    w.setVisited(true);
//                    w.setNodeParent(v);
//                    w.setType(Node.TO_CHECK);
//                    queue.add(w);
//                }
//            }
//        }
    }

    public bfsPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
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
