import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

//extend node class so that we can draw it to gameDrawRoot
public class Pac extends Node {

    //position and x,y object vars (position is static since it can be)
    private static Node position;
    private int x;
    private int y;

    //constructor to init position and position on draw pane
    Pac(int x, int y) {
        super(x * 10,y * 10);
        this.position = new Node(x,y);
        this.x = x;
        this.y = y;
        this.setFill(new ImagePattern(new Image("Pac.png")));
    }

    //basic getters and setters-------------------------------


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