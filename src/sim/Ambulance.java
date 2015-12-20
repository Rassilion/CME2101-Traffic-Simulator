package sim;

import javafx.scene.paint.Color;

import java.util.Stack;

public class Ambulance extends Vehicle {

    String destination;
    int startTime;
    int delay;
    int endTime;



    boolean active;
    boolean finish;

    Stack<Node> a = new Stack<Node>();
    Stack<Node> b = new Stack<Node>();


    public Ambulance(String destination, int startingtime, Node startingPoint, String name) {
        super(startingPoint, name, null);
        this.destination = destination;
        this.startTime = startingtime;
        this.setColor(Color.DARKRED);
        this.active=false;
        this.finish=false;
        this.endTime=startingtime;
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
    public boolean isActive() {
        return active;
    }

    public void setActive() {
        this.active = true;
        currentNode.addVehicle(this);
    }

    @Override
    public void display() {
        System.out.println("name: " + name + " start:" + startingPoint + " dest: " + destination + " time: " + startTime);
    }

    @Override
    public String toString() {
        return "name: " + name + " start:" + startingPoint + " dest: " + destination + " time: " + startTime;
    }

    @Override
    public void move() {
       if (this.active) {


           if (delay == 0) {
               if (a.isEmpty())
                   delay = 1;
               else {
                   this.nextNode = a.pop();
                   if (nextNode.getVehiclecount() == 4) {
                       for (int i = 0; i < 4; i++) {
                           nextNode.vehicles[i].move();
                       }
                   }
                   currentNode.deleteVehicle(this);
                   previousNode = currentNode;
                   currentNode = nextNode;
                   wait = 0;
                   currentNode.addVehicle(this);
                   nextNode = null;
               }
           } else {
               delay--;
               if (b==null){
                   active=false;
                   finish=true;
                   currentNode.deleteVehicle(this);
               }
               if (delay == 0) {

                   a = b;
                   b=null;
               }
           }
       }
    }

    public void setShortestPath(Stack<Node> path,Stack<Node> path2) {
        a = path;
        currentNode = a.pop();
        b=path2;


    }

}
