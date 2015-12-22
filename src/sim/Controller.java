package sim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

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
    public Canvas canvas1;
    public Simulation s = new Simulation();
    public Button MoveButton;
    public Button backupButton;
    public TextField timeArea;
    public DatePicker datePicker;
    public Canvas backUpCanvas;
    public BarChart barChart1;
    public BarChart barChart2;
    public BarChart barChart3;


    //images
    public Image road = new Image("/road.png");
    public Image road2 = new Image("/road2.png");
    public Image node = new Image("/node.png");

    public Image de = new Image("/de.png");
    public Image ds = new Image("/ds.png");
    public Image dw = new Image("/dw.png");
    public Image dn = new Image("/dn.png");
    //table lists
    public ObservableList<Vehicle> car;
    public ObservableList<Ambulance> ambulance;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        drawVehicle();
        updateChart();
    }

    //Rewrite tables on screen
    public void updateTables() {
        carList.refresh();
        ambulanceList.refresh();
    }

    public void updateChart() {
        barChart1.getData().clear();
        XYChart.Series series1 = new XYChart.Series();
        for (int i = 0; i < s.getVehicles().length; i++) {
            if (s.getVehicles()[i] != null) {
                series1.getData().add(new XYChart.Data(s.getVehicles()[i].name, s.getVehicles()[i].getSumWait()));
            }
        }
        barChart1.getData().addAll(series1);

        XYChart.Series series2 = new XYChart.Series();
        barChart2.getData().clear();
        for (int i = 0; i < s.getAmbulances().length; i++) {
            if (s.getAmbulances()[i] != null) {
                series2.getData().add(new XYChart.Data(s.getAmbulances()[i].name, (int) s.getAmbulances()[i].endTime - (int) s.getAmbulances()[i].startTime));
            }
        }
        barChart2.getData().addAll(series2);

        XYChart.Series series3 = new XYChart.Series();
        barChart3.getData().clear();
        for (Node node : s.getMap().getNodes()) {
            series3.getData().add(new XYChart.Data(node.name, node.wait));
        }
        barChart3.getData().addAll(series3);


    }

    //Move button
    public void ButtonClick() {
        //if simulation not finished
        if (!s.finishCondition()) {
            //incrase tick
            s.setTick(s.getTick() + 1);
            timeArea.setText(String.valueOf(s.getTick()));
            //move vehicles
            s.simulate();
            //update screen
            drawVehicle();
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
        System.out.println(datePicker.getValue().toString());

    }

    //Key listener
    public void ButtonKey(KeyEvent e) {
        if (e.getCode().toString().equals("K"))
            ButtonClick();
    }

    //draw map on screen
    public void drawMap() {
        GraphicsContext gc = layer1.getGraphicsContext2D();
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
                drawEast(node);
            }
            if (node.adjacent[1] != null) {
                drawSouth(node);
            }
            if (node.adjacent[2] != null) {
                drawWest(node);
            }
            if (node.adjacent[3] != null) {
                drawNorth(node);
            }
        }
    }

    //draw vehicles
    public void drawVehicle() {
        GraphicsContext gc = layer3.getGraphicsContext2D();
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


    //Edge and direction draw
    public void drawSouth(Node from) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road2, from.x, from.y + node.getHeight());
        gc2.drawImage(ds, from.x, from.y + node.getHeight());
    }

    public void drawNorth(Node from) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road2, from.x, from.y - road.getWidth());
        gc2.drawImage(dn, from.x, from.y - road.getWidth());
    }

    public void drawEast(Node from) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road, from.x + node.getHeight(), from.y);
        gc2.drawImage(de, from.x + node.getHeight(), from.y);
    }

    public void drawWest(Node from) {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        GraphicsContext gc2 = layer2.getGraphicsContext2D();
        gc.drawImage(road, from.x - road.getWidth(), from.y);
        gc2.drawImage(dw, from.x - road.getWidth(), from.y);
    }


}
