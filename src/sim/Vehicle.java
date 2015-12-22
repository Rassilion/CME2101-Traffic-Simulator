package sim;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.Random;

public class Vehicle {

    protected String startingPoint;
    protected String name;
    protected Node currentNode;
    protected Node nextNode;
    protected Node previousNode;
    protected Paint color;
    protected int wait;
    private int sumWait;
    protected boolean moved;
    private Hashtable loop;
    Color[] clr = {Color.AQUA, Color.BLACK, Color.AZURE, Color.SALMON, Color.BURLYWOOD, Color.DARKMAGENTA, Color.YELLOW, Color.NAVY, Color.GAINSBORO, Color.FUCHSIA};

    public Node getPreviousNode() {
        return previousNode;
    }

    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Vehicle(Node startingPoint, String name, Hashtable ht) {
        this.startingPoint = startingPoint.name;
        this.currentNode = startingPoint;
        this.loop = ht;
        nextNode = null;
        previousNode = null;
        this.name = name;
        this.color = clr[Integer.parseInt(name.substring(1)) % 10];
        wait = 0;
        sumWait = 0;
        moved = false;
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public void display() {
        System.out.println("name: " + name + " start:" + startingPoint + " wait:" + wait + " sumWait:" + sumWait);
    }

    public String toString() {
        return "name: " + name + " start:" + startingPoint;
    }

    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    public Node getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public int getSumWait() {
        return sumWait;
    }

    public void setSumWait(int sumWait) {
        this.sumWait = sumWait;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    public void move() {
        if (nextNode != null && !moved) {
            if (nextNode.addVehicle(this)) {
                moved = true;
                currentNode.deleteVehicle(this);
                previousNode = currentNode;
                currentNode = nextNode;
                wait = 0;
                currentNode.wait++;
            } else {//swap
                for (Vehicle v : nextNode.vehicles) {
                    if (currentNode == v.nextNode && !(v instanceof Ambulance)) {
                        swap(v);
                        break;
                    }
                }
            }
        }
    }

    private void swap(Vehicle v) {
        moved = true;
        currentNode.deleteVehicle(this);
        v.currentNode.deleteVehicle(v);
        nextNode.addVehicle(this);
        previousNode = currentNode;
        currentNode = nextNode;
        wait = 0;
        currentNode.wait++;

        v.moved = true;
        v.previousNode = v.currentNode;
        v.currentNode = v.nextNode;
        v.wait = 0;
        v.currentNode.addVehicle(v);
        v.currentNode.wait++;


    }


    public void heuristic1() {
        this.moved = false;
        Node temp = null;
        int a = loop.getElement(currentNode.name);
        for (int i = a; i < a + 4; i++) {
            if (currentNode.adjacent[i % 4] != null && currentNode.adjacent[i % 4] != previousNode) {
                temp = currentNode.adjacent[i % 4];
                break;
            } else if (currentNode.adjacent[i % 4] != null && currentNode.getAdjacentCount() == 1) {
                temp = currentNode.adjacent[i % 4];
                break;
            }
        }
        loop.setElement(currentNode.name, (a + 1));
        nextNode = temp;
    }

    public void heuristic2() {
        this.moved = false;
        Random rnd = new Random();
        Node temp = null;
        do {
            int i = rnd.nextInt(200) % 4;
            temp = currentNode.adjacent[i];
        } while (temp == null);

        nextNode = temp;

        int a = loop.getElement(currentNode.name);
        loop.setElement(currentNode.name, (a + 1));


    }

    public void updateWait() {
        moved = true;
        wait++;
        sumWait++;
        currentNode.wait += 1;

    }


}
