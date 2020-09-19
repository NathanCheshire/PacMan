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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    //checkbox booleans
    public boolean hardModeEnable;
    public boolean drawPathsEnable;

    //used for dragging
    private double xOffset = 0;
    private double yOffset = 0;

    //size of our grid 40x40 (0-39)
    private static int size = 40;

    //our grid
    private static Node[][] grid;

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
    private static Pane gameDrawRoot;

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

    //get a node at x,y
    public Node getNode(int x, int y) {
        return grid[x][y];
    }

    //get neighbors of a node provided they are valid
    public java.util.List<Node> getNeighbors(Node node) {
        List<Node> ret = new ArrayList<>();

        for (int x = -1 + node.getNodeX(); x <= 1 + node.getNodeY() ; x++) {
            for (int y = -1 + node.getNodeY(); y <= 1 + node.getNodeY() ; y++) {
                if (x == 0 && y == 0)
                    continue;
                if (x > 0 && y > 0 && x < size && y < size)
                    ret.add(grid[x][y]);

            }
        }

        return ret;
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
        algorithms.add("Dijakstras");
        algorithms.add("A*");
        algorithmsList.addAll(algorithms);
        inkyChoice.getItems().addAll(algorithmsList);
        inkyChoice.getSelectionModel().select(0);
        blinkyChoice.getItems().addAll(algorithmsList);
        blinkyChoice.getSelectionModel().select(0);
        pinkyChoice.getItems().addAll(algorithmsList);
        pinkyChoice.getSelectionModel().select(0);
        clydeChoice.getItems().addAll(algorithmsList);
        clydeChoice.getSelectionModel().select(0);

        //start updating the mouse position vars, used to draw walls
        startMouseUpdates();

        Main.primaryStage.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            try {
                int xNode = (int) Math.round(xGame / 10.0);
                int yNode = (int) Math.round(yGame / 10.0);

                if (drawWallsMode && !gameRunning) {
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode] == null) {
                            Node node = new Node(xNode,yNode);
                            node.setType(Node.WALL);
                            grid[xNode][yNode] = node;
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);
                        }
                    }
                }

                else if (!gameRunning){
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode] != null && grid[xNode][yNode].getType() == Node.WALL) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode] = null;
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
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
                        if (grid[xNode][yNode] == null) {
                            Node node = new Node(xNode,yNode);
                            node.setType(Node.WALL);
                            grid[xNode][yNode] = node;
                            gameDrawRoot.getChildren().add(grid[xNode][yNode]);
                        }
                    }
                }

                else if (!gameRunning){
                    if (xNode < 40 && xNode >= 0 && yNode < 40 && yNode >= 0) {
                        if (grid[xNode][yNode] != null && grid[xNode][yNode].getType() == Node.WALL) {
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                            grid[xNode][yNode] = null;
                            gameDrawRoot.getChildren().remove(grid[xNode][yNode]);
                        }
                    }
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
        });

        //init grid
        grid = new Node[40][40];
        for (int i = 0 ; i < grid.length ; i++) {
            for (int j = 0 ; j < grid[0].length ; j++) {
                grid[i][j] = null;
            }
        }

        //initiate game pane
        gameDrawRoot = new Pane();

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(i,0,i,400);
            lin.setStroke(javafx.scene.paint.Color.rgb(0,0,0));
            gameDrawRoot.getChildren().add(lin);
        }

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(0,i,400,i);
            lin.setStroke(javafx.scene.paint.Color.rgb(0,0,0));
            gameDrawRoot.getChildren().add(lin);
        }

        gameAnchorPane.getChildren().add(gameDrawRoot);
    }


    public void startGameLoop() {
        hardModeEnable = hardModeCheck.isSelected();
        drawPathsEnable = showPathsCheck.isSelected();

        Random rn = new Random();

        if (pac == null) {
            pac = new Pac(0, 0);

            int pacX = rn.nextInt(40);
            int pacY = rn.nextInt(40);

            while (grid[pacX][pacY] != null) {
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
            inky = new Ghost(0, 0,0);

            int inkyX = rn.nextInt(40);
            int inkyY = rn.nextInt(40);

            while (grid[inkyX][inkyY] != null) {
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
            blinky = new Ghost(0, 0,1);

            int blinkyX = rn.nextInt(40);
            int blinkyY = rn.nextInt(40);

            while (grid[blinkyX][blinkyY] != null) {
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
            pinky = new Ghost(0, 0,2);

            int pinkyX = rn.nextInt(40);
            int pinkyY = rn.nextInt(40);

            while (grid[pinkyX][pinkyY] != null) {
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
            clyde = new Ghost(0, 0,3);

            int clydeX = rn.nextInt(40);
            int clydelY = rn.nextInt(40);

            clyde.setTranslateX(clydeX * 10);
            clyde.setTranslateY(clydelY * 10);

            grid[clydeX][clydelY] = clyde;

            clyde.setExactX(clydeX);
            clyde.setExactY(clydelY);

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
        System.out.println("here");
        if (startX == goalX) {
            System.out.println("Vertical LOS"); //todo determine if we can go in a straight or vertical line to pac, if we hit wall or ghost return false
            return true;
        }

        else if (startY == goalY) {
           System.out.println("Horizontal LOS");
           return true;
        }

        return false;
    }

    //todo: you can draw and delete walls on top of ghosts and pac man again

    private void update() {
        updateGameDrawRoot();
        if (hardModeEnable) {
            if (inky != null) {
                if (nsecondsInkySeen / 10.0  == 500000000) { //divide by ten so that comparison should be correct, check this
                    endGame();
                }

                if (straightSight(inky.getExactX(), inky.getExactY(), pac.getExactX(), pac.getExactY())) {
                    nsecondsInkySeen += gameTimeout;
                }

                else {
                    nsecondsInkySeen = 0;
                }
            }

            if (blinky != null) {
                if (nsecondsBlinkySeen / 10.0  == 500000000) { //divide by ten so that comparison should be correct, check this
                    endGame();
                }

                if (straightSight(blinky.getExactX(), blinky.getExactY(), pac.getExactX(), pac.getExactY())) {
                    nsecondsBlinkySeen += gameTimeout;
                }

                else {
                    nsecondsBlinkySeen = 0;
                }
            }

            if (pinky != null) {
                if (nsecondsPinkySeen / 10.0  == 500000000) { //divide by ten so that comparison should be correct, check this
                    endGame();
                }

                if (straightSight(pinky.getExactX(), pinky.getExactY(), pac.getExactX(), pac.getExactY())) {
                    nsecondsPinkySeen += gameTimeout;
                }

                else {
                    nsecondsPinkySeen = 0;
                }
            }

            if (clyde != null) {
                if (nsecondsClydeSeen / 10.0  == 500000000) { //divide by ten so that comparison should be correct, check this
                    endGame();
                }

                if (straightSight(clyde.getExactX(), clyde.getExactY(), pac.getExactX(), pac.getExactY())) {
                    nsecondsClydeSeen += gameTimeout;
                }

                else {
                    nsecondsClydeSeen = 0;
                }
            }

            //todo if it's been ten seconds, decrease update time for ghosts pathfinding by 100ms
        }

        if (inkyEnable.isSelected()) {
            inky.step();
        }

        if (blinkyEnable.isSelected()) {
            blinky.step();
        }

        if (pinkyEnable.isSelected()) {
            pinky.step();
        }

        if (clydeEnable.isSelected()) {
            clyde.step();
        }

        if (isDead()) {
            endGame();
        }
    }

    public Node[] get1DGrid() {
        Node[] ret = new Node[this.size * this.size];

        for (int i = 0 ; i < this.grid.length ; i++) {
            for (int j = 0 ; j < this.grid[0].length ; j++) {
                ret[i * j] = this.grid[i][j];
            }
        }

        return ret;
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

    private void updateGameDrawRoot() {
        gameDrawRoot.getChildren().removeAll(pac,inky,blinky,pinky,clyde);
        gameDrawRoot.getChildren().add(pac);

        if (inkyEnable.isSelected())
            gameDrawRoot.getChildren().add(inky);

        if (blinkyEnable.isSelected())
            gameDrawRoot.getChildren().add(blinky);

        if (pinkyEnable.isSelected())
            gameDrawRoot.getChildren().add(pinky);

        if (clydeEnable.isSelected())
            gameDrawRoot.getChildren().add(clyde);
    }

    @FXML
    private void startGame(ActionEvent e) {
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

            inkyChoice.setDisable(false);
            blinkyChoice.setDisable(false);
            pinkyChoice.setDisable(false);
            clydeChoice.setDisable(false);
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
        }
    }

    private void endGame() {
        //todo tell user they lost, disable start/stop button so that they must press reset button
    }

    @FXML
    private void resetGame(ActionEvent e) {
        System.out.println("Reset Game");

        if (gameRunning) {
            startButton.setText("Start Game");
            gameRunning = !gameRunning;
            drawWallsMode = true;
            drawWallsButton.setText("Walls: Draw");

            tim.stop();
            tim = null;
            Main.primaryStage.removeEventFilter(KeyEvent.KEY_PRESSED, pacMovement);

            inkyEnable.setDisable(false);
            blinkyEnable.setDisable(false);
            pinkyEnable.setDisable(false);
            clydeEnable.setDisable(false);

            inkyChoice.setDisable(false);
            blinkyChoice.setDisable(false);
            pinkyChoice.setDisable(false);
            clydeChoice.setDisable(false);
        }

        gameDrawRoot.getChildren().clear();
        pac = null;
        inky = null;
        blinky = null;
        pinky = null;
        clyde = null;

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(i,0,i,400);
            lin.setStroke(javafx.scene.paint.Color.rgb(0,0,0));
            gameDrawRoot.getChildren().add(lin);
        }

        for (int i = 0 ; i <= 400 ; i += 10) {
            Line lin = new Line(0,i,400,i);
            lin.setStroke(javafx.scene.paint.Color.rgb(0,0,0));
            gameDrawRoot.getChildren().add(lin);
        }

        for (int i = 0 ; i < grid.length ; i++) {
            for (int j = 0 ; j < grid[0].length ; j++) {
                grid[i][j] = null;
            }
        }

        inkyChoice.setValue("BFS");
        blinkyChoice.setValue("BFS");
        pinkyChoice.setValue("BFS");
        clydeChoice.setValue("BFS");

        inkyEnable.setSelected(false);
        blinkyEnable.setSelected(false);
        pinkyEnable.setSelected(false);
        clydeEnable.setSelected(false);
    }

    @FXML
    private void minimize_stage(MouseEvent e) {
        Main.primaryStage.setIconified(true);
    }

    @FXML
    private void close_app(MouseEvent e) {
        System.exit(0);
    }

    private boolean isDead() {
        //todo check neighbors of pac for enabled ghosts positions, if equal, return true
        return false;
    }

    private EventHandler<KeyEvent> pacMovement = new EventHandler<>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            if (gameRunning) {
                System.out.println("here");
                switch (keyEvent.getCode()) {
                    case A:
                        if (pac.getExactX() - 1 >= 0 && grid[pac.getExactX() - 1][pac.getExactY()] == null) {
                            grid[pac.getExactX()][pac.getExactY()] = null;
                            pac.setTranslateX(pac.getTranslateX() - 10.0);
                            pac.setExactX(pac.getExactX() - 1);
                        }
                        break;
                    case S:
                        if (pac.getExactY() + 1 < 40 && grid[pac.getExactX()][pac.getExactY() + 1] == null) {
                            grid[pac.getExactX()][pac.getExactY()] = null;
                            pac.setTranslateY(pac.getTranslateY() + 10.0);
                            pac.setExactY(pac.getExactY() + 1);
                            grid[pac.getExactX()][pac.getExactY()] = pac;
                        }
                        break;
                    case D:
                        if (pac.getExactX() + 1 < 40 && grid[pac.getExactX() + 1][pac.getExactY()] == null) {
                            grid[pac.getExactX()][pac.getExactY()] = null;
                            pac.setTranslateX(pac.getTranslateX() + 10.0);
                            pac.setExactX(pac.getExactX() + 1);
                            grid[pac.getExactX()][pac.getExactY()] = pac;
                        }
                        break;
                    case W:
                        if (pac.getExactY() - 1 >= 0 && grid[pac.getExactX()][pac.getExactY() - 1] == null) {
                            grid[pac.getExactX()][pac.getExactY()] = null;
                            pac.setTranslateY(pac.getTranslateY() - 10.0);
                            pac.setExactY(pac.getExactY() - 1);
                            grid[pac.getExactX()][pac.getExactY()] = pac;
                        }
                        break;
                }
            }
        }
    };
}
