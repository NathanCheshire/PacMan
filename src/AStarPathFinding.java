import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {

    //for updating our drawn graph
    private static Node[][] graph;

    //for conducting local path finding on
    private Node[][] pathfindingGraph;
    private static Pac pac;
    private Ghost controlGhost;

    //regular constructor
    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

    //take a step up
    private void stepUp(boolean onlyOneGhost) {
        int refreshX = controlGhost.getExactX();
        int refreshY = controlGhost.getExactY();

        Node newNode = new Node(controlGhost.getExactX(), controlGhost.getExactY() + 1);
        newNode.setNodeX(controlGhost.getExactX());
        newNode.setNodeY(controlGhost.getExactY() + 1);

        graph[controlGhost.getExactX()][controlGhost.getExactY()] = newNode;
        controlGhost.setTranslateY(controlGhost.getTranslateY() - 10.0);
        controlGhost.setExactY(controlGhost.getExactY() - 1);
        graph[controlGhost.getExactX()][controlGhost.getExactY()] = controlGhost;

        Controller.gameDrawRoot.getChildren().remove(graph[refreshX][refreshY]);
        graph[refreshX][refreshY] = new Node(refreshX, refreshY);
        Controller.gameDrawRoot.getChildren().add(graph[refreshX][refreshY]);

        if (onlyOneGhost)
            Controller.showCheckedNode(refreshX, refreshY);
    }

    //take a step down
    private void stepDown(boolean onlyOneGhost) {
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

        if (onlyOneGhost)
            Controller.showCheckedNode(refreshX, refreshY);
    }

    //take a step left
    private void stepLeft(boolean onlyOneGhost) {
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

        if (onlyOneGhost)
            Controller.showCheckedNode(refreshX, refreshY);
    }

    //take a step right
    private void stepRight(boolean onlyOneGhost) {
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

        if (onlyOneGhost)
            Controller.showCheckedNode(refreshX, refreshY);
    }

    @Override
    public void refreshPath(Node[][] graph, boolean move, boolean onlyOneGhost) {
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

            //while priority queue is not empty
            while (!open.isEmpty()) {
                Node min = open.poll();
                open.remove(min);

                if (min.getNodeX() == pac.getExactX() && min.getNodeY() == pac.getExactY() || nextTo(min.getNodeX(), min.getNodeY(), pac.getExactX(), pac.getExactY())) {
                    pathfindingGraph[pac.getExactX()][pac.getExactY()].setNodeParent(min);
                    break;
                }

                //for valid enighbors
                for (int i = min.getNodeX() - 1 ; i < min.getNodeX() + 2 ; i++) {
                    for (int j = min.getNodeY() - 1 ; j < min.getNodeY() + 2 ; j++) {
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

                        //if pathable
                        if (typeChecksOut) {
                            if (i == min.getNodeX() - 1 && j == min.getNodeY() - 1)
                                continue;
                            if (i == min.getNodeX() + 1 && j == min.getNodeY() + 1)
                                continue;
                            if (i == min.getNodeX() + 1 && j == min.getNodeY() - 1)
                                continue;
                            if (i == min.getNodeX() - 1 && j == min.getNodeY() + 1)
                                continue;

                            //calculate new H
                            double newH = heuristic(pathfindingGraph[i][j], pathfindingGraph[pac.getExactX()][pac.getExactY()]);

                            //if new H is better, update information
                            if (newH < pathfindingGraph[i][j].getHCost()) {
                                pathfindingGraph[i][j].setHCost(newH);
                                pathfindingGraph[i][j].setNodeParent(min);
                                pathfindingGraph[i][j].setgCost(min.getGCost() + heuristic(pathfindingGraph[i][j], min));

                                //if not in queue, add it
                                if (!open.contains(pathfindingGraph[i][j]))
                                    open.add(pathfindingGraph[i][j]);
                            }
                        }
                    }
                }
            }

            //if goal, draw path and step in right direction
            if (pathfindingGraph[pac.getExactX()][pac.getExactY()].getNodeParent().getNodeParent() != null) {
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
                        stepDown(onlyOneGhost);

                    else if (startX == x && startY > y)
                        stepUp(onlyOneGhost);

                    else if (startY == y && startX > x)
                        stepLeft(onlyOneGhost);

                    else if (startY == y && startX < x)
                        stepRight(onlyOneGhost);
                }

                if (onlyOneGhost) {
                    for (int i = 0 ; i < 40 ; i++) {
                        for (int j = 0 ; j < 40 ; j++) {
                            if (pathfindingGraph[i][j].getNodeParent() != null)
                                Controller.showCheckedNode(i,j);
                        }
                    }
                }

                 x = pathfindingGraph[pac.getExactX()][pac.getExactY()].getNodeParent().getNodeX();
                 y = pathfindingGraph[pac.getExactX()][pac.getExactY()].getNodeParent().getNodeY();


                while (!nextTo(x,y,startX,startY)) {
                    if (Controller.drawPathsEnable) {
                        Controller.showPath(x,y);
                    }

                    int copyX = x;
                    int copyY = y;

                    x = pathfindingGraph[copyX][copyY].getNodeParent().getNodeX();
                    y = pathfindingGraph[copyX][copyY].getNodeParent().getNodeY();
                }

                if (Controller.drawPathsEnable) {
                    Controller.showPath(x,y);
                }

                return;
            }

            //if here then there was no path
            showPopupMessage("No path found from " + controlGhost + " to " + pac,Main.primaryStage);
        }

        catch (Exception e) {
            showPopupMessage("No path found from " + controlGhost + " to " + pac,Main.primaryStage);
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

    //comp nodes based on f cost, could also use g cost
    class NodeComparator implements Comparator<Node> {
        @Override
        public int compare(Node node1, Node node2) {
            if (node1.getFCost() > node2.getFCost())
                return 1;
            else if (node1.getFCost() < node2.getFCost())
                return -1;
            else {
                if (node1.getHCost() > node2.getHCost())
                    return 1;
                else if (node1.getHCost() < node2.getHCost())
                    return -1;
                else
                    return 0;
            }
        }
    }

    //this should return 1 for dijkstras
    private double heuristic(Node one, Node two) {
        return dist(one, two);
    }

    //distance function for Nodes, max should be 56.56
    private double dist(Node one, Node two) {
        if (one.getNodeX() == two.getNodeType() && one.getNodeY() == two.getNodeY()) return 0;
        return Math.sqrt(Math.pow((one.getNodeX() - two.getNodeX()), 2) + Math.pow((one.getNodeY() - two.getNodeY()), 2));
    }

    //is one node next to another node
    private boolean nextTo(int x1, int y1, int x2, int y2) {
        if (Math.abs(x1 - x2) == 1 && Math.abs(y1 - y2) == 0)
            return true;
        return Math.abs(x1 - x2) == 0 && Math.abs(y1 - y2) == 1;
    }

    private Popup createPopup(final String message) {
        final Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);
        Label label = new Label(message);
        label.getStylesheets().add("DefaultStyle.css");
        label.getStyleClass().add("popup");
        popup.getContent().add(label);
        return popup;
    }

    private void showPopupMessage(final String message, final Stage stage) {
        final Popup popup = createPopup(message);
        popup.setOnShown(e -> {
            popup.setX(stage.getX() + 20);
            popup.setY(stage.getY() + 460);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }
}
