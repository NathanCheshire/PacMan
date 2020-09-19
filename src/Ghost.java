import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class Ghost extends Node {

    //ghosts postiion
    private static Node position;

    //if we should render this ghost and path find
    private boolean enabled;

    //ghost types and our type
    private int type;
    public static final int INKY = 0;
    public static final int BLINKY = 1;
    public static final int PINKY = 2;
    public static final int CLYDE = 3;

    //ghost pictures
    public static final ImagePattern inkyImage = new ImagePattern(new Image("Inky.png"));
    public static final ImagePattern blinkyImage = new ImagePattern(new Image("Blinky.png"));
    public static final ImagePattern pinkyImage = new ImagePattern(new Image("Pinky.png"));
    public static final ImagePattern clydeImage = new ImagePattern(new Image("Clyde.png"));

    private int x;
    private int y;

    //local graph that includes walls because each algorithm/ghost will have different search paths
    private PathFinder pathFinder;

    Ghost(int x, int y, int type) {
        super((x) * 10,(y) * 10);
        this.type = type;
        this.position = new Node(x,y);
        this.x = x;
        this.y = y;

        //init image
        switch(type) {
            case 0:
                this.setFill(inkyImage);
                break;
            case 1:
                this.setFill(blinkyImage);
                break;
            case 2:
                this.setFill(pinkyImage);
                break;
            case 3:
                this.setFill(clydeImage);
                break;
        }
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

    public void step() {
        pathFinder.resfreshPath();
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

    public int getExactY() {
        return this.y;
    }
}
