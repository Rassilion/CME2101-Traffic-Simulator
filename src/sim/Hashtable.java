package sim;


import java.util.ArrayList;

public class Hashtable {

    public int[][] hashArray;


    public Hashtable(ArrayList<Node> nodes) {
        hashArray = new int[nodes.size()][2];

        for (Node node : nodes) {
            this.Addelement(node.name, 0);
        }
    }

    public int Key(String edge) {
        int key = 0;
        for (int i = 0; i < edge.length(); i++) {
            key += (int) edge.charAt(i);
        }
        return key;
    }

    public void Addelement(String node, int direction) {
        int i = 0;
        while (hashArray[Linearprobing(Key(node), i)][0] != 0) {
            i++;
        }
        hashArray[Linearprobing(Key(node), i)][0] = Key(node);
        hashArray[Linearprobing(Key(node), i)][1] = direction;

    }

    public int hashFunction(int key) {

        return key % hashArray.length;
    }

    public int Linearprobing(int key, int i) {

        return (hashFunction(key) + i) % hashArray.length;

    }

    public int getElement(String node) {
        int i = 0;
        while (hashArray[Linearprobing(Key(node), i)][0] != Key(node)) {
            i++;
        }

        return hashArray[Linearprobing(Key(node), i)][1];
    }

    public void setElement(String node, int direction) {
        int i = 0;
        while (hashArray[Linearprobing(Key(node), i)][0] != Key(node)) {
            i++;
        }
        hashArray[Linearprobing(Key(node), i)][1] = direction;
    }
}
