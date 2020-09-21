import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;

public class Ghost extends Node {
    //ghosts postiion
    private static Node position;

    //if we should render this ghost and path find
    private boolean enabled;

    //ghost types and our type
    private int type;

    public static final Color pathColor = javafx.scene.paint.Color.rgb(204,153,0,0.8);
    public static final Color pathableColor = javafx.scene.paint.Color.rgb(0,0,0,0);

    //ghost pictures
    public static final ImagePattern inkyImage = new ImagePattern(new Image("Inky.png"));
    public static final ImagePattern blinkyImage = new ImagePattern(new Image("Blinky.png"));
    public static final ImagePattern pinkyImage = new ImagePattern(new Image("Pinky.png"));
    public static final ImagePattern clydeImage = new ImagePattern(new Image("Clyde.png"));

    private int x;
    private int y;
    
    private PathFinder pathFinder;

    Ghost(int x, int y, int type) {
        super((x) * 10,(y) * 10);
        
        this.type = type;
        super.setNodeType(type);

        this.position = new Node(x,y);
        
        this.x = x;
        this.y = y;

        //init image
        switch(type) {
            case Ghost.INKY:
                this.setFill(inkyImage);
                break;
            case Ghost.BLINKY:
                this.setFill(blinkyImage);
                break;
            case Ghost.PINKY:
                this.setFill(pinkyImage);
                break;
            case Ghost.CLYDE:
                this.setFill(clydeImage);
                break;
        }
    }

    public int getNodeType() {
        return this.type;
    }

    public void setPathFinder(PathFinder pf) {
        this.pathFinder = pf;
    }

    public void setPosition(Node pos) {
        this.position = pos;
    }
    public Node getPosition() {
        return this.position;
    }

    public void step(Node[][] graph) {
        pathFinder.refreshPath(graph);
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean getEnabled() { return this.enabled; }

    public void setExactX(int x) {
        this.x = x;
    }

    public void setExactY(int y) {
        this.y = y;
    }

    public int getExactX() {
        return this.x;
    }

    public String getNameType() {
        switch(type) {
            case Ghost.INKY:
                return "inky";
            case Ghost.BLINKY:
                return "blinky";
            case Ghost.PINKY:
                return "pinky";
            case Ghost.CLYDE:
                return "clyde";
        }

        return  null;
    }

    public int getExactY() {
        return this.y;
    }

    @Override
    public String toString() {
        return "Ghost type: " + type + super.toString();
    }
}
