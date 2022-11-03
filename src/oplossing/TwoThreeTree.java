package oplossing;

import opgave.SearchTree;

import java.util.ArrayList;
import java.util.Iterator;

public class TwoThreeTree<E extends Comparable<E>> implements SearchTree<E> {

    private Node<E> root = null;

    public TwoThreeTree() {

    }

    @Override
    public int size() {
        return isEmpty() ? 0 : recursiveSize(root);
    }

    public int recursiveSize(Node<E> node) {
        if (node.isLeaf()) {
            return node.nodeSize();
        }
        int sizeChild1 = 0;
        int sizeChild2 = 0;
        int sizeChild3 = 0;

        if (node.getChildNode1() != null) {
            sizeChild1 = recursiveSize(node.getChildNode1());
        }
        if (node.getChildNode2() != null) {
            sizeChild2 = recursiveSize(node.getChildNode2());
        }
        if (node.getChildNode3() != null) {
            sizeChild3 = recursiveSize(node.getChildNode3());
        }

        return sizeChild1 + sizeChild2 + sizeChild3;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    public boolean isRoot(Node<E> node) {
        return node.getParentNode() == null;
    }

    @Override
    public boolean contains(E o) {
        if (isEmpty()) {
            return false;
        }
        return search(o, root);
    }

    public boolean search(E o, Node<E> currentNode) {
        if (currentNode.containsKey(o)) {
            return true;
        } else if (currentNode.isLeaf()) {
            return false;
        } else {
            return search(o, currentNode.nextNode(o));
        }
    }

    @Override
    public boolean add(E o) {
        return findLeaf(o, root);
    }

    public boolean findLeaf(E o, Node<E> currentNode) {
        if (root == null) {
            root = new Node<>(o, null, null);
            return true;
        } else if (currentNode.containsKey(o)) {
            return false;
        } else if (currentNode.isLeaf()) {
            insertKey(o, currentNode);
            return true;
        }
        return findLeaf(o, currentNode.nextNode(o));
    }
    
    public void insertKey(E o, Node<E> leaf) {
        if (! leaf.hasTwoKeys()) {
            leaf.addKey(o);
        } else {
            Node<E> subTree;
            Node<E> parent = isRoot(leaf) ? null : leaf.getParentNode();
            if (o.compareTo(leaf.getKey1()) < 0) {
                subTree = new Node<>(leaf.getKey1(), null, parent);
                subTree.setChildNode1(new Node<>(o, null, subTree));
                subTree.setChildNode2(new Node<>(leaf.getKey2(), null, subTree));
            } else if (o.compareTo(leaf.getKey2()) > 0) {
                subTree = new Node<>(leaf.getKey2(), null, parent);
                subTree.setChildNode1(new Node<>(leaf.getKey1(), null, subTree));
                subTree.setChildNode2(new Node<>(o, null, subTree));
            } else {
                subTree = new Node<>(o, null, parent);
                subTree.setChildNode1(new Node<>(leaf.getKey1(), null, subTree));
                subTree.setChildNode2(new Node<>(leaf.getKey2(), null, subTree));
            }
            addBalance(subTree);
        }
    }

    //function balances the tree bases upon the imbalanced subtree
    public void addBalance(Node<E> subTree) {
        if (isRoot(subTree)) {
            root = subTree;
        } else {
            Node<E> parent = subTree.getParentNode();
            if (! subTree.getParentNode().hasTwoKeys()) {
                parent.addKey(subTree.getKey1());
                if (parent.getKey1().compareTo(subTree.getKey1()) > 0) {
                    parent.setChildNode2(subTree.getChildNode1());
                    parent.setChildNode3(parent.getChildNode2());
                    parent.setChildNode2(subTree.getChildNode2());
                    parent.getChildNode1().setParentNode(parent);
                    parent.getChildNode2().setParentNode(parent);
                } else {
                    parent.setChildNode2(subTree.getChildNode1());
                    parent.setChildNode3(subTree.getChildNode2());
                    parent.getChildNode2().setParentNode(parent);
                    parent.getChildNode3().setParentNode(parent);
                }
            } else {
                Node<E> newSTree;
                if (parent.getKey1().compareTo(subTree.getKey1()) > 0) {
                    newSTree = new Node<>(parent.getKey1(), null, isRoot(parent) ? null : parent.getParentNode());
                    newSTree.setChildNode1(subTree);
                    subTree.setParentNode(newSTree);
                    Node<E> rightChild = new Node<>(parent.getKey2(), null, newSTree);
                    rightChild.setChildNode1(parent.getChildNode2());
                    rightChild.getChildNode1().setParentNode(rightChild);
                    rightChild.setChildNode2(parent.getChildNode3());
                    rightChild.getChildNode2().setParentNode(rightChild);
                    newSTree.setChildNode2(rightChild);
                } else if (parent.getKey2().compareTo(subTree.getKey1()) < 0) {
                    newSTree = new Node<>(parent.getKey2(), null, isRoot(parent) ? null : parent.getParentNode());
                    newSTree.setChildNode2(subTree);
                    subTree.setParentNode(newSTree);
                    Node<E> leftChild = new Node<>(parent.getKey1(), null, newSTree);
                    leftChild.setChildNode1(parent.getChildNode1());
                    leftChild.getChildNode1().setParentNode(leftChild);
                    leftChild.setChildNode2(parent.getChildNode2());
                    leftChild.getChildNode2().setParentNode(leftChild);
                    newSTree.setChildNode1(leftChild);
                } else {
                    newSTree = new Node<>(subTree.getKey1(), null, isRoot(parent) ? null : parent.getParentNode());
                    Node<E> leftChild = new Node<>(parent.getKey1(), null, newSTree);
                    newSTree.setChildNode1(leftChild);
                    Node<E> rightChild = new Node<>(parent.getKey2(), null, newSTree);
                    newSTree.setChildNode2(rightChild);
                    leftChild.setChildNode1(parent.getChildNode1());
                    leftChild.getChildNode1().setParentNode(leftChild);
                    leftChild.setChildNode2(subTree.getChildNode1());
                    leftChild.getChildNode2().setParentNode(leftChild);
                    rightChild.setChildNode1(subTree.getChildNode2());
                    rightChild.getChildNode1().setParentNode(rightChild);
                    rightChild.setChildNode2(parent.getChildNode3());
                    rightChild.getChildNode2().setParentNode(rightChild);

                }
                addBalance(newSTree);
            }
        }
    }

    public Node<E> findGreatestLeftChild(Node<E> Start, E key) {
        if (Start.isLeaf()) {
            return Start;
        }
        Node<E> lChild;
        if (Start.hasTwoKeys() && key.compareTo(Start.getKey2()) == 0) {
            lChild = Start.getChildNode2();
        } else {
            lChild = Start.getChildNode1();
        }
        while (! lChild.isLeaf()) {
            lChild = lChild.hasTwoKeys() ? lChild.getChildNode3() : lChild.getChildNode2();
        }
        return lChild;
    }

    //Returns the node that contains a certain key
    public Node<E> getNode(E key) {
        Node<E> currentNode = root;
        while (!currentNode.containsKey(key)) {
            currentNode = currentNode.nextNode(key);
        }
        return currentNode;
    }

    @Override
    public boolean remove(E e) {
        Node<E> rmNode = getNode(e);
        if (rmNode.isLeaf() && rmNode.hasTwoKeys()) {
            rmNode.removeKey(e);
            return true;
        }

        Node <E> grtstLChild = findGreatestLeftChild(rmNode, e);
        if (grtstLChild.hasTwoKeys()) {
            rmNode.removeKey(e);
            rmNode.addKey(grtstLChild.getKey2());
            grtstLChild.removeKey(grtstLChild.getKey2());
            return true;
        }
        rmNode.removeKey(e);
        rmNode.addKey(grtstLChild.getKey1());
        grtstLChild.setKey1(null);
        return rRemove(grtstLChild);
    }

    //TODO: setParent of every moved child node + place all child assignement in ! .isLeaf() conditional
    public boolean rRemove(Node <E> emptyNode) {
        if (isRoot(emptyNode)) {
            root = emptyNode.getChildNode1();
            if (! isEmpty()) {
                root.setParentNode(null);
            }
            return true;
        }
        Node<E> parent = emptyNode.getParentNode();
        if (subTreeSize(emptyNode) == 2) {
            if (parent.getChildNode1().isEmpty()) {
                emptyNode.addKey(parent.getKey1());
                emptyNode.addKey(parent.getChildNode2().getKey1());
                parent.setKey1(null);
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(parent.getChildNode2().getChildNode1());
                    emptyNode.getChildNode2().setParentNode(emptyNode);
                    emptyNode.setChildNode3(parent.getChildNode2().getChildNode2());
                    emptyNode.getChildNode3().setParentNode(emptyNode);
                }
                parent.setChildNode2(null);
            }else {
                parent.getChildNode1().addKey(parent.getKey1());
                parent.setKey1(null);
                if (! emptyNode.isLeaf()) {
                    parent.getChildNode1().setChildNode3(emptyNode.getChildNode1());
                    parent.getChildNode1().getChildNode3().setParentNode(parent.getChildNode1());
                }
                parent.setChildNode2(null);
            }
            return rRemove(parent);
        } else if (subTreeSize(emptyNode) == 3) {
            Node<E> sibling;
            if (parent.getChildNode1().isEmpty()) {
                sibling = parent.getChildNode2();
                emptyNode.addKey(parent.getKey1());
                parent.removeKey(parent.getKey1());
                parent.addKey(sibling.getKey1());
                sibling.removeKey(sibling.getKey1());
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(sibling.getChildNode1());
                    emptyNode.getChildNode2().setParentNode(emptyNode);
                    sibling.setChildNode1(sibling.getChildNode2());
                    sibling.setChildNode2(sibling.getChildNode3());
                    sibling.setChildNode3(null);
                }
            } else {
                sibling = parent.getChildNode1();
                emptyNode.addKey(parent.getKey1());
                parent.removeKey(parent.getKey1());
                parent.addKey(sibling.getKey2());
                sibling.removeKey(sibling.getKey2());
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(sibling.getChildNode3());
                    emptyNode.getChildNode1().setParentNode(emptyNode);
                    sibling.setChildNode3(null);
                }
            }
        } else if (subTreeSize(emptyNode) == 4) {
            if (parent.getChildNode1().isEmpty()){
                emptyNode.addKey(parent.getKey1());
                emptyNode.addKey(parent.getChildNode2().getKey1());
                parent.removeKey(parent.getKey1());
                parent.setChildNode2(parent.getChildNode3());
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(parent.getChildNode2().getChildNode1());
                    emptyNode.getChildNode2().setParentNode(emptyNode);
                    emptyNode.setChildNode3(parent.getChildNode2().getChildNode2());
                    emptyNode.getChildNode3().setParentNode(emptyNode);
                }
                parent.setChildNode3(null);
            } else if (parent.getChildNode2().isEmpty()) {
                parent.getChildNode1().addKey(parent.getKey1());
                parent.removeKey(parent.getKey1());
                parent.setChildNode2(parent.getChildNode3());
                if (! emptyNode.isLeaf()) {
                    parent.getChildNode1().setChildNode3(emptyNode.getChildNode1());
                    parent.getChildNode1().getChildNode3().setParentNode(parent.getChildNode1());
                }
                parent.setChildNode3(null);
            } else {
                parent.getChildNode2().addKey(parent.getKey2());
                parent.removeKey(parent.getKey2());
                if (! emptyNode.isLeaf()) {
                    parent.getChildNode2().setChildNode3(emptyNode.getChildNode1());
                    parent.getChildNode2().getChildNode3().setParentNode(parent.getChildNode2());
                }
                parent.setChildNode3(null);
            }
        } else if (subTreeSize(emptyNode) == 5) {
            Node<E> child1 = parent.getChildNode1();
            Node<E> child2 = parent.getChildNode2();
            Node<E> child3 = parent.getChildNode3();
            if (parent.getChildNode3().hasTwoKeys()) {
                if (child1.isEmpty()) {
                    emptyNode.setKey1(parent.getKey1());
                    parent.setKey1(child2.getKey1());
                    child2.setKey1(parent.getKey2());
                    parent.setKey2(child3.getKey1());
                    child3.removeKey(child3.getKey1());
                    emptyNode.setChildNode2(child2.getChildNode1());
                    child2.setChildNode1(child2.getChildNode2());
                    child2.setChildNode2(child3.getChildNode1());
                    child3.setChildNode1(child3.getChildNode2());
                    child3.setChildNode2(child3.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode2().setParentNode(emptyNode);
                        child2.getChildNode2().setParentNode(child2);
                    }
                    child3.setChildNode3(null);
                } else {
                    emptyNode.setKey1(parent.getKey2());
                    parent.setKey2(child1.getKey1());
                    child3.removeKey(child3.getKey1());
                    emptyNode.setChildNode2(child3.getChildNode1());
                    child3.setChildNode1(child3.getChildNode2());
                    child3.setChildNode2(child3.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode2().setParentNode(emptyNode);
                    }
                    child3.setChildNode3(null);
                }
            } else if (parent.getChildNode2().hasTwoKeys()) {
                if (child1.isEmpty()) {
                    emptyNode.setKey1(parent.getKey1());
                    parent.setKey1(child2.getKey1());
                    child2.removeKey(child2.getKey1());
                    emptyNode.setChildNode2(child2.getChildNode1());
                    child2.setChildNode1(child2.getChildNode2());
                    child2.setChildNode2(child2.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode2().setParentNode(emptyNode);
                    }
                } else {
                    emptyNode.setKey1(parent.getKey2());
                    parent.setKey2(child2.getKey2());
                    child2.removeKey(child2.getKey2());
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(child2.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode1().setParentNode(emptyNode);
                    }
                    child2.setChildNode3(null);
                }
            } else {
                if (child1.hasTwoKeys()) {
                    emptyNode.setKey1(parent.getKey1());
                    parent.setKey1(child1.getKey2());
                    child1.removeKey(child1.getKey2());
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(child1.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode1().setParentNode(emptyNode);
                    }
                    child3.setChildNode3(null);
                } else {
                    emptyNode.setKey2(parent.getKey2());
                    parent.setKey2(child2.getKey1());
                    child2.setKey1(parent.getKey1());
                    parent.setKey1(child1.getKey2());
                    child1.removeKey(child1.getKey2());
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(child2.getChildNode2());
                    child2.setChildNode2(child2.getChildNode1());
                    child2.setChildNode1(child1.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode1().setParentNode(emptyNode);
                        child2.getChildNode1().setParentNode(child2);
                    }
                }
            }
        } else {
            Node<E> child1 = parent.getChildNode1();
            Node<E> child2 = parent.getChildNode2();
            if (child1.isEmpty()) {
                emptyNode.addKey(parent.getKey1());
                parent.setKey1(child2.getKey1());
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(child2.getChildNode1());
                    emptyNode.getChildNode2().setParentNode(emptyNode);
                    child2.setChildNode1(child2.getChildNode2());
                    child2.setChildNode2(child2.getChildNode3());
                    child2.setChildNode3(null);
                }
            } else if (child2.isEmpty()) {
                emptyNode.setKey1(parent.getKey1());
                parent.setKey1(child1.getKey2());
                child1.removeKey(child1.getKey2());
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(child1.getChildNode3());
                    emptyNode.getChildNode1().setParentNode(emptyNode);
                    child1.setChildNode3(null);
                }
            } else {
                emptyNode.setKey1(parent.getKey2());
                parent.setKey2(child2.getKey2());
                child2.removeKey(child2.getKey2());
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(child2.getChildNode3());
                    emptyNode.getChildNode1().setParentNode(emptyNode);
                    child2.setChildNode3(null);
                }
            }
        }
        return true;
    }

    public int subTreeSize(Node<E> node) {
        if(isRoot(node)) {
            return node.nodeSize();
        }
        Node<E> parent = node.getParentNode();
        return parent.nodeSize() +
                (parent.getChildNode1() == null ? 0 : parent.getChildNode1().nodeSize()) +
                (parent.getChildNode2() == null ? 0 : parent.getChildNode2().nodeSize()) +
                (parent.getChildNode3() == null ? 0 : parent.getChildNode3().nodeSize());
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public Iterator<E> iterator() {
        return DFS(root, new ArrayList<E>()).iterator();
    }

    public ArrayList<E> DFS(Node<E> node, ArrayList<E> ls) {
        if (node.isLeaf()) {
            ls.add(node.getKey1());
            if (node.hasTwoKeys()) {
                ls.add(node.getKey2());
            }
        } else {
            DFS(node.getChildNode1(), ls);
            ls.add(node.getKey1());
            DFS(node.getChildNode2(), ls);
            if (node.hasTwoKeys()) {
                ls.add(node.getKey2());
                DFS(node.getChildNode3(), ls);
            }
        }
        return ls;
    }
}