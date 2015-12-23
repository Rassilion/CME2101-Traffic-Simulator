package sim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    //javafx objects
    public TableView<Vehicle> carList;
    public TableView<Ambulance> ambulanceList;
    public TableColumn<Vehicle, String> carName;
    public Canvas layer1;//node and road layer
    public Canvas layer2;//direction layer
    public Canvas layer3;//vehicle layer
    public TableView tableBack;

    public Canvas layer4;//node and road layer
    public Canvas layer5;//direction layer
    public Canvas layer6;//vehicle layer

    public ToggleGroup heuristicToggle;
    public RadioButton heuristic1;

    static public Simulation s = new Simulation("RoadMap.txt", "Vehicles.txt");

    public Simulation s2 = new Simulation(true);

    public ComboBox simulationCombo;
    public ComboBox timeCombo;

    public Button MoveButton;
    public Button backupButton;
    public TextField timeArea;
    public DatePicker datePicker;
    public Canvas backUpCanvas;
    public BarChart barChart1;
    public BarChart barChart2;
    public BarChart barChart3;
    public Label carWait;
    public Label ambulanceWait;


    //images
    private Image road = new Image("/road.png");
    private Image road2 = new Image("/road2.png");
    private Image node = new Image("/node.png");

    private Image de = new Image("/de.png");
    private Image ds = new Image("/ds.png");
    private Image dw = new Image("/dw.png");
    private Image dn = new Image("/dn.png");
    //table lists
    public ObservableList<Vehicle> car;
    public ObservableList<Ambulance> ambulance;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        start();
    }

    public void start() {
        //Table list
        car = FXCollections.observableArrayList();
        for (Vehicle vehicle : s.getVehicles()) {
            if (vehicle == null) {
                break;
            }
            car.add(vehicle);
        }
        carList.setItems(car);

        ambulance = FXCollections.observableArrayList();
        for (Ambulance vehicle : s.getAmbulances()) {
            if (vehicle == null) {
                break;
            }
            ambulance.add(vehicle);
        }

        ambulanceList.setItems(ambulance);
        drawMap();
        drawVehicle(s, layer3);
        updateChart();
        s.initSQL();
    }

    //Rewrite tables on screen
    public void updateTables() {
        carList.refresh();
        ambulanceList.refresh();
    }

    public void SimFill(String date) {
        simulationCombo.getItems().clear();
        timeCombo.getItems().clear();
        ArrayList<String> k = s.getMysql().selectSim(date);
        simulationCombo.getItems().addAll(k);
    }

    public void TimeFill(String sim) {

        timeCombo.getItems().clear();
        ArrayList<String> k = s.getMysql().selectTime(Integer.parseInt(sim));
        timeCombo.getItems().addAll(k);
    }

    public void ChooseDate() {
        SimFill(datePicker.getValue().toString());
    }

    public void ChooseSim() {

        TimeFill(simulationCombo.getValue().toString());
    }

    public void updateChart() {
        barChart1.getData().clear();
        XYChart.Series series1 = new XYChart.Series();
        int totalCarWait = 0, totalAmbulanceTime = 0;
        for (int i = 0; i < s.getVehicles().length; i++) {
            if (s.getVehicles()[i] != null) {

                series1.getData().add(new XYChart.Data(s.getVehicles()[i].name + "(" + s.getVehicles()[i].getSumWait() + ")", s.getVehicles()[i].getSumWait()));
                totalCarWait += s.getVehicles()[i].getSumWait();

            }
        }
        barChart1.getData().addAll(series1);

        XYChart.Series series2 = new XYChart.Series();
        barChart2.getData().clear();
        for (int i = 0; i < s.getAmbulances().length; i++) {
            if (s.getAmbulances()[i] != null) {
                int time = s.getAmbulances()[i].endTime - s.getAmbulances()[i].startTime;
                series2.getData().add(new XYChart.Data(s.getAmbulances()[i].name + "(" + time + ")", s.getAmbulances()[i].endTime - s.getAmbulances()[i].startTime));
                totalAmbulanceTime += s.getAmbulances()[i].endTime - s.getAmbulances()[i].startTime;

            }
        }
        barChart2.getData().addAll(series2);

        XYChart.Series series3 = new XYChart.Series();
        barChart3.getData().clear();
        for (Node node : s.getMap().getNodes()) {
            series3.getData().add(new XYChart.Data(node.name + "(" + node.wait + ")", node.wait));
        }
        barChart3.getData().addAll(series3);

        carWait.setText("Total car Wait: " + totalCarWait);
        ambulanceWait.setText("Total ambulance time: " + totalAmbulanceTime);


    }

    public void newButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("new.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.showAndWait();
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Move button
    public void ButtonClick() {
        //if simulation not finished
        if (!s.finishCondition()) {
            //incrase tick
            s.setTick(s.getTick() + 1);
            timeArea.setText(String.valueOf(s.getTick()));
            //move vehicles
            if (heuristicToggle.getSelectedToggle() == heuristic1)
                s.simulate(1);
            else
                s.simulate(2);
            //update screen
            drawVehicle(s, layer3);
            updateTables();
            updateChart();
        }
        //else alert user
        if (s.finishCondition()) {
            alert();
        }
    }

    public void alert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Simulation Message");
        alert.setHeaderText("Simulation Message");
        alert.setContentText("Simulation Finished !!!!!!!!");
        alert.showAndWait();
    }

    public void BackUpButton() {
        s = new Simulation(true);
        System.out.println(Integer.parseInt(simulationCombo.getValue().toString()));
        s2.getMysql().selectN(Integer.parseInt(simulationCombo.getValue().toString()), s2);
       String[][] a= s2.getMysql().Time_Select(datePicker.getValue().toString(), simulationCombo.getValue().toString(), timeCombo.getValue().toString(), s2);
        Table(a);
        drawBackup();

    }

    //Key listener
    public void ButtonKey(KeyEvent e) {
        if (e.getCode().toString().equals("K"))
            ButtonClick();
    }

    //draw back up
    public void drawBackup() {
        GraphicsContext gc = layer4.getGraphicsContext2D();
        gc.clearRect(0, 0, 1000, 1000);
        GraphicsContext gc2 = layer5.getGraphicsContext2D();
        gc2.clearRect(0, 0, 1000, 1000);
        GraphicsContext gc3 = layer6.getGraphicsContext2D();
        gc3.clearRect(0, 0, 1000, 1000);
        ArrayList<Node> nodes = s2.getMap().getNodes();
        //write nodes on screen until every node on screen
        for (Node node : nodes) {
            drawNode(node, layer4);
        }

        //draw roads
        for (Node node : nodes) {
            if (node.adjacent[0] != null) {
                drawEast(node, layer4, layer5);
            }
            if (node.adjacent[1] != null) {
                drawSouth(node, layer4, layer5);
            }
            if (node.adjacent[2] != null) {
                drawWest(node, layer4, layer5);
            }
            if (node.adjacent[3] != null) {
                drawNorth(node, layer4, layer5);
            }
        }
        drawVehicle(s2, layer6);
    }

    //draw map on screen
    public void drawMap() {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        gc.clearRect(0, 0, 2000, 2000);
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc2.clearRect(0, 0, 2000, 2000);
        GraphicsContext gc3 = layer3.getGraphicsContext2D();
        gc3.clearRect(0, 0, 2000, 2000);
        Font theFont = Font.font("Times New Roman", 10);
        gc.setFont(theFont);
        s.getMap().getRoot().x = 20;
        s.getMap().getRoot().y = 20;
        ArrayList<Node> nodes = s.getMap().getNodes();

        gc.drawImage(this.node, s.getMap().getRoot().x, s.getMap().getRoot().y);
        gc.fillText(s.getMap().getRoot().name, s.getMap().getRoot().x + this.node.getHeight(), s.getMap().getRoot().y);
        //write nodes on screen until every node on screen
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Node node : nodes) {

                System.out.print(node.name + " ");
                drawNode(node);
                if (node.x == 0 && node.y == 0) {
                    flag = true;
                }
            }
            System.out.println(" ");
        }
        //draw roads
        for (Node node : nodes) {
            if (node.adjacent[0] != null) {
                drawEast(node, layer1, layer2);
            }
            if (node.adjacent[1] != null) {
                drawSouth(node, layer1, layer2);
            }
            if (node.adjacent[2] != null) {
                drawWest(node, layer1, layer2);
            }
            if (node.adjacent[3] != null) {
                drawNorth(node, layer1, layer2);
            }
        }
    }

    //draw vehicles
    public void drawVehicle(Simulation s, Canvas layer) {
        GraphicsContext gc = layer.getGraphicsContext2D();
        //clear canvas
        gc.clearRect(0, 0, 6000, 5000);
        ArrayList<Node> nodes = s.getMap().getNodes();

        for (Node node : nodes) {
            int xPoint = 42, yPoint = 39;
            for (int i = 0; i < node.vehicles.length; i++) {
                if (node.vehicles[i] != null) {
                    if (node.vehicles[i] instanceof Ambulance) {
                        if (((Ambulance) node.vehicles[i]).getStartTime() > s.getTick()) {
                            System.out.print("\n5");
                            continue;
                        }
                    }
                    //choose position for vehicle
                    if (i == 1 || i == 3) {
                        xPoint = xPoint + 11;
                    }
                    if (i == 2) {
                        xPoint = xPoint - 14;
                        yPoint = yPoint + 18;
                    }

                    gc.setFill(node.vehicles[i].getColor()); //Chose color type

                    gc.fillRect(node.x + xPoint, node.y + yPoint, 7, 7);
                }
            }
        }
    }

    //Draw nodes and set Node position
    public void drawNode(Node node) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        if (node.adjacent[0] != null) {
            if (!(node.x == 0 && node.y == 0) && node.adjacent[0].x == 0 && node.adjacent[0].y == 0) {
                node.adjacent[0].x = node.x + (road.getWidth() + this.node.getHeight());
                node.adjacent[0].y = node.y;
                gc.drawImage(this.node, node.adjacent[0].x, node.adjacent[0].y);
                gc.fillText(node.adjacent[0].name, node.adjacent[0].x + this.node.getHeight(), node.adjacent[0].y);
            } else if (!(node.adjacent[0].x == 0 && node.adjacent[0].y == 0)) {
                node.x = node.adjacent[0].x - (road.getWidth() + this.node.getHeight());
                node.y = node.adjacent[0].y;
                gc.drawImage(this.node, node.x, node.y);
                gc.fillText(node.name, node.x + this.node.getHeight(), node.y);
            }
        }
        if (node.adjacent[1] != null) {
            if (!(node.x == 0 && node.y == 0) && node.adjacent[1].x == 0 && node.adjacent[1].y == 0) {
                node.adjacent[1].x = node.x;
                node.adjacent[1].y = node.y + (road.getWidth() + this.node.getHeight());
                gc.drawImage(this.node, node.adjacent[1].x, node.adjacent[1].y);
                gc.fillText(node.adjacent[1].name, node.adjacent[1].x + this.node.getHeight(), node.adjacent[1].y);
            } else if (!(node.adjacent[1].x == 0 && node.adjacent[1].y == 0)) {
                node.x = node.adjacent[1].x;
                node.y = node.adjacent[1].y - (road.getWidth() + this.node.getHeight());
                gc.drawImage(this.node, node.x, node.y);
                gc.fillText(node.name, node.x + this.node.getHeight(), node.y);
            }
        }
        if (node.adjacent[2] != null) {
            if (!(node.x == 0 && node.y == 0) && node.adjacent[2].x == 0 && node.adjacent[2].y == 0) {
                node.adjacent[2].x = node.x - (road.getWidth() + this.node.getHeight());
                node.adjacent[2].y = node.y;
                gc.drawImage(this.node, node.adjacent[2].x, node.adjacent[2].y);
                gc.fillText(node.adjacent[2].name, node.adjacent[2].x + this.node.getHeight(), node.adjacent[2].y);
            } else if (!(node.adjacent[2].x == 0 && node.adjacent[2].y == 0)) {
                node.x = node.adjacent[2].x + (road.getWidth() + this.node.getHeight());
                node.y = node.adjacent[2].y;
                gc.drawImage(this.node, node.x, node.y);
                gc.fillText(node.name, node.x + this.node.getHeight(), node.y);
            }
        }
        if (node.adjacent[3] != null) {
            if (!(node.x == 0 && node.y == 0) && node.adjacent[3].x == 0 && node.adjacent[3].y == 0) {
                node.adjacent[3].x = node.x;
                node.adjacent[3].y = node.y - (road.getWidth() + this.node.getHeight());
                gc.drawImage(this.node, node.adjacent[3].x, node.adjacent[3].y);
                gc.fillText(node.adjacent[3].name, node.adjacent[3].x + this.node.getHeight(), node.adjacent[3].y);
            } else if (!(node.adjacent[3].x == 0 && node.adjacent[3].y == 0)) {
                node.x = node.adjacent[3].x;
                node.y = node.adjacent[3].y + (road.getWidth() + this.node.getHeight());
                gc.drawImage(this.node, node.x, node.y);
                gc.fillText(node.name, node.x + this.node.getHeight(), node.y);
            }
        }
    }

    public void drawNode(Node node, Canvas layer) {
        GraphicsContext gc = layer.getGraphicsContext2D();
        gc.drawImage(this.node, node.x, node.y);
        gc.fillText(node.name, node.x + this.node.getHeight(), node.y);
    }


    //Edge and direction draw
    public void drawSouth(Node from, Canvas layer1, Canvas layer2) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road2, from.x, from.y + node.getHeight());
        gc2.drawImage(ds, from.x, from.y + node.getHeight());
    }

    public void drawNorth(Node from, Canvas layer1, Canvas layer2) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road2, from.x, from.y - road.getWidth());
        gc2.drawImage(dn, from.x, from.y - road.getWidth());
    }

    public void drawEast(Node from, Canvas layer1, Canvas layer2) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road, from.x + node.getHeight(), from.y);
        gc2.drawImage(de, from.x + node.getHeight(), from.y);
    }

    public void drawWest(Node from, Canvas layer1, Canvas layer2) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road, from.x - road.getWidth(), from.y);
        gc2.drawImage(dw, from.x - road.getWidth(), from.y);
    }
public void Table(String[][] b)
{
    for (int i = 0; i <b.length ; i++) {
        tableBack.getColumns().addAll(b[i][0],b[i][1],b[i][2]);
    }


}

}
