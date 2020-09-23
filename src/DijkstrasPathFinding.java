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

    private void stepUp() {
        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() - 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() - 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() - 10.0);
        controlGhost.setExactY(controlGhost.getExactY() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepDown() {
        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() + 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() + 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() + 10.0);
        controlGhost.setExactY(controlGhost.getExactY() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepLeft() {
        Node newNode = new Node(controlGhost.getExactX() - 1, controlGhost.getExactY());
        newNode.setNodeX(controlGhost.getExactX() - 1);
        newNode.setNodeY(controlGhost.getExactY());

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateX(controlGhost.getTranslateX() - 10.0);
        controlGhost.setExactX(controlGhost.getExactX() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    private void stepRight() {
        Node newNode = new Node(controlGhost.getExactX() + 1, controlGhost.getExactY());
        newNode.setNodeX(controlGhost.getExactX() + 1);
        newNode.setNodeY(controlGhost.getExactY());

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateX(controlGhost.getTranslateX() + 10.0);
        controlGhost.setExactX(controlGhost.getExactX() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;
    }

    @Override
    public void refreshPath(Node[][] graph) {
        this.graph = graph;
        System.out.println("Refreshing dijkastras path and stepping towards (" + pac.getExactX() + "," + pac.getExactY() + ")");

        this.graph = graph;
        pathfindingGraph = new Node[40][40];

        for (int row = 0 ; row < 40 ; row++) {
            for (int col = 0 ; col < 40 ; col++) {
                Node setMe = new Node(row,col);
                setMe.setNodeX(row);
                setMe.setNodeY(col);
                setMe.setNodeType(graph[row][col].getNodeType());
                pathfindingGraph[row][col] = setMe;
            }
        }

        int startX = controlGhost.getExactX();
        int startY = controlGhost.getExactY();

        int goalX = pac.getExactX();
        int goalY = pac.getExactY();

        //todo use pathfindingGrid to find path to goalX, goalY and raw it and take step
    }

}
