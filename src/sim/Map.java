package sim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

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

    public Stack<Node> BFS(Node startPoint, Node endPoint) {
        Queue<Node> q = new LinkedList<>();
        boolean[] visited = new boolean[nodes.size()];
        Node[] previous = new Node[nodes.size()];
        q.add(startPoint);
        visited[startPoint.id] = true;
        while (!q.isEmpty()) {
            Node n, child;
            n = (q.peek());
            for (int i = 0; i < 4; i++) {

                if (n.adjacent[i] != null) {
                    if (visited[n.adjacent[i].id] != true) {
                        q.add(n.adjacent[i]);
                        previous[n.adjacent[i].id] = n;
                        visited[n.adjacent[i].id] = true;
                    }
                }

            }
            q.remove();


        }
        Stack<Node> path = new Stack<>();
        Node temp = endPoint;

        while (temp != startPoint) {
            path.push(temp);
            temp = previous[temp.id];


        }
        path.push(startPoint);
        return path;
    }


<<<<<<< HEAD

}
=======
}
>>>>>>> ed66953b21641ab11f75f0afc126e0ec7429d346
