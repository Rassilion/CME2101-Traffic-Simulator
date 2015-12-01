package sim;

import java.util.ArrayList;

public class Map {

    private Node root;
    private ArrayList<Node> nodes;

    private int[][] adjacentMatrix;

    public Node getRoot() {
        return root;
    }

    public int[][] getAdjacentMatrix() {
        return adjacentMatrix;
    }

    public void setAdjacentMatrix(int[][] adjecentMatrix) {
        this.adjacentMatrix = adjecentMatrix;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public Map() {
        this.root = null;
        this.nodes = new ArrayList<>();
    }

    public void addNode(Node node) {
        if (root == null) {
            root = node;
        }
        nodes.add(node);
    }

    public Node getNode(String name) {
        for (Node node : nodes) {
            if (node.name.equals(name)) {
                return node;
            }
        }
        return null;
    }

    public void addEdge(Node from, Node to, int direc) {
        from.adjacent[direc] = to;

    }
}
