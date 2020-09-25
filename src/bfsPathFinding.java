public class bfsPathFinding extends PathFinder {

    private static Node[][] graph;
    private static Pac pac;
    private Ghost controlGhost;

    @Override
    public void refreshPath(Node[][] graph, boolean move) {
        this.graph = graph;
        System.out.println("Refreshing bfs path and stepping towards (" + pac.getExactX() + "," + pac.getExactY() + ")");

        //path through null nodes until you get to pac if you can
        //once you get to pac, use getParent to path all the way back to the ghost
        //once at ghost figure out if the ghost's node's parent is up down left or right
        //then call the corresponding step function
    }

    public bfsPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }
}
