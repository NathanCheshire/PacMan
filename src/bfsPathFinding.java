import java.util.LinkedList;

public class bfsPathFinding extends PathFinder {
    private static Node[][] graph;
    private Node[][] pathfindingGraph;
    private static Pac pac;
    private Ghost controlGhost;

    bfsPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
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


    //search algorithm pseudocode that was followed was taken directly from slides
    //anything is is game checks or necessary to make it work in this (Pac-Man) context.
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

            LinkedList<Node> open = new LinkedList<>();
            pathfindingGraph[startX][startY].setVisited(true);
            open.add(pathfindingGraph[startX][startY]);

            while (!open.isEmpty()) {
                Node polledNode = open.poll();
                open.remove(polledNode);

                if (polledNode.getNodeX() == pac.getExactX() && polledNode.getNodeY() == pac.getExactY() || nextTo(polledNode.getNodeX(), polledNode.getNodeY(), pac.getExactX(), pac.getExactY())) {
                    pathfindingGraph[pac.getExactX()][pac.getExactY()].setNodeParent(polledNode);

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

                for (int i = polledNode.getNodeX() - 1 ; i < polledNode.getNodeX() + 2 ; i++) {
                    for (int j = polledNode.getNodeY() - 1 ; j < polledNode.getNodeY() + 2 ; j++) {
                        if (i < 0 || j < 0 || i > 39 || j > 39)
                            continue;

                        int type = pathfindingGraph[i][j].getNodeType();
                        boolean typeChecksOut = false;

                        if (type == Node.CLYDE)
                            typeChecksOut = true;
                        if (type == Node.INKY)
                            typeChecksOut = true;
                        if (type == Node.BLINKY)
                            typeChecksOut = true;
                        if (type == Node.PINKY)
                            typeChecksOut = true;
                        if (type == Node.PATHABLE)
                            typeChecksOut = true;

                        if (typeChecksOut) {
                            if (i == polledNode.getNodeX() - 1 && j == polledNode.getNodeY() - 1)
                                continue;
                            if (i == polledNode.getNodeX() + 1 && j == polledNode.getNodeY() + 1)
                                continue;
                            if (i == polledNode.getNodeX() + 1 && j == polledNode.getNodeY() - 1)
                                continue;
                            if (i == polledNode.getNodeX() - 1 && j == polledNode.getNodeY() + 1)
                                continue;

                            if (!pathfindingGraph[i][j].isVisited()) {
                                pathfindingGraph[i][j].setVisited(true);
                                pathfindingGraph[i][j].setNodeParent(polledNode);
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

    private boolean nextTo(int x1, int y1, int x2, int y2) {
        if (Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 0)
            return true;
        return Math.abs(x1 - x2) == 0 && Math.abs(y1 - y2) == 1;
    }
}
