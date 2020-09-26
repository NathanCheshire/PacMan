import javafx.animation.AnimationTimer;
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

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

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

    @FXML
    private HBox dragLabel;

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

        inkyEnable.setSelected(true);

        startMouseUpdates();

        grid = new Node[40][40];

        for (int i = 0 ; i < 40 ; i++)
            for (int j = 0 ; j < 40 ; j++)
                grid[i][j] = new Node(i,j);

        Main.primaryStage.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            try {
                int xNode = (int) Math.round(xGame / 10.0);
                int yNode = (int) Math.round(yGame / 10.0);

                if (drawWallsMode && !gameRunning) {
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode].getNodeType() == Node.PATHABLE) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode].setNodeType(Node.WALL);
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);
                        }
                    }
                }

                else if (!gameRunning && !drawWallsMode){
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

                if (drawWallsMode && !gameRunning) {
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode].getNodeType() == Node.PATHABLE) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode].setNodeType(Node.WALL);
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);

                        }
                    }
                }

                else if (!gameRunning && !drawWallsMode){
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

        gameDrawRoot = new Pane();

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
    }

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

    public void startGameLoop() {
        hardModeEnable = hardModeCheck.isSelected();
        drawPathsEnable = showPathsCheck.isSelected();

        Random rn = new Random();

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

        if (pac == null) {
            pac = new Pac(0, 0);

            int pacX = rn.nextInt(40);
            int pacY = rn.nextInt(40);

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

        if (inkyEnable.isSelected() && inky == null) {
            inky = new Ghost(0, 0,Ghost.INKY);

            int inkyX = rn.nextInt(40);
            int inkyY = rn.nextInt(40);


            while (grid[inkyX][inkyY].getNodeType() != Node.PATHABLE) {
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

            else if (choice.equalsIgnoreCase("Dijkastras")) {
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

            while (grid[blinkyX][blinkyY].getNodeType() != Node.PATHABLE) {
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

            else if (choice.equalsIgnoreCase("Dijkastras")) {
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

            while (grid[pinkyX][pinkyY].getNodeType() != Node.PATHABLE) {
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

            else if (choice.equalsIgnoreCase("Dijkastras")) {
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

            while (grid[clydeX][clydeY].getNodeType() != Node.PATHABLE) {
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

            else if (choice.equalsIgnoreCase("Dijkastras")) {
                clyde.setPathFinder(new DijkstrasPathFinding(grid,pac,clyde));
            }

            else {
                clyde.setPathFinder(new AStarPathFinding(grid,pac,clyde));
            }
        }

        Main.primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, pacMovement);

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

        if (hardModeEnable) {
            if (inky != null) {
                if (nsecondsInkySeen / 10.0  == 500000000)
                    endGame("inky");

                if (straightSight(inky.getExactX(), inky.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsInkySeen += gameTimeout;

                else
                    nsecondsInkySeen = 0;
            }

            if (blinky != null) {
                if (nsecondsBlinkySeen / 10.0  == 500000000)
                    endGame("blinky");

                if (straightSight(blinky.getExactX(), blinky.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsBlinkySeen += gameTimeout;

                else
                    nsecondsBlinkySeen = 0;
            }

            if (pinky != null) {
                if (nsecondsPinkySeen / 10.0  == 500000000)
                    endGame("pinky");

                if (straightSight(pinky.getExactX(), pinky.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsPinkySeen += gameTimeout;

                else
                    nsecondsPinkySeen = 0;
            }

            if (clyde != null) {
                if (nsecondsClydeSeen / 10.0  == 500000000)
                    endGame("clyde");

                if (straightSight(clyde.getExactX(), clyde.getExactY(), pac.getExactX(), pac.getExactY()))
                    nsecondsClydeSeen += gameTimeout;

                else
                    nsecondsClydeSeen = 0;
            }
            speedUpCounter += (gameTimeout / 1000000.0);

            if (speedUpCounter >= 10000) {
                if (gameTimeout >= 100000000)
                    gameTimeout -= 50000000;
                speedUpCounter = 0;
            }
        }

        //this is where the pathfinders are called
        if (inkyEnable.isSelected())
            inky.step(grid, true);

        if (blinkyEnable.isSelected())
            blinky.step(grid, true);

        if (pinkyEnable.isSelected())
            pinky.step(grid, true);

        if (clydeEnable.isSelected())
            clyde.step(grid, true);

        String dead = isDead();
        if (!dead.equals("null"))
            endGame(dead);

        hardModeEnable = hardModeCheck.isSelected();
        drawPathsEnable = showPathsCheck.isSelected();
    }

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

        gameDrawRoot.getChildren().remove(grid[pac.getNodeX()][pac.getNodeY()]);
        grid[pac.getNodeX()][pac.getNodeY()].setFill(new ImagePattern(new Image("Pac.png")));
        gameDrawRoot.getChildren().add(grid[pac.getNodeX()][pac.getNodeY()]);

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
        if (x == pac.getNodeX() && y == pac.getNodeY())
            return;

        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setFill(Ghost.pathColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

    public static void hidePath(int x, int y) {
        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setFill(Ghost.pathableColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

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

    ObservableList algorithmsList = FXCollections.observableArrayList();

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
    private RadioButton inkyEnable;
    @FXML
    private RadioButton blinkyEnable;
    @FXML
    private RadioButton pinkyEnable;
    @FXML
    private RadioButton clydeEnable;

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

    @FXML
    private void startGame(ActionEvent e) {
        int total = 40 * 40;
        int wallNum = 0;

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (grid[i][j].getNodeType() == Node.WALL)
                    wallNum++;
            }
        }

        if (((double) wallNum / (double) total) >= 0.69) {
            System.out.println("Too many walls on the grid! Please remove some so that you don't overwork the poor ghosts!");
            return;
        }

        if (gameRunning) {
            System.out.println("Stoping Game");
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

            drawWallsButton.setDisable(false);
            hardModeCheck.setDisable(false);
        }

        else {
            if (!inkyEnable.isSelected() && !blinkyEnable.isSelected() && !pinkyEnable.isSelected() && !clydeEnable.isSelected()) {
                System.out.println("Must have at least one ghost enabled");
                return;
            }

            System.out.println("Starting/Resuming Game");
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
            
            drawWallsButton.setDisable(true);
            hardModeCheck.setDisable(true);
        }
    }

    private void endGame(String name) {
        if (hardModeEnable)
            System.out.println("You were killed since " + name + " saw you for 10 seconds, damn him!" + "\nPress reset to play again");
        else
            System.out.println("You were killed by " + name + ", damn him!" + "\nPress reset to play again");
        
        inkyEnable.setDisable(true);
        blinkyEnable.setDisable(true);
        pinkyEnable.setDisable(true);
        clydeEnable.setDisable(true);

        inkyChoice.setDisable(true);
        blinkyChoice.setDisable(true);
        pinkyChoice.setDisable(true);
        clydeChoice.setDisable(true);
        
        drawWallsButton.setDisable(true);
        hardModeCheck.setDisable(true);

        startButton.setDisable(true);

        Main.primaryStage.removeEventFilter(KeyEvent.KEY_PRESSED, pacMovement);

        tim.stop();

        pac = null;
    }

    @FXML
    private void resetGame(ActionEvent e) {
        System.out.println("Reset Game");
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

        drawWallsButton.setDisable(false);
        hardModeCheck.setDisable(false);

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
        showPathsCheck.setSelected(false);

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

    @FXML
    private void showPathsHandler(ActionEvent e) {
        if (showPathsCheck.isSelected()) {
            if (inky != null)
                inky.step(grid,false);
            if (blinky != null)
                blinky.step(grid,false);
            if (pinky != null)
                pinky.step(grid,false);
            if (clyde != null)
                clyde.step(grid,false);
        }

        else
            repaintGame();
    }

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

    @FXML
    public Button createMazeButton;
    public Button advanceButton;

    @FXML
    public void step(ActionEvent event) {
        if (!inkyEnable.isSelected() && !blinkyEnable.isSelected() && !pinkyEnable.isSelected() && !clydeEnable.isSelected()) {
            System.out.println("Must have at least one ghost enabled");
            return;
        }

        if (pac == null)
            return;

        repaintGame();

        if (inky != null && !gameRunning)
            inky.step(grid,true);
        if (blinky != null && !gameRunning)
            blinky.step(grid,true);
        if (pinky != null && !gameRunning)
            pinky.step(grid,true);
        if (clyde != null && !gameRunning)
            clyde.step(grid,true);

        String dead = isDead();
        if (!dead.equals("null"))
            endGame(dead);
    }

    private void setWall(int x, int y) {
        if (x > 40 || y > 40 || x < 0 || y < 0)
            return;
        gameDrawRoot.getChildren().remove(grid[x][y]);
        grid[x][y].setNodeType(Node.WALL);
        grid[x][y].setFill(Ghost.wallColor);
        gameDrawRoot.getChildren().add(grid[x][y]);
    }

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

        char[][] maze = (new MazeGenerator()).getMaze(40);

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (maze[i][j] == '0')
                    setPathable(i,j);
                else
                    setWall(i,j);
            }
        }

        for (int i = 0 ; i < 40 ; i++) {
            for (int j = 0 ; j < 40 ; j++) {
                if (i == 0 || j == 0 || i == 39 || j == 39)
                    setPathable(i,j);
            }
        }
    }
}
