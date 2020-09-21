import javafx.scene.paint.Color;

import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;

public class AStarPathFinding extends PathFinder {
    //g cost is distance from start node
    //h cost is distance from end node
    //f cost = g cost + h cost

    //graph is used for moving but we path find which one we should move to using a copy of it
    private static Node[][] graph;
    private static Pac pac;
    private Ghost controlGhost;

    AStarPathFinding(Node[][] graph, Pac pac, Ghost controlGhost) {
        this.graph = graph;
        this.pac = pac;
        this.controlGhost = controlGhost;
    }

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
        try {
            this.graph = graph;

            //todo path find from (ghostX, ghostY) to (pacX, pacY). If possible, take a step in the best direction
            int startX = controlGhost.getExactX();
            int startY = controlGhost.getExactY();

            int goalX = pac.getExactX();
            int goalY = pac.getExactY();

            Node[][] localSearchGraph = new Node[40][40];

            for (int row = 0 ; row < 40 ; row++)
                for (int col = 0 ; col < 40 ; col++)
                    if (graph[col][row] == null) {
//                        if (graph[col][row].getType() == Node.PATH)
//                            graph[col][row].setType(Node.PATHABLE);

                        localSearchGraph[row][col] = new Node(row,col);
                        localSearchGraph[row][col].setNodeX(row);
                        localSearchGraph[row][col].setNodeY(col);
                    }


            localSearchGraph[startX][startY] = new Node(startX, startY);

            Node start =  localSearchGraph[startX][startY];
            start.setNodeX(startX);
            start.setNodeY(startY);
            start.setgCost(5);
            start.setNodeParent(null);

            PriorityQueue<Node> openpq = new PriorityQueue<>(new NodeComparator());
            PriorityQueue<Node> closedpq = new PriorityQueue<>(new NodeComparator());

            openpq.add(start);
            closedpq.add(start);

            while (!openpq.isEmpty()) {
                Node min = openpq.poll();

                if (min.getNodeX() == goalX && min.getNodeY() == goalY) {
                    //draw the path if it's enabled by setting the color nodes to the PATH color
                    //take a step in the cardinal direciton needed
                    System.out.println("Path found");
                    System.out.println(localSearchGraph[startX][startY].getNodeParent().getNodeX() + "," + localSearchGraph[startX][startY].getNodeParent().getNodeY());
                    return;
                }

                closedpq.add(min);

                //for valid squares around min
                for (int i = min.getNodeX() - 1 ; i <= min.getNodeX() + 1 ; i++) {
                    for (int j = min.getNodeY() - 1 ; j <= min.getNodeY() + 1 ; j++) {
                        if (i == min.getNodeX() && j == min.getNodeY())
                            continue;

                        if ((i >= 0 && j >= 0 && i < 40 && j < 40) && localSearchGraph[i][j] != null) {
                            graph[i][j] = new Node(i,j);

                            if (graph[i][j].getType() != Node.PATH && graph[i][j].getType() == Node.PATHABLE) {
                                graph[i][j].setType(Node.PATH);
                                graph[i][j].setFill(Color.rgb(230, 153, 0, 0.5));
                            }

                            //todo make sure pacman can go through these colored path squares

                            Controller.gameDrawRoot.getChildren().remove(graph[i][j]);
                            Controller.gameDrawRoot.getChildren().add(graph[i][j]);
                        }
                    }
                }
            }

            System.out.println("Path for " + controlGhost.getType() + " DNE to (" + goalX + "," + goalY + ")");
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean contains(Node testNode , PriorityQueue pq) {
        Iterator<Node> iterator = pq.iterator();

        while (iterator.hasNext()) {
            Node compare = iterator.next();
            //System.out.println("Compare: (" + compare.getNodeX() + "," + compare.getNodeY() + ") to match (" + testNode.getNodeX() + "," + testNode.getNodeY() + ")");
            if (compare.getNodeX() == testNode.getNodeX() && compare.getNodeY() == testNode.getNodeY()) {
                //System.out.println("has it");
                return true;
            }

        }
        //System.out.println("Doesn't have it");
        return false;
    }

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

    private double dist(Node one, Node two) {
        System.out.println("Distnace: " +  Math.sqrt(Math.pow((one.getNodeX() - two.getNodeX()), 2) + Math.pow((one.getNodeY() - two.getNodeY()), 2)));
        return Math.sqrt(Math.pow((one.getNodeX() - two.getNodeX()), 2) + Math.pow((one.getNodeY() - two.getNodeY()), 2));
    }
}
