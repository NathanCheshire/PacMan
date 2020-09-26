import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    private static Node[][] graph;
    private Node[][] pathfindingGraph;
    private static Pac pac;
    private Ghost controlGhost;

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    private void stepUp() {
        int refreshX = controlGhost.getExactX();
        int refreshY = controlGhost.getExactY();

        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() - 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() - 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() - 10.0);
        controlGhost.setExactY(controlGhost.getExactY() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;

        Controller.gameDrawRoot.getChildren().remove(graph[refreshX][refreshY]);
        graph[refreshX][refreshY] = new Node(refreshX, refreshY);
        Controller.gameDrawRoot.getChildren().add(graph[refreshX][refreshY]);
    }

    private void stepDown() {
        int refreshX = controlGhost.getExactX();
        int refreshY = controlGhost.getExactY();

        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() + 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() + 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() + 10.0);
        controlGhost.setExactY(controlGhost.getExactY() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;

        Controller.gameDrawRoot.getChildren().remove(graph[refreshX][refreshY]);
        graph[refreshX][refreshY] = new Node(refreshX, refreshY);
        Controller.gameDrawRoot.getChildren().add(graph[refreshX][refreshY]);
    }

    private void stepLeft() {
        int refreshX = controlGhost.getExactX();
        int refreshY = controlGhost.getExactY();

        Node newNode = new Node(controlGhost.getExactX() - 1, controlGhost.getExactY());
        newNode.setNodeX(controlGhost.getExactX() - 1);
        newNode.setNodeY(controlGhost.getExactY());

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateX(controlGhost.getTranslateX() - 10.0);
        controlGhost.setExactX(controlGhost.getExactX() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;

        Controller.gameDrawRoot.getChildren().remove(graph[refreshX][refreshY]);
        graph[refreshX][refreshY] = new Node(refreshX, refreshY);
        Controller.gameDrawRoot.getChildren().add(graph[refreshX][refreshY]);
    }

    private void stepRight() {
        int refreshX = controlGhost.getExactX();
        int refreshY = controlGhost.getExactY();

        Node newNode = new Node(controlGhost.getExactX() + 1, controlGhost.getExactY());
        newNode.setNodeX(controlGhost.getExactX() + 1);
        newNode.setNodeY(controlGhost.getExactY());

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateX(controlGhost.getTranslateX() + 10.0);
        controlGhost.setExactX(controlGhost.getExactX() + 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;

        Controller.gameDrawRoot.getChildren().remove(graph[refreshX][refreshY]);
        graph[refreshX][refreshY] = new Node(refreshX, refreshY);
        Controller.gameDrawRoot.getChildren().add(graph[refreshX][refreshY]);
    }

    @Override
    public void refreshPath(Node[][] graph, boolean move) {
        try {
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

            PriorityQueue<Node> open = new PriorityQueue<>(new NodeComparator());

            pathfindingGraph[startX][startY].setgCost(0);
            pathfindingGraph[startX][startY].setHCost(heuristic(pathfindingGraph[startX][startY],pathfindingGraph[pac.getExactX()][pac.getExactY()]));
            open.add(pathfindingGraph[startX][startY]);

            while (!open.isEmpty()) {
                Node min = open.poll();
                open.remove(min);

                if (min.getNodeX() == pac.getExactX() && min.getNodeY() == pac.getExactY() || nextTo(min.getNodeX(), min.getNodeY(), pac.getExactX(), pac.getExactY())) {
                    pathfindingGraph[pac.getExactX()][pac.getExactY()].setNodeParent(min);

                    int x = pathfindingGraph[pac.getExactX()][pac.getExactY()].getNodeParent().getNodeX();
                    int y = pathfindingGraph[pac.getExactX()][pac.getExactY()].getNodeParent().getNodeY();

                    while (!nextTo(x,y,startX,startY)) {
                        if (Controller.drawPathsEnable) {
                            Controller.showPath(x,y);
                        }

                        int copyX = x;
                        int copyY = y;

                        x = pathfindingGraph[copyX][copyY].getNodeParent().getNodeX();
                        y = pathfindingGraph[copyX][copyY].getNodeParent().getNodeY();
                    }

                    if (move) {
                        if (startX == x && startY < y)
                            stepDown();

                        else if (startX == x && startY > y)
                            stepUp();

                        else if (startY == y && startX > x)
                            stepLeft();

                        else if (startY == y && startX < x)
                            stepRight();
                    }

                    return;
                }

                for (int i = min.getNodeX() - 1 ; i < min.getNodeX() + 2 ; i++) {
                    for (int j = min.getNodeY() - 1 ; j < min.getNodeY() + 2 ; j++) {
                        if (i >= 0 && j >= 0 && i < 40 && j < 40 && pathfindingGraph[i][j].getNodeType() == Node.PATHABLE) {
                            if (i == min.getNodeX() - 1 && j == min.getNodeY() - 1)
                                continue;
                            if (i == min.getNodeX() + 1 && j == min.getNodeY() + 1)
                                continue;
                            if (i == min.getNodeX() + 1 && j == min.getNodeY() - 1)
                                continue;
                            if (i == min.getNodeX() - 1 && j == min.getNodeY() + 1)
                                continue;

                            double newH = heuristic(pathfindingGraph[i][j], pathfindingGraph[pac.getExactX()][pac.getExactY()]);

                            if (newH < pathfindingGraph[i][j].getHCost()) {
                                pathfindingGraph[i][j].setHCost(newH);
                                pathfindingGraph[i][j].setNodeParent(min);
                                pathfindingGraph[i][j].setgCost(min.getGCost() + heuristic(pathfindingGraph[i][j], min));

                                if (!open.contains(pathfindingGraph[i][j]))
                                    open.add(pathfindingGraph[i][j]);
                            }
                        }
                    }
                }
            }

            System.out.println("No path found from " + controlGhost + " to " + pac);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //prints the visual representation of a 2d array of nodes
    private void printGraph(Node[][] NodeArr) {
        for (int col = 0 ; col < 40 ; col++) {
            for (int row = 0 ; row < 40 ; row++) {
                if (NodeArr[row][col].getNodeType() == Node.PATHABLE)
                    System.out.print("- ");

                else if (NodeArr[row][col].getNodeType() == Node.PAC)
                    System.out.print("* ");

                else if (NodeArr[row][col].getNodeType() == Node.INKY)
                    System.out.print("I ");

                else if (NodeArr[row][col].getNodeType() == Node.BLINKY)
                    System.out.print("B ");

                else if (NodeArr[row][col].getNodeType() == Node.PINKY)
                    System.out.print("P ");

                else if (NodeArr[row][col].getNodeType() == Node.CLYDE)
                    System.out.print("C ");

                else if (NodeArr[row][col].getNodeType() == Node.WALL)
                    System.out.print("W ");
            }

            System.out.println();
        }

        System.out.println("\n\n\n");
    }

    //this is properly setup
    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
            if (node1.getFCost() > node2.getFCost())
                return 1;
            else if (node1.getFCost() < node2.getFCost())
                return -1;
            else
                return 0;
        }
    }

    //this should return 1 for dijkastra's
    private double heuristic(Node one, Node two) {
        return dist(one, two);
    }

    //distance function for Nodes, max should be 56.56
    private double dist(Node one, Node two) {
        if (one.getNodeX() == two.getNodeType() && one.getNodeY() == two.getNodeY()) return 0;
        return Math.sqrt(Math.pow((one.getNodeX() - two.getNodeX()), 2) + Math.pow((one.getNodeY() - two.getNodeY()), 2));
    }

    private boolean nextTo(int x1, int y1, int x2, int y2) {
        if (Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 0)
            return true;
        return Math.abs(x1 - x2) == 0 && Math.abs(y1 - y2) == 1;
    }
}
