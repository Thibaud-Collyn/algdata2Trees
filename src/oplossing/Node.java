package oplossing;

public class Node<E extends Comparable<E>> {
    private E key1;
    private E key2;
    private Node<E> parentNode;
    private Node<E> childNode1;
    private Node<E> childNode2;
    private Node<E> childNode3;

    public Node (E valKey1, E valKey2, Node<E> parent) {
        key1 = valKey1;
        key2 = valKey2;
        parentNode = parent;
        childNode1 = null;
        childNode2 = null;
        childNode3 = null;
    }


    //Basic getters and setter for the node class.
    public E getKey1() {
        return key1;
    }

    public E getKey2() {
        return key2;
    }

    public Node<E> getParentNode() {
        return parentNode;
    }

    public Node<E> getChildNode1() {
        return childNode1;
    }

    public Node<E> getChildNode2() {
        return childNode2;
    }

    public Node<E> getChildNode3() {
        return childNode3;
    }

    public void setKey1(E key1) {
        this.key1 = key1;
    }

    public void setKey2(E key2) {
        this.key2 = key2;
    }

    public void setParentNode(Node<E> parentNode) {
        this.parentNode = parentNode;
    }

    public void setChildNode1(Node<E> childNode1) {
        this.childNode1 = childNode1;
    }

    public void setChildNode2(Node<E> childNode2) {
        this.childNode2 = childNode2;
    }

    public void setChildNode3(Node<E> childNode3) {
        this.childNode3 = childNode3;
    }

    //Specialised getters
    public E getLargestkey() {
        if (key2 == null) {
            return key1;
        }
        return key2;
    }

    //Functions returns the amount of key in the node
    public int nodeSize() {
        if (isEmpty()) {
            return 0;
        }
        return key2 != null ? 2 : 1;
    }

    public boolean containsKey(E key) {
        return key.compareTo(key1) == 0 || (key2!=null && key.compareTo(key2) == 0 );
    }

    public boolean isEmpty() {
        return key1 == null && key2 == null;
    }

    //Function that returns whether a Node has two keys or not.
    public boolean hasTwoKeys() {
        return key1 != null && key2 != null;
    }

    //This function adds a new key and ensures that key1 and key2 stay ordered(so that key1 is smaller than key2)
    public void addKey(E newKey) {
        if (key1 == null) {
            setKey1(newKey);
        } else if (key1.compareTo(newKey) < 0) {
            setKey2(newKey);
        } else {
            setKey2(key1);
            setKey1(newKey);
        }
    }

    //This function removes a key from a node and ensures that the remaining key gets placed in the right field
    public void removeKey(E key) {
        if (key2 != null && key.compareTo(key2) == 0) {
            key2 = null;
        }
        key1 = key2;
        key2 = null;
    }

    //Function that returns whether a node is a leaf or not
    public boolean isLeaf() {
        return childNode1 == null && childNode2 == null && childNode3 == null;
    }

    public Node<E> nextNode(E key) {
        if (hasTwoKeys()) {
            if (key.compareTo(key1) < 0) {
                return childNode1;
            } else if (key.compareTo(key2) > 0) {
                return childNode3;
            }
            return childNode2;
        }
        if (key.compareTo(key1) < 0) {
            return childNode1;
        }
        return childNode2;
    }
}