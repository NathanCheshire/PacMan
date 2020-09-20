import java.awt.*;

public class Node extends javafx.scene.shape.Rectangle {
    //constants
    public static final int INFINITY = Integer.MAX_VALUE;

    //node colors and our color
    public static final Color wallC = new Color(0,0,0);
    public static final Color path = new Color(230,153,0);
    private static Color color = wallC;

    //node types and our type
    public static final int WALL = 0;
    public static final int HAS_CHECKED = 1;
    public static final int TO_CHECK = 2;
    public static final int PATH = 3;

    public static final int PATHABLE = 20;

    public static final int INKY = 4;
    public static final int BLINKY = 5;
    public static final int PINKY = 6;
    public static final int CLYDE = 7;

    public static final int PAC = 8;

    private static int type;

    //used by both
    private static Node parent;

    //used by dijkastras
    private static double distance;

    //used by bfs
    private static boolean visited;

    //used by A*
    private static double hCost;
    private static double gCost;

    Node (int x, int y) {
        super(x* 10,y * 10,10,10);
        this.setType(Node.PATHABLE); //pathable by default
        this.setFill(javafx.scene.paint.Color.rgb(color.getRed(),color.getGreen(),color.getBlue()));
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

    public Color getColor() {return this.color; }
    public void setColor(Color color) {
        this.color = color;
        this.setFill(javafx.scene.paint.Color.rgb(color.getRed(),color.getGreen(),color.getBlue()));
    }

    public int getType() { return this.type; }
    public void setType(int type) {
        this.type = type;

    }

    public boolean isVisited() { return this.visited; }
    public void setVisited(boolean visited) { this.visited = visited; }
}
