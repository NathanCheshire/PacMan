import java.util.ArrayList;
import java.util.List;

public class DijkstrasPathFinding extends PathFinder {

    private static Node[][] graph;
    private static Pac pac;
    private static Ghost controlGhost;

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
    public void resfreshPath(Node[][] graph) {
        this.graph = graph;
        System.out.println("Refreshing dijkastras path and stepping towards (" + pac.getExactX() + "," + pac.getExactY() + ")");

        //path through null nodes until you get to pac if you can
        //once you get to pac, use getParent to path all the way back to the ghost
        //once at ghost figure out if the ghost's node's parent is up down left or right
        //then call the corresponding step function
    }

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
