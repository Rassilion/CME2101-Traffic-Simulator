package sim;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

// ambulanslari zamani gelince mapa ekle

public class Controller implements Initializable {
    //javafx objects
    public TableView<Vehicle> carList;
    public TableView<Ambulance> ambulanceList;
    public TableColumn<Vehicle, String> carName;
    public Canvas layer1;
    public Canvas layer2;
    public Canvas layer3;
    public Simulation s = new Simulation();
    public Button MoveButton;
    public Button backupButton;
    public TextField timeArea;
    public DatePicker datePicker;
    public Canvas backUpCanvas;


    //images
    public Image road = new Image("/road.png");
    public Image road2 = new Image("/road2.png");
    public Image node = new Image("/node.png");

    public Image de = new Image("/de.png");
    public Image ds = new Image("/ds.png");
    public Image dw = new Image("/dw.png");
    public Image dn = new Image("/dn.png");

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

    }

    public void updateTables(){
        carList.refresh();
        ambulanceList.refresh();
    }

    //Move button
    public void ButtonClick() {
        s.setTick(s.getTick() + 1);
        timeArea.setText(String.valueOf(s.getTick()));
        s.simulate();
        drawVehicle();
        updateTables();

    }
    public void BackUpButton(){
        System.out.println(datePicker.getValue().toString());

    }

    //Key listener
    public void ButtonKey(KeyEvent e) {
        if (e.getCode().toString().equals("K"))
            ButtonClick();
    }

    public void drawMap() {
        GraphicsContext gc = layer1.getGraphicsContext2D();
        Font theFont = Font.font("Times New Roman", 10);
        gc.setFont(theFont);
        s.getMap().getRoot().x = 20;
        s.getMap().getRoot().y = 20;
        //draw root and name
        gc.fillText(s.getMap().getRoot().name, s.getMap().getRoot().x + node.getHeight(), s.getMap().getRoot().y);
        gc.drawImage(node, s.getMap().getRoot().x, s.getMap().getRoot().y);
        ArrayList<Node> nodes = s.getMap().getNodes();
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Node node : nodes) {

                System.out.print(node.name);
                drawNode(node);
                if (node.x == 0 && node.y == 0) {
                    flag = true;

                }
            }
        }
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
        Random rnd = new Random();
        int Randomcolor = 0;
        GraphicsContext gc = layer3.getGraphicsContext2D();
        gc.clearRect(0, 0, 1000, 1000);
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
        if (node.x != 0 && node.y != 0) {
            if (node.adjacent[0] != null) {
                if (node.adjacent[0].x == 0 && node.adjacent[0].y == 0) {
                    node.adjacent[0].x = node.x + (road.getWidth() + this.node.getHeight());
                    node.adjacent[0].y = node.y;
                    gc.drawImage(this.node, node.adjacent[0].x, node.adjacent[0].y);
                    gc.fillText(node.adjacent[0].name, node.adjacent[0].x + this.node.getHeight(), node.adjacent[0].y);
                }
            }
            if (node.adjacent[1] != null) {
                if (node.adjacent[1].x == 0 && node.adjacent[1].y == 0) {
                    node.adjacent[1].x = node.x;
                    node.adjacent[1].y = node.y + (road.getWidth() + this.node.getHeight());
                    gc.drawImage(this.node, node.adjacent[1].x, node.adjacent[1].y);
                    gc.fillText(node.adjacent[1].name, node.adjacent[1].x + this.node.getHeight(), node.adjacent[1].y);
                }
            }
            if (node.adjacent[2] != null) {
                if (node.adjacent[2].x == 0 && node.adjacent[2].y == 0) {
                    node.adjacent[2].x = node.x - (road.getWidth() + this.node.getHeight());
                    node.adjacent[2].y = node.y;
                    gc.drawImage(this.node, node.adjacent[2].x, node.adjacent[2].y);
                    gc.fillText(node.adjacent[2].name, node.adjacent[2].x + this.node.getHeight(), node.adjacent[2].y);
                }
            }
            if (node.adjacent[3] != null) {
                if (node.adjacent[3].x == 0 && node.adjacent[3].y == 0) {
                    node.adjacent[3].x = node.x;
                    node.adjacent[3].y = node.y - (road.getWidth() + this.node.getHeight());
                    gc.drawImage(this.node, node.adjacent[3].x, node.adjacent[3].y);
                    gc.fillText(node.adjacent[3].name, node.adjacent[3].x + this.node.getHeight(), node.adjacent[3].y);
                }
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
