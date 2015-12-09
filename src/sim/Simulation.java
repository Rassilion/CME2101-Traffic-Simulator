package sim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Simulation {

    private Vehicle[] vehicles;
    private Ambulance[] ambulances;
    SQL mysql = new SQL();
    private Map map;
    private int tick;

    public Simulation() {
        String path = "RoadMap.txt";
        String path2 = "Vehicles.txt";
        vehicles = new Vehicle[100];
        ambulances = new Ambulance[100];
        map = new Map();

        map.setAdjacentMatrix(readMap(path));

        readNode(path);
        readEdge(path);
        readVehicle(path2);

        System.out.println("  A B C D E F G H I J K");
        String str = "ABCDEFGHIJK";
        for (int i = 0; i < 11; i++) {
            System.out.print(str.charAt(i) + " ");
            for (int j = 0; j < 11; j++) {
                System.out.print(map.getAdjacentMatrix()[i][j] + " ");
            }
            System.out.println();
        }
        display();

        try {
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

        for (Vehicle vehicle : vehicles) {
            if (vehicle == null) {
                break;
            }
            // vehicle wait
            if (vehicle.getNextNode().getVehiclecount() == 4) {
                vehicle.updateWait();
                continue;
            }
            vehicle.move();
        }
        display();
        try {

            writeTime(tick);
        } catch (Exception e) {
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
            mysql.Node_Insert(node.name);
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
