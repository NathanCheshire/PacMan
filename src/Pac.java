import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Pac extends Node {
    private static Node position;
    private int x;
    private int y;

    Pac(int x, int y) {
        super(x * 10,y * 10);
        this.position = new Node(x,y);
        this.x = x;
        this.y = y;
        this.setFill(new ImagePattern(new Image("Pac.png")));
    }

    public Node getPosition() {
        return this.position;
    }

    public int getNodeType() {
        return Node.PAC;
    }

    public void setPosition(Node position) {
        this.position = position;
    }
    public void setPosition(int x, int y) {
        this.position.setNodeX(x);
        this.position.setNodeY(y);
    }

    public void setExactX(int x) {
        this.x = x;
    }

    public void setExactY(int y) {
        this.y = y;
    }

    public int getExactX() {
        return this.x;
    }

    public int getExactY() {
        return this.y;
    }
}