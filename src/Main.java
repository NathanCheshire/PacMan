import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static Stage primaryStage;
    public static Parent root;
    public static Scene primaryScene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("PacGUI.fxml"));
        primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);
        primaryStage.getIcons().add(new Image(
                Main.class.getResourceAsStream("Pac.png")));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
