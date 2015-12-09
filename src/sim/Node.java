package sim;

public class Node {

    //E S W N
    public Node[] adjacent;
    public String name;
    public Vehicle[] vehicles;
    public double x;
    public double y;
    public int id;
    public static int uniqueId = 0;

    public Node(String name) {
        this.adjacent = new Node[4];
        this.name = name;
        x = 0;
        y = 0;
        vehicles = new Vehicle[4];
        id = uniqueId;
        uniqueId++;

    }

    public void addVehicle(Vehicle vehicle) {
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i] == null) {
                vehicles[i] = vehicle;
                break;
            }
        }
    }

    public void deleteVehicle(Vehicle vehicle) {
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i] == vehicle) {
                vehicles[i] = null;
            }
        }
    }

    public int getVehiclecount() {
        int j = 0;
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i] != null) {
                j++;
            }
        }
        return j;
    }

    public int getAdjacentCount() {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (adjacent[i] != null) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return name;
    }
}
