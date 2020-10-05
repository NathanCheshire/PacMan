import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Line;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.util.Duration;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

//future features I plan to add/fix-----------------------------------------------
//todo fix ghost rendering when moving through path
//todo fix disabling/reanabling ghosts when using advance feature
//todo fix drawing very next path square when re-enabling paths when using advance feature
//todo switch algorithm mid game ability
//todo make all ghosts purple in hard mode
//todo don't refresh path if pac didn't move, just advance on path already there and calculated
//todo fix pathfinding glitch where the ghost doesn't follow the path, stems from a tie between nodes
//todo get to point where you disable no components
//--------------------------------------------------------------------------------

public class Controller {
    //game animation timer
    private AnimationTimer tim;

    //hardmode variables (once to five, pac dies)
    private long nsecondsInkySeen = 0;
    private long nsecondsBlinkySeen = 0;
    private long nsecondsPinkySeen = 0;
    private long nsecondsClydeSeen = 0;

    //ghosts start out updating every half second
    private long gameTimeout = 500000000;

    //var used to decrease gameTimeout for hardmode
    private long speedUpCounter = 0;

    //checkbox booleans
    public static boolean hardModeEnable;
    public static boolean drawPathsEnable;

    //used for dragging
    private double xOffset = 0;
    private double yOffset = 0;

    //size of our grid 40x40 (0-39)
    private int size = 40;

    //our grid
    public static Node[][] grid;

    //player and ghosts
    private static Pac pac;
    private Ghost inky;
    private Ghost blinky;
    private Ghost pinky;
    private Ghost clyde;

    //draw walls mode
    private boolean drawWallsMode = true;

    //game running var
    private boolean gameRunning = false;

    //the pane we can add children to such as nodes, ghosts, and pacman
    public static Pane gameDrawRoot;

    //maze generation linked lists
    private Stack<Node> mazeStack = new Stack<>();
    //mouse vars
    private double xGame;
    private double yGame;

    //getters for mouse vars
    public double getxGame() {
        return this.xGame;
    }
    public double getyGame() {
        return this.yGame;
    }

    ObservableList algorithmsList = FXCollections.observableArrayList();

    @FXML
    private HBox dragLabel;
    @FXML
    public Button createMazeButton;
    @FXML
    public Button advanceButton;
    @FXML
    public AnchorPane gameAnchorPane;
    @FXML
    public ChoiceBox<String> inkyChoice;
    @FXML
    public ChoiceBox<String> blinkyChoice;
    @FXML
    public ChoiceBox<String> pinkyChoice;
    @FXML
    public ChoiceBox<String> clydeChoice;
    @FXML
    public CheckBox inkyEnable;
    @FXML
    public CheckBox blinkyEnable;
    @FXML
    public CheckBox pinkyEnable;
    @FXML
    public CheckBox clydeEnable;
    @FXML
    private Button drawWallsButton;
    @FXML
    private Button startButton;
    @FXML
    private Button resetButton;
    @FXML
    private CheckBox showPathsCheck;
    @FXML
    private CheckBox hardModeCheck;

    public boolean onlyOneGhost() {
        boolean inkyEn = inkyEnable.isSelected();
        boolean blinkyEn = blinkyEnable.isSelected();
        boolean pinkyEn = pinkyEnable.isSelected();
        boolean clydeEn = clydeEnable.isSelected();

        if (inkyEn && !blinkyEn && !pinkyEn && !clydeEn)
            return true;
        if (!inkyEn && blinkyEn && !pinkyEn && !clydeEn)
            return true;
        if (!inkyEn && !blinkyEn && pinkyEn && !clydeEn)
            return true;
        return !inkyEn && !blinkyEn && !pinkyEn && clydeEn;
    }

