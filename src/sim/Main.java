package sim;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Stage s=new Stage();
        Parent root1 = FXMLLoader.load(getClass().getResource("new.fxml"));
        s.setTitle("Hello World");
        s.initStyle(StageStyle.UTILITY);
        s.setScene(new Scene(root1, 1000, 600));
        s.showAndWait();

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();







    }
    public void SecondScreen(Stage primaryStage){

    }


    public static void main(String[] args) {
        launch(args);
    }
}
