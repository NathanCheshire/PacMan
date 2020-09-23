public class DijkstrasPathFinding extends PathFinder {

    private static Node[][] graph;
    private Node[][] pathfindingGraph;
    private static Pac pac;
    private static Ghost controlGhost;

    DijkstrasPathFinding(Node[][] graph, Pac pac, Ghost ghost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = ghost;
    }

    //todo dijkastras for point to point is really A* with a heuristic of 1 so make that an option
    //once A* is working make a getHeuristic function but for dijkastras it will be 1 instead of dist(n1,n2)
}