    @FXML
    public void initialize() {
        //window initialize outside of game
        dragLabel.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        dragLabel.setOnMouseDragged(event -> {
            Main.primaryStage.setX(event.getScreenX() - xOffset);
            Main.primaryStage.setY(event.getScreenY() - yOffset);
            Main.primaryStage.setOpacity(0.8f);
        });

        dragLabel.setOnDragDone((event -> Main.primaryStage.setOpacity(1.0f)));
        dragLabel.setOnMouseReleased((event -> Main.primaryStage.setOpacity(1.0f)));

        //init ghost algorithms
        ArrayList<String> algorithms = new ArrayList<>();
        algorithms.add("BFS");
        algorithms.add("Dijkstras");
        algorithms.add("A*");
        algorithmsList.addAll(algorithms);
        inkyChoice.getItems().addAll(algorithmsList);
        inkyChoice.getSelectionModel().select(2);
        blinkyChoice.getItems().addAll(algorithmsList);
        blinkyChoice.getSelectionModel().select(2);
        pinkyChoice.getItems().addAll(algorithmsList);
        pinkyChoice.getSelectionModel().select(2);
        clydeChoice.getItems().addAll(algorithmsList);
        clydeChoice.getSelectionModel().select(2);

        //at least one ghost must be enabled
        inkyEnable.setSelected(true);

        showPathsCheck.setSelected(true);

        //used to draw walls
        startMouseUpdates();

        //init grid
        grid = new Node[40][40];

        for (int i = 0 ; i < 40 ; i++)
            for (int j = 0 ; j < 40 ; j++)
                grid[i][j] = new Node(i,j);

        //draw wall to grid
        Main.primaryStage.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            try {
                int xNode = (int) Math.round(xGame / 10.0);
                int yNode = (int) Math.round(yGame / 10.0);

                if (drawWallsMode) {
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode].getNodeType() == Node.PATHABLE) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode].setNodeType(Node.WALL);
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);
                        }
                    }
                }

                else if (!drawWallsMode){
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode].getNodeType() == Node.WALL) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode] = new Node(xNode, yNode);
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);
                        }
                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        });

        Main.primaryStage.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            try {
                int xNode = (int) Math.round(xGame / 10.0);
                int yNode = (int) Math.round(yGame / 10.0);

                if (drawWallsMode) {
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode].getNodeType() == Node.PATHABLE) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode].setNodeType(Node.WALL);
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);

                        }
                    }
                }

                else if (!drawWallsMode){
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode].getNodeType() == Node.WALL) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode] = new Node(xNode, yNode);
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);
                        }
                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        });

        //main game drawing root
        gameDrawRoot = new Pane();

        //add dashed lines to our gameDrawRoot
        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(i,0,i,400);
            lin.setStroke(Node.lineColor);
            gameAnchorPane.getChildren().add(lin);
        }

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(0,i,400,i);
            lin.setStroke(Node.lineColor);
            gameAnchorPane.getChildren().add(lin);
        }

        gameAnchorPane.getChildren().add(gameDrawRoot);

        drawWallsButton.setText("Walls: Draw");
    }

    //the following methods handle when a ghost is selected/deselected

    @FXML
    public void inkyEnableHandle(ActionEvent e) {
        if (inky != null) {
            grid[inky.getExactX()][inky.getExactY()] = new Node(inky.getExactX(), inky.getExactY());
            grid[inky.getExactX()][inky.getExactY()].setFill(Ghost.pathableColor);
            inky = null;
        }
    }

    @FXML
    public void blinkyEnableHandle(ActionEvent e) {
        if (blinky != null) {
            gameDrawRoot.getChildren().remove(grid[blinky.getExactX()][blinky.getExactY()]);
            grid[blinky.getExactX()][blinky.getExactY()] = new Node(blinky.getExactX(), blinky.getExactY());
            grid[blinky.getExactX()][blinky.getExactY()].setFill(Ghost.pathableColor);
            gameDrawRoot.getChildren().add(grid[blinky.getExactX()][blinky.getExactY()]);
            blinky = null;
        }
    }

    @FXML
    public void pinkyEnableHandle(ActionEvent e) {
        if (pinky != null) {
            gameDrawRoot.getChildren().remove( grid[pinky.getExactX()][pinky.getExactY()]);
            grid[pinky.getExactX()][pinky.getExactY()] = new Node(pinky.getExactX(), pinky.getExactY());
            grid[pinky.getExactX()][pinky.getExactY()].setFill(Ghost.pathableColor);
            gameDrawRoot.getChildren().add( grid[pinky.getExactX()][pinky.getExactY()]);
            pinky = null;
        }
    }

    @FXML
    public void clydeEnableHandle(ActionEvent e) {
        if (clyde != null) {
            gameDrawRoot.getChildren().remove(grid[clyde.getExactX()][clyde.getExactY()]);
            grid[clyde.getExactX()][clyde.getExactY()] = new Node(clyde.getExactX(), clyde.getExactY());
            grid[clyde.getExactX()][clyde.getExactY()].setFill(Ghost.pathableColor);
            gameDrawRoot.getChildren().add(grid[clyde.getExactX()][clyde.getExactY()]);
            clyde = null;
        }
    }

    //called when start is pressed
    public void startGameLoop() {
        //get options
        hardModeEnable = hardModeCheck.isSelected();
        drawPathsEnable = showPathsCheck.isSelected();

        Random rn = new Random();

        //init selected ghosts
        if (!inkyEnable.isSelected() && inky != null) {
            grid[inky.getExactX()][inky.getExactY()] = new Node(inky.getExactX(), inky.getExactY());
            inky = null;
        }

        if (!blinkyEnable.isSelected() && blinky != null) {
            grid[blinky.getExactX()][blinky.getExactY()] = new Node(blinky.getExactX(), blinky.getExactY());
            blinky = null;
        }

        if (!pinkyEnable.isSelected() && pinky != null) {
            grid[pinky.getExactX()][pinky.getExactY()] = new Node(pinky.getExactX(), pinky.getExactY());
            pinky = null;
        }

        if (!clydeEnable.isSelected() && clyde != null) {
            grid[clyde.getExactX()][clyde.getExactY()] = new Node(clyde.getExactX(), clyde.getExactY());
            clyde = null;
        }

        int pacX = 0;
        int pacY = 0;

        //put path on board
        if (pac == null) {
            pac = new Pac(0, 0);

            pacX = rn.nextInt(40);
            pacY = rn.nextInt(40);

            while (grid[pacX][pacY].getNodeType() != Node.PATHABLE) {
                pacX = rn.nextInt(40);
                pacY = rn.nextInt(40);
            }

            pac.setTranslateX(pacX * 10);
            pac.setTranslateY(pacY * 10);

            grid[pacX][pacY] = pac;
            pac.setExactX(pacX);
            pac.setExactY(pacY);
        }

        //figure out where to put ghosts
        if (inkyEnable.isSelected() && inky == null) {
            inky = new Ghost(0, 0,Ghost.INKY);

            int inkyX = rn.nextInt(40);
            int inkyY = rn.nextInt(40);


            while (grid[inkyX][inkyY].getNodeType() != Node.PATHABLE || getDistance(inkyX, inkyY, pacX, pacY) < 10) {
                inkyX = rn.nextInt(40);
                inkyY = rn.nextInt(40);
            }

            inky.setTranslateX(inkyX * 10);
            inky.setTranslateY(inkyY * 10);

            grid[inkyX][inkyY] = inky;

            inky.setExactX(inkyX);
            inky.setExactY(inkyY);

            String choice = inkyChoice.getValue();

            if  (choice.equalsIgnoreCase("BFS")) {
                inky.setPathFinder(new bfsPathFinding(grid,pac,inky));
            }

            else if (choice.equalsIgnoreCase("Dijkstras")) {
                inky.setPathFinder(new DijkstrasPathFinding(grid,pac,inky));
            }

            else {
                inky.setPathFinder(new AStarPathFinding(grid,pac,inky));
            }
        }

        if (blinkyEnable.isSelected() && blinky == null) {
            blinky = new Ghost(0, 0,Ghost.BLINKY);

            int blinkyX = rn.nextInt(40);
            int blinkyY = rn.nextInt(40);

            while (grid[blinkyX][blinkyY].getNodeType() != Node.PATHABLE || getDistance(blinkyX, blinkyY, pacX, pacY) < 10) {
                blinkyX = rn.nextInt(40);
                blinkyY = rn.nextInt(40);
            }

            blinky.setTranslateX(blinkyX * 10);
            blinky.setTranslateY(blinkyY * 10);

            grid[blinkyX][blinkyY] = blinky;

            blinky.setExactX(blinkyX);
            blinky.setExactY(blinkyY);

            String choice = blinkyChoice.getValue();

            if  (choice.equalsIgnoreCase("BFS")) {
                blinky.setPathFinder(new bfsPathFinding(grid,pac,blinky));
            }

            else if (choice.equalsIgnoreCase("Dijkstras")) {
                blinky.setPathFinder(new DijkstrasPathFinding(grid,pac,blinky));
            }

            else {
                blinky.setPathFinder(new AStarPathFinding(grid,pac,blinky));
            }
        }

        if (pinkyEnable.isSelected() && pinky == null) {
            pinky = new Ghost(0, 0,Ghost.PINKY);

            int pinkyX = rn.nextInt(40);
            int pinkyY = rn.nextInt(40);

            while (grid[pinkyX][pinkyY].getNodeType() != Node.PATHABLE || getDistance(pinkyX, pinkyY, pacX, pacY) < 10) {
                pinkyX = rn.nextInt(40);
                pinkyY = rn.nextInt(40);
            }

            pinky.setTranslateX(pinkyX * 10);
            pinky.setTranslateY(pinkyY * 10);

            grid[pinkyX][pinkyY] = pinky;

            pinky.setExactX(pinkyX);
            pinky.setExactY(pinkyY);

            String choice = pinkyChoice.getValue();

            if  (choice.equalsIgnoreCase("BFS")) {
                pinky.setPathFinder(new bfsPathFinding(grid,pac,pinky));
            }

            else if (choice.equalsIgnoreCase("Dijkstras")) {
                pinky.setPathFinder(new DijkstrasPathFinding(grid,pac,pinky));
            }

            else {
                pinky.setPathFinder(new AStarPathFinding(grid,pac,pinky));
            }
        }

        if (clydeEnable.isSelected() && clyde == null) {
            clyde = new Ghost(0, 0,Ghost.CLYDE);

            int clydeX = rn.nextInt(40);
            int clydeY = rn.nextInt(40);

            while (grid[clydeX][clydeY].getNodeType() != Node.PATHABLE || getDistance(clydeX, clydeY, pacX, pacY) < 10) {
                clydeX = rn.nextInt(40);
                clydeY = rn.nextInt(40);
            }

            clyde.setTranslateX(clydeX * 10);
            clyde.setTranslateY(clydeY * 10);

            grid[clydeX][clydeY] = clyde;

            clyde.setExactX(clydeX);
            clyde.setExactY(clydeY);

            String choice = clydeChoice.getValue();

            if  (choice.equalsIgnoreCase("BFS")) {
                clyde.setPathFinder(new bfsPathFinding(grid,pac,clyde));
            }

            else if (choice.equalsIgnoreCase("Dijkstras")) {
                clyde.setPathFinder(new DijkstrasPathFinding(grid,pac,clyde));
            }

            else {
                clyde.setPathFinder(new AStarPathFinding(grid,pac,clyde));
            }
        }

        //init movement for pac
        Main.primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, pacMovement);

        //start game timer or refresh gameDrawRoot
        if (tim == null) {
            tim = new AnimationTimer() {
                private long lastUp = 0;

                @Override
                public void handle(long now) {
                    if (now - lastUp >= gameTimeout) {
                        update();
                        lastUp = now;
                    }
                }
            };

            tim.start();
        }

        else {
            tim.start();
        }
    }

    //can a ghost see pac?
    private boolean straightSight(int startX, int startY, int goalX, int goalY) {
        if (startX == goalX) {
            int miny = Math.min(startY, goalY) + 1;
            int maxy = Math.max(startY, goalY);

            while (miny != maxy) {
                if (grid[startX][miny].getNodeType() != Node.PATHABLE)
                    return false;
                miny++;
            }
            return true;
        }

        else if (startY == goalY) {
            int minx = Math.min(startX, goalX) + 1;
            int maxx = Math.max(startX, goalX);

            while (minx != maxx) {
                if (grid[minx][startY].getNodeType() != Node.PATHABLE)
                    return false;

                minx++;
            }
            return true;
        }

        return false;
    }

    private void update() {
        repaintGame();

        if (pac == null)
            return;

        //if it's hard mode, speed up ghosts or kill pac depending in input
        if (hardModeEnable) {
            if (inky != null) {
                if (nsecondsInkySeen / 5.0  == 500000000)
                    endGame("inky", true);

                if (straightSight(inky.getExactX(), inky.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsInkySeen += gameTimeout;

                else
                    nsecondsInkySeen = 0;
            }

            if (blinky != null) {
                if (nsecondsBlinkySeen / 5.0  == 500000000)
                    endGame("blinky",true);

                if (straightSight(blinky.getExactX(), blinky.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsBlinkySeen += gameTimeout;

                else
                    nsecondsBlinkySeen = 0;
            }

            if (pinky != null) {
                if (nsecondsPinkySeen / 5.0  == 500000000)
                    endGame("pinky", true);

                if (straightSight(pinky.getExactX(), pinky.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsPinkySeen += gameTimeout;

                else
                    nsecondsPinkySeen = 0;
            }

            if (clyde != null) {
                if (nsecondsClydeSeen / 5.0  == 500000000)
                    endGame("clyde", true);

                if (straightSight(clyde.getExactX(), clyde.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsClydeSeen += gameTimeout;

                else
                    nsecondsClydeSeen = 0;
            }

            speedUpCounter += (gameTimeout / 1000000.0);

            //increase game speed if it has been 5 seconds in hard mode without killing pac
            if (speedUpCounter >= 5000) {
                if (gameTimeout >= 100000000)
                    gameTimeout -= 50000000;

                speedUpCounter = 0;
            }
        }

        //this is where the pathfinders are called
        if (inkyEnable.isSelected())
            inky.step(grid, true, onlyOneGhost());

        if (blinkyEnable.isSelected())
            blinky.step(grid, true, onlyOneGhost());

        if (pinkyEnable.isSelected())
            pinky.step(grid, true, onlyOneGhost());

        if (clydeEnable.isSelected())
            clyde.step(grid, true, onlyOneGhost());

        String dead = isDead();
        if (!dead.equals("null"))
            endGame(dead, false);

        hardModeEnable = hardModeCheck.isSelected();
        drawPathsEnable = showPathsCheck.isSelected();
    }

    //updates gameDrawRoot walls, pathable nodes, pac, and ghsots
    private void repaintGame() {
        gameDrawRoot.getChildren().clear();

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (grid[i][j].getNodeType() == Node.PATHABLE)
                    grid[i][j].setFill(Ghost.pathableColor);
            }
        }

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (grid[i][j].getNodeType() == Node.WALL)
                    gameDrawRoot.getChildren().add(grid[i][j]);
            }
        }

        if (pac != null) {
            gameDrawRoot.getChildren().remove(grid[pac.getNodeX()][pac.getNodeY()]);
            grid[pac.getNodeX()][pac.getNodeY()].setFill(new ImagePattern(new Image("Pac.png")));
            gameDrawRoot.getChildren().add(grid[pac.getNodeX()][pac.getNodeY()]);
        }

        if (inky != null) {
            gameDrawRoot.getChildren().remove(grid[inky.getNodeX()][inky.getNodeY()]);
            grid[inky.getNodeX()][inky.getNodeY()].setFill(new ImagePattern(new Image("Inky.png")));
            gameDrawRoot.getChildren().add(grid[inky.getNodeX()][inky.getNodeY()]);
        }

        if (blinky != null) {
            gameDrawRoot.getChildren().remove(grid[blinky.getNodeX()][blinky.getNodeY()]);
            grid[blinky.getNodeX()][blinky.getNodeY()].setFill(new ImagePattern(new Image("Blinky.png")));
            gameDrawRoot.getChildren().add(grid[blinky.getNodeX()][blinky.getNodeY()]);
        }

        if (pinky != null) {
            gameDrawRoot.getChildren().remove(grid[pinky.getNodeX()][pinky.getNodeY()]);
            grid[pinky.getNodeX()][pinky.getNodeY()].setFill(new ImagePattern(new Image("Pinky.png")));
            gameDrawRoot.getChildren().add(grid[pinky.getNodeX()][pinky.getNodeY()]);
        }

        if (clyde != null) {
            gameDrawRoot.getChildren().remove(grid[clyde.getNodeX()][clyde.getNodeY()]);
            grid[clyde.getNodeX()][clyde.getNodeY()].setFill(new ImagePattern(new Image("Clyde.png")));
            gameDrawRoot.getChildren().add(grid[clyde.getNodeX()][clyde.getNodeY()]);
        }
    }

    //use this to update a path from pathfinding classes
    public static void showPath(int x, int y) {
        if (x == pac.getNodeX() && y == pac.getNodeY()
                || grid[x][y].getNodeType() != Node.PATHABLE)
            return;

        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setFill(Ghost.pathColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

    public static void showCheckedNode(int x, int y) {
        if (x == pac.getNodeX() && y == pac.getNodeY())
            return;

        if (grid[x][y].getNodeType() != Node.PATHABLE)
            return;

        if (!drawPathsEnable)
            return;

        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setFill(Ghost.checkedNode);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

    //removes a path color from draw root
    public static void hidePath(int x, int y) {
        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setFill(Ghost.pathableColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

    //get mouse location used for drawing walls
    private void startMouseUpdates() {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                try {
                    while (true) {
                        Point p = MouseInfo.getPointerInfo().getLocation();

                        xGame = p.getX() - 176.5 - Main.primaryStage.getX();
                        yGame = (p.getY() - 87 - Main.primaryStage.getY());

                        Thread.sleep(1);
                    }
                } catch (Exception ignored) {}
                return null;
            }
        };
        new Thread(task).start();
    }



    //toggle button for drawing walls
    @FXML
    private void drawWalls(ActionEvent e) {
        if (!drawWallsMode) {
            drawWallsMode = !drawWallsMode;
            drawWallsButton.setText("Walls: Draw");
        }

        else {
            drawWallsMode = !drawWallsMode;
            drawWallsButton.setText("Walls: Remove");
        }
    }

    //start game for the first time or resume it
    @FXML
    private void startGame(ActionEvent e) {
        int total = 40 * 40;
        int wallNum = 0;

        //make sure there aren't too many walls
        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (grid[i][j].getNodeType() == Node.WALL)
                    wallNum++;
            }
        }

        if (((double) wallNum / (double) total) >= 0.69) {
            showPopupMessage("Too many walls on the grid! Please remove some so that you don't overwork the poor ghosts!", Main.primaryStage);
            return;
        }

        //stop game
        if (gameRunning) {
            showPopupMessage("Stoping Game",Main.primaryStage);
            startButton.setText("Start Game");
            gameRunning = !gameRunning;
            drawWallsMode = true;
            drawWallsButton.setText("Walls: Draw");

            tim.stop();
            Main.primaryStage.removeEventFilter(KeyEvent.KEY_PRESSED, pacMovement);

            inkyEnable.setDisable(false);
            blinkyEnable.setDisable(false);
            pinkyEnable.setDisable(false);
            clydeEnable.setDisable(false);
        }

        //start/restart a paused game
        else {
            if (!inkyEnable.isSelected() && !blinkyEnable.isSelected() && !pinkyEnable.isSelected() && !clydeEnable.isSelected()) {
                showPopupMessage("Must have at least one ghost enabled",Main.primaryStage);
                return;
            }

            showPopupMessage("Starting/Resuming Game",Main.primaryStage);
            startButton.setText("Stop Game");
            gameRunning = !gameRunning;
            drawWallsMode = false;
            drawWallsButton.setText("Walls: Remove");

            startGameLoop();

            inkyEnable.setDisable(true);
            blinkyEnable.setDisable(true);
            pinkyEnable.setDisable(true);
            clydeEnable.setDisable(true);

            inkyChoice.setDisable(true);
            blinkyChoice.setDisable(true);
            pinkyChoice.setDisable(true);
            clydeChoice.setDisable(true);
        }
    }

    //freeze game and don't let a resume happen
    private void endGame(String name, boolean sight) {
        if (hardModeEnable && sight)
            showPopupMessage("You were killed by " + name + " since he saw you for too long, damn him!" + "\nPress reset to play again",Main.primaryStage);
        else
            showPopupMessage("You were killed by " + name + ", damn him!" + "\nPress reset to play again",Main.primaryStage);

        inkyEnable.setDisable(true);
        blinkyEnable.setDisable(true);
        pinkyEnable.setDisable(true);
        clydeEnable.setDisable(true);

        inkyChoice.setDisable(true);
        blinkyChoice.setDisable(true);
        pinkyChoice.setDisable(true);
        clydeChoice.setDisable(true);

        startButton.setDisable(true);

        Main.primaryStage.removeEventFilter(KeyEvent.KEY_PRESSED, pacMovement);

        tim.stop();

        pac = null;
    }

    //reset to defaults
    @FXML
    private void resetGame(ActionEvent e) {
        showPopupMessage("Reset Game",Main.primaryStage);
        startButton.setDisable(false);
        startButton.setText("Start Game");
        drawWallsMode = true;
        drawWallsButton.setText("Walls: Draw");

        if (gameRunning) {
            gameRunning = false;

            tim.stop();
            tim = null;
            Main.primaryStage.removeEventFilter(KeyEvent.KEY_PRESSED, pacMovement);
        }

        inkyEnable.setDisable(false);
        blinkyEnable.setDisable(false);
        pinkyEnable.setDisable(false);
        clydeEnable.setDisable(false);

        inkyChoice.setDisable(false);
        blinkyChoice.setDisable(false);
        pinkyChoice.setDisable(false);
        clydeChoice.setDisable(false);

        gameDrawRoot.getChildren().clear();
        pac = null;
        inky = null;
        blinky = null;
        pinky = null;
        clyde = null;

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(i,0,i,400);
            lin.setStroke(Node.lineColor);
            gameDrawRoot.getChildren().add(lin);
        }

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(0,i,400,i);
            lin.setStroke(Node.lineColor);
            gameDrawRoot.getChildren().add(lin);
        }

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                grid[i][j] = new Node(i,j);
                grid[i][j].setNodeType(Node.PATHABLE);
            }
        }

        inkyChoice.setValue("A*");
        blinkyChoice.setValue("A*");
        pinkyChoice.setValue("A*");
        clydeChoice.setValue("A*");

        inkyEnable.setSelected(true);
        blinkyEnable.setSelected(false);
        pinkyEnable.setSelected(false);
        clydeEnable.setSelected(false);

        hardModeCheck.setSelected(false);
        showPathsCheck.setSelected(true);

        gameTimeout = 500000000;
        speedUpCounter = 0;
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        System.exit(0);
    }

    //handler for when show paths checkbox is toggled
    @FXML
    private void showPathsHandler(ActionEvent e) {
        if (showPathsCheck.isSelected()) {
            if (inky != null)
                inky.step(grid,false, onlyOneGhost());
            if (blinky != null)
                blinky.step(grid,false, onlyOneGhost());
            if (pinky != null)
                pinky.step(grid,false, onlyOneGhost());
            if (clyde != null)
                clyde.step(grid,false, onlyOneGhost());
        }

        else
            repaintGame();
    }

    //checks if a ghost is next to pac, could optimize this method using the nextTo method
    private String isDead() {
        if (inky != null && inkyEnable.isSelected()) {
            //left
            if (pac.getExactX() - 1 >= 0 && (pac.getExactX() - 1 == inky.getExactX() && pac.getExactY() == inky.getExactY()))
                return "inky";
            //right
            if (pac.getExactX() + 1 < 40 && (pac.getExactX() + 1 == inky.getExactX() && pac.getExactY() == inky.getExactY()))
                return "inky";
            //up
            if (pac.getExactY() + 1 < 40 && (pac.getExactX() == inky.getExactX() && pac.getExactY() + 1 == inky.getExactY()))
                return "inky";
            //down
            if (pac.getExactY() - 1 >= 0 && (pac.getExactX() == inky.getExactX() && pac.getExactY() - 1 == inky.getExactY()))
                return "inky";
        }

        if (blinky != null && blinkyEnable.isSelected()) {
            //left
            if (pac.getExactX() - 1 >= 0 && (pac.getExactX() - 1 == blinky.getExactX() && pac.getExactY() == blinky.getExactY()))
                return "blinky";
            //right
            if (pac.getExactX() + 1 < 40 && (pac.getExactX() + 1 == blinky.getExactX() && pac.getExactY() == blinky.getExactY()))
                return "blinky";
            //up
            if (pac.getExactY() + 1 < 40 && (pac.getExactX() == blinky.getExactX() && pac.getExactY() + 1 == blinky.getExactY()))
                return "blinky";
            //down
            if (pac.getExactY() - 1 >= 0 && (pac.getExactX() == blinky.getExactX() && pac.getExactY() - 1 == blinky.getExactY()))
                return "blinky";
        }

        if (pinky != null && pinkyEnable.isSelected()) {
            //left
            if (pac.getExactX() - 1 >= 0 && (pac.getExactX() - 1 == pinky.getExactX() && pac.getExactY() == pinky.getExactY()))
                return "pinky";
            //right
            if (pac.getExactX() + 1 < 40 && (pac.getExactX() + 1 == pinky.getExactX() && pac.getExactY() == pinky.getExactY()))
                return "pinky";
            //up
            if (pac.getExactY() + 1 < 40 && (pac.getExactX() == pinky.getExactX() && pac.getExactY() + 1 == pinky.getExactY()))
                return "pinky";
            //down
            if (pac.getExactY() - 1 >= 0 && (pac.getExactX() == pinky.getExactX() && pac.getExactY() - 1 == pinky.getExactY()))
                return "pinky";
        }

        if (clyde != null && clydeEnable.isSelected()) {
            //left
            if (pac.getExactX() - 1 >= 0 && (pac.getExactX() - 1 == clyde.getExactX() && pac.getExactY() == clyde.getExactY()))
                return "clyde";
            //right
            if (pac.getExactX() + 1 < 40 && (pac.getExactX() + 1 == clyde.getExactX() && pac.getExactY() == clyde.getExactY()))
                return "clyde";
            //up
            if (pac.getExactY() + 1 < 40 && (pac.getExactX() == clyde.getExactX() && pac.getExactY() + 1 == clyde.getExactY()))
                return "clyde";
            //down
            if (pac.getExactY() - 1 >= 0 && (pac.getExactX() == clyde.getExactX() && pac.getExactY() - 1 == clyde.getExactY()))
                return "clyde";
        }

        return "null";
    }

    //movement for pacman
    private EventHandler<KeyEvent> pacMovement = new EventHandler<>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (gameRunning) {
                switch (keyEvent.getCode()) {
                    case A:
                        if (pac.getExactX() - 1 >= 0 && grid[pac.getExactX() - 1][pac.getExactY()].getNodeType() == Node.PATHABLE) {
                            grid[pac.getExactX()][pac.getExactY()] = new Node(pac.getExactX(), pac.getExactY());
                            pac.setTranslateX(pac.getTranslateX() - 10.0);
                            pac.setExactX(pac.getExactX() - 1);
                        }
                        break;
                    case S:
                        if (pac.getExactY() + 1 < 40 && grid[pac.getExactX()][pac.getExactY() + 1].getNodeType() == Node.PATHABLE) {
                            grid[pac.getExactX()][pac.getExactY()] = new Node(pac.getExactX(), pac.getExactY());
                            pac.setTranslateY(pac.getTranslateY() + 10.0);
                            pac.setExactY(pac.getExactY() + 1);
                            grid[pac.getExactX()][pac.getExactY()] = pac;
                        }
                        break;
                    case D:
                        if (pac.getExactX() + 1 < 40 && grid[pac.getExactX() + 1][pac.getExactY()].getNodeType() == Node.PATHABLE) {
                            grid[pac.getExactX()][pac.getExactY()] = new Node(pac.getExactX(), pac.getExactY());
                            pac.setTranslateX(pac.getTranslateX() + 10.0);
                            pac.setExactX(pac.getExactX() + 1);
                            grid[pac.getExactX()][pac.getExactY()] = pac;
                        }
                        break;
                    case W:
                        if (pac.getExactY() - 1 >= 0 && grid[pac.getExactX()][pac.getExactY() - 1].getNodeType() == Node.PATHABLE) {
                            grid[pac.getExactX()][pac.getExactY()] = new Node(pac.getExactX(), pac.getExactY());
                            pac.setTranslateY(pac.getTranslateY() - 10.0);
                            pac.setExactY(pac.getExactY() - 1);
                            grid[pac.getExactX()][pac.getExactY()] = pac;
                        }
                        break;
                }
            }
        }
    };


    //used to increment game step by step
    @FXML
    public void step(ActionEvent event) {
        if (!inkyEnable.isSelected() && !blinkyEnable.isSelected() && !pinkyEnable.isSelected() && !clydeEnable.isSelected()) {
            showPopupMessage("Must have at least one ghost enabled",Main.primaryStage);
            return;
        }

        //if game has not been started, don't do anything
        if (pac == null)
            return;

        //redraw game
        repaintGame();

        //move each enabled ghost as long as the game is not running
        if (inky != null && !gameRunning)
            inky.step(grid,true, onlyOneGhost());
        if (blinky != null && !gameRunning)
            blinky.step(grid,true, onlyOneGhost());
        if (pinky != null && !gameRunning)
            pinky.step(grid,true, onlyOneGhost());
        if (clyde != null && !gameRunning)
            clyde.step(grid,true, onlyOneGhost());

        //check for death
        String dead = isDead();
        if (!dead.equals("null"))
            endGame(dead, false);
    }

    //sets a game node to a wall
    private void setWall(int x, int y) {
        if (x > 40 || y > 40 || x < 0 || y < 0)
            return;
        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setNodeType(Node.WALL);
        grid[x][y].setFill(Ghost.wallColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

    //sets a game node to pathable
    private void setPathable(int x, int y) {
        if (x > 40 || y > 40 || x < 0 || y < 0)
            return;

        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setNodeType(Node.PATHABLE);
        grid[x][y].setFill(Ghost.pathableColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

    //this function uses backtracking so I thought it was applicable for this class and thus this project
    @FXML
    public void drawMaze(ActionEvent event) {
        resetGame(event);

        char[][] maze = (new MazeGenerator()).getMaze(42);

        for (int i = 1 ; i < 41 ; i++) {
            for (int j = 1 ; j < 41 ; j++) {
                if (maze[i][j] == '0')
                    setPathable(i - 1,j - 1);
                else
                    setWall(i - 1,j - 1);
            }
        }

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (i == 0 || j == 0 || i == 39 || j == 39)
                    setPathable(i,j);
            }
        }
    }

    //popup messages, can customize look based on the style sheet selected
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
            popup.setX(stage.getX() + stage.getWidth() / 2 - popup.getWidth() / 2 + 80);
            popup.setY(stage.getY() + 250);
        });
        popup.show(stage);
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> popup.hide());
        delay.play();
    }

    //calculate euclidean distance between coordinates
    private double getDistance(int x1, int y1, int x2, int y2) {
        System.out.println("distance: " + Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2)));
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    @FXML
    private void hardModeChangeHandler() {
        if (hardModeCheck.isSelected()) {
            inkyChoice.setValue("A*");
            blinkyChoice.setValue("A*");
            pinkyChoice.setValue("A*");
            clydeChoice.setValue("A*");

            inkyEnable.setSelected(true);
            blinkyEnable.setSelected(true);
            pinkyEnable.setSelected(true);
            clydeEnable.setSelected(true);
        }
    }
}
