package sim;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


public class NewController implements Initializable {
    public TextField mapText;
    public TextField vehicleText;
    public Button start;
    public String map, vehicle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void mapButtonClick(ActionEvent event) {
        File file = fileChooser();
        map = file.toString();
        mapText.setText(file.toString());
    }

    public File fileChooser() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        String currentDir = System.getProperty("user.dir");
        File file = new File(currentDir);
        fileChooser.setInitialDirectory(file);
        file = fileChooser.showOpenDialog(new Stage());
        return file;
    }

    public void vehicleButtonClick(ActionEvent event) {
        File file = fileChooser();
        vehicle = file.toString();
        vehicleText.setText(file.toString());
    }

    public void startClick(ActionEvent event) {
        System.out.println(map);
        System.out.println(vehicle);
       Simulation.Mappath=map;
        Simulation.Vehiclepath=vehicle;

        Stage stage = (Stage) start.getScene().getWindow();
        // do what you have to do
        stage.close();

    }
}