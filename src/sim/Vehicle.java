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
    private Hashtable loop;
    Color[] clr = {Color.AQUA, Color.BLACK, Color.AZURE, Color.SALMON, Color.BURLYWOOD, Color.GREEN, Color.YELLOW, Color.NAVY, Color.GAINSBORO, Color.FIREBRICK};

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
        this.color = clr[Integer.parseInt(name.substring(1))];
        wait = 0;
        sumWait = 0;
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
        if (nextNode != null) {
            currentNode.deleteVehicle(this);
            previousNode = currentNode;
            currentNode = nextNode;
            wait = 0;
            currentNode.addVehicle(this);
            nextNode = null;
        }
    }

    public void heuristic1() {
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
        Random rnd = new Random();
        Node temp = null;
        do {
            int i = rnd.nextInt(200) % 4;
            temp = currentNode.adjacent[i];
        } while (temp == null);

        nextNode = temp;

    }

    public void updateWait() {
        wait++;
        sumWait++;

    }


}
