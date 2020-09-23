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

    //todo just make a A* but pass in a heuristic of 1 (make 0 use dist and case 1 use 1)
    //once A* is working make a getHeuristic function but for dijkastras it will be 1 instead of dist(n1,n2)
}
