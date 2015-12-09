package sim;

import javafx.scene.paint.Color;

import java.util.Stack;

public class Ambulance extends Vehicle {

    String destination;
    int startTime;

    public Ambulance(String destination, int startingtime, Node startingPoint, String name) {
        super(startingPoint, name, null);
        this.destination = destination;
        this.startTime = startingtime;
        this.setColor(Color.DARKRED);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    @Override
    public void display() {
        System.out.println("name: " + name + " start:" + startingPoint + " dest: " + destination + " time: " + startTime);
    }

    @Override
    public String toString() {
        return "name: " + name + " start:" + startingPoint + " dest: " + destination + " time: " + startTime;
    }



    private void shortestPath() {
        

    }

}
