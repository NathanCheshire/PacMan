import javafx.scene.paint.Color;

public class Node extends javafx.scene.shape.Rectangle {
    //used for gscore
    public static final int INFINITY = Integer.MAX_VALUE;

    //node types and our nodeType
    public static final int WALL = 0;
    public static final int PATH = 3;
    public static final int PATHABLE = 4;

    public static final int INKY = 5;
    public static final int BLINKY = 6;
    public static final int PINKY = 7;
    public static final int CLYDE = 8;

    public static final Color wallColor = javafx.scene.paint.Color.rgb(26,40,70,1);
    public static final Color lineColor = javafx.scene.paint.Color.rgb(0,0,0,1);

    public static final int PAC = 9;

    //default nodeType is pathable
    private int nodeType = PATHABLE;

    //used by both
    private Node parent;

    //used by dijkastras and bfs
    private double distance;

    //used by maze generator;
    public boolean mazeVisited;
    public boolean north;
    public boolean south;
    public boolean east;
    public boolean west;

    //used by bfs
    private boolean visited;

    //used by A*
    private double hCost = Integer.MAX_VALUE;
    private double gCost = Integer.MAX_VALUE;

    Node (int x, int y) {
        super(x* 10,y * 10,10,10);
        this.setFill(Color.rgb(0, 0, 0,0));
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getNodeX() {
        return (int) (this.getTranslateX() / 10.0);
    }

    public void setNodeX(int x) {
        this.setTranslateX(x * 10);
    }

    public int getNodeY() {
        return (int) (this.getTranslateY() / 10.0);
    }
    public void setNodeY(int y) {
        this.setTranslateY(y * 10);
    }

    public Node getNodeParent() {
        return parent;
    }

    public void setNodeParent(Node parent) {
        this.parent = parent;
    }

    public double getGCost() {
        return this.gCost;
    }

    public void setgCost(double gCost) {
        this.gCost = gCost;
    }

    public double getHCost() {
        return this.hCost;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
    }

    public double getFCost() {
        return this.getGCost() + this.getHCost();
    }

    public int getNodeType() { return this.nodeType; }
    public void setNodeType(int type) {
        this.nodeType = type;

        if (type == Node.WALL)
            this.setFill(Node.wallColor);

        else if (type == Node.PATH)
            this.setFill(Color.rgb(0, 0, 0,0));
    }

    public boolean isVisited() { return this.visited; }
    public void setVisited(boolean visited) { this.visited = visited; }

    private String getTypeName() {
        switch (nodeType) {
            case 0:
                return "wall";
            case 1:
                return "has checked";
            case 2:
                return "to check";
            case 3:
                return "path";
            case 4:
                return "pathable";
            case 5:
                return "inky";
            case 6:
                return "blinky";
            case 7:
                return "pinky";
            case 8:
                return "clyde";
            case 9:
                return "pac";
        }

        return "null";
    }

    @Override
    public String toString() {
        return (getTypeName() + " at (" + getNodeX() + "," + getNodeY() + ")");
    }
}
