package sim;

import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Simulation {

    private Vehicle[] vehicles;
    private Ambulance[] ambulances;



  public   SQL mysql = new SQL();
    public SQL getMysql() {
        return mysql;
    }

    public void setMysql(SQL mysql) {
        this.mysql = mysql;
    }
    private Map map;

    private int tick;

    public Simulation(String path,String path2) {
        vehicles = new Vehicle[100];
        ambulances = new Ambulance[100];
        map = new Map();


        readNode(path);
        readEdge(path);
        readVehicle(path2);

        display();

        try {
            mysql.Sim_Control();
            writeNode();
            writeVehicle();
            writeTime(tick);
        } catch (Exception e) {
            //TODO print some information
        }

    }

    public void display() {
        System.out.println("vehicles");
        for (Vehicle vehicle : vehicles) {
            if (vehicle == null) {
                break;
            }
            vehicle.display();
        }
        for (Ambulance vehicle : ambulances) {
            if (vehicle == null) {
                break;
            }
            vehicle.display();
        }
    }

    public boolean finishCondition() {
        boolean flag = true;
        for (Ambulance vehicle : ambulances) {
            if (vehicle != null) {
                if (!vehicle.finish) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    public boolean wait(Vehicle vehicle) {
        boolean flag = true;
        if (vehicle.getNextNode().getVehiclecount() == 4) {
            for (Vehicle v : vehicle.getNextNode().vehicles) {
                if (!v.isMoved())
                    flag = false;
            }
        }
        return flag;
    }


    public void simulate() {
        for (Vehicle vehicle : vehicles) {
            if (vehicle == null) {
                break;
            }
            //select next node
            vehicle.heuristic1();
        }

        for (Ambulance vehicle : ambulances) {
            if (vehicle == null) {
                break;
            }
            vehicle.move();

            if (tick == vehicle.getStartTime()) {
                vehicle.setActive();
            }

        }
        boolean flag = true;
        while (flag) {
            flag = false;
            for (Vehicle vehicle : vehicles) {
                if (vehicle == null) {
                    break;
                }
                vehicle.move();
                if (!vehicle.isMoved()) {
                    if (!wait(vehicle))
                        flag = true;
                    else
                        vehicle.updateWait();
                }
            }
        }
        display();
        try {
            writeTime(tick);
        } catch (
                Exception e
                ) {
            //TODO print some information
        }

    }

    public void getStatistics() {


    }

    public void addVehicle(Vehicle vehicle) {
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i] == null) {
                vehicles[i] = vehicle;
                break;
            }
        }
    }

    public void addAmbulance(Ambulance ambulance) {
        for (int i = 0; i < ambulances.length; i++) {
            if (ambulances[i] == null) {
                ambulances[i] = ambulance;
                break;
            }
        }
    }

    public void writeVehicle() {
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i] == null) {
                break;
            }

            mysql.Vehicle_Insert("Car", vehicles[i].getName());

        }
        for (int i = 0; i < ambulances.length; i++) {
            if (ambulances[i] == null) {
                break;
            }
            mysql.Vehicle_Insert("Ambulance", ambulances[i].getName());
        }

    }

    public void writeNode() {

        for (Node node : map.getNodes()) {
            String e = "0", s = "0", w = "0", n = "0";
            if (node.adjacent[0] != null) {
                e = node.adjacent[0].name;
            }
            if (node.adjacent[1] != null) {
                s = node.adjacent[1].name;
            }
            if (node.adjacent[2] != null) {
                w = node.adjacent[2].name;
            }
            if (node.adjacent[3] != null) {
                n = node.adjacent[3].name;
            }

            mysql.Node_Insert(node.name, e, s, w, n);
        }

    }

    public void writeTime(int time) {

        for (Node node : map.getNodes()) {

            for (int i = 0; i < node.vehicles.length; i++) {

                if (node.vehicles[i] == null) {
                    continue;
                }
                mysql.Time_Insert_V_N(time, node.name, node.vehicles[i].getName());


            }


        }


    }

    public void readVehicle(String url) {
        try {
            FileReader fileReader = new FileReader(new File(url));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(" "); // Split lines by space
                if (splited.length == 2) {
                    Vehicle vehicle = new Vehicle(map.getNode(splited[1]), splited[0], new Hashtable(map.getNodes()));
                    addVehicle(vehicle);
                    map.getNode(splited[1]).addVehicle(vehicle);
                } else if (splited.length == 4) {
                    Ambulance ambulance = new Ambulance(splited[3], Integer.parseInt(splited[1]), map.getNode(splited[2]), splited[0]);
                    addAmbulance(ambulance);
                    if (Integer.parseInt(splited[1]) == 0) {
                        ambulance.setActive();
                    }
                    ambulance.setShortestPath(map.BFS(map.getNode(splited[2]), map.getNode(splited[3])), map.BFS(map.getNode(splited[3]), map.getNode(splited[2])));
                } else {
                    System.out.println("input error");
                }
            }
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("Sorry, file not found..");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readNode(String url) {
        try {
            FileReader fileReader = new FileReader(new File(url));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(" ");
                if (!splited[0].equals(""))
                    map.addNode(new Node(splited[0]));
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Sorry, file not found..");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void readEdge(String url) {
        try {
            FileReader fileReader = new FileReader(new File(url));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(" ");
                String[] splited2 = splited[1].split(",");
                for (int j = 0; j < splited2.length; j++) {
                    if (!splited2[j].equals("0")) {
                        map.addEdge(map.getNode(splited[0]), map.getNode(splited2[j]), j);
                    }
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Sorry, file not found..");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int[][] readMap(String url) {
        int[][] tempArray = new int[11][11];
        try {
            FileReader fileReader = new FileReader(new File(url));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;

            int i = 0;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splited = line.split(" ");
                String[] splited2 = splited[1].split(",");
                for (int j = 0; j < splited2.length; j++) {
                    if (!splited2[j].equals("0")) {
                        tempArray[i][splited2[j].charAt(0) - 65] = 1;
                    }
                }
                i++;
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Sorry, file not found..");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tempArray;

    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }


    public Vehicle[] getVehicles() {
        return vehicles;
    }

    public void setVehicles(Vehicle[] vehicles) {
        this.vehicles = vehicles;
    }

    public Ambulance[] getAmbulances() {
        return ambulances;
    }

    public void setAmbulances(Ambulance[] ambulances) {
        this.ambulances = ambulances;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }
}
