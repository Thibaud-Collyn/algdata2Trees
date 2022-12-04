package oplossing;

import opgave.SearchTree;
import opgave.samplers.Sampler;
import opgave.samplers.ZipfSampler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TwoThreeTree<E extends Comparable<E>> implements SearchTree<E> {

    protected Node<E> root = null;

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
        if (isEmpty()) {
            return false;
        } else if (currentNode.containsKey(o)) {
            return true;
        } else if (currentNode.isLeaf()) {
            return false;
        } else {
            if (currentNode.nextNode(o) == null) {
                return false;
            } else {
                return search(o, currentNode.nextNode(o));
            }
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
                if (parent.getKey1().compareTo(subTree.getKey1()) > 0) {
                    parent.addKey(subTree.getKey1());
                    parent.setChildNode3(parent.getChildNode2());
                    parent.setChildNode1(subTree.getChildNode1());
                    parent.getChildNode1().setParentNode(parent);
                    parent.setChildNode2(subTree.getChildNode2());
                    parent.getChildNode2().setParentNode(parent);
                } else {
                    parent.addKey(subTree.getKey1());
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
        while (lChild.getRightChild() != null) {
            lChild = lChild.getRightChild();
        }
        return lChild;
    }

    //Returns the node that contains a certain key
    public Node<E> getNode(E key) {
        Node<E> currentNode = root;
        while (!currentNode.containsKey(key)) {
            currentNode = currentNode.nextNode(key);
            if (currentNode == null) {
                return null;
            }
        }
        return currentNode;
    }

    @Override
    public boolean remove(E e) {
        if (!contains(e)) {
            return false;
        }
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
                if (! emptyNode.isLeaf()) {
                    emptyNode.setChildNode2(parent.getChildNode2().getChildNode1());
                    emptyNode.getChildNode2().setParentNode(emptyNode);
                    emptyNode.setChildNode3(parent.getChildNode2().getChildNode2());
                    emptyNode.getChildNode3().setParentNode(emptyNode);
                }
                parent.setChildNode2(parent.getChildNode3());
                parent.setChildNode3(null);
            } else if (parent.getChildNode2().isEmpty()) {
                parent.getChildNode1().addKey(parent.getKey1());
                parent.removeKey(parent.getKey1());
                if (! emptyNode.isLeaf()) {
                    parent.getChildNode1().setChildNode3(emptyNode.getChildNode1());
                    parent.getChildNode1().getChildNode3().setParentNode(parent.getChildNode1());
                }
                parent.setChildNode2(parent.getChildNode3());
                parent.setChildNode3(null);
            } else {
                parent.getChildNode1().addKey(parent.getKey1());
                parent.setKey1(parent.getChildNode2().getKey1());
                parent.getChildNode2().setKey1(parent.getKey2());
                parent.removeKey(parent.getKey2());
                if (! emptyNode.isLeaf()) {
                    parent.getChildNode1().setChildNode3(parent.getChildNode2().getChildNode1());
                    parent.getChildNode1().getChildNode3().setParentNode(parent.getChildNode1());
                    parent.getChildNode2().setChildNode1(parent.getChildNode2().getChildNode2());
                    parent.getChildNode2().setChildNode2(emptyNode.getChildNode1());
                    parent.getChildNode2().getChildNode2().setParentNode(parent.getChildNode2());
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
                    parent.setKey2(child3.getKey1());
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
                    child2.setChildNode3(null);
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
                if (child2.isEmpty()) {
                    emptyNode.setKey1(parent.getKey1());
                    parent.setKey1(child1.getKey2());
                    child1.removeKey(child1.getKey2());
                    emptyNode.setChildNode2(emptyNode.getChildNode1());
                    emptyNode.setChildNode1(child1.getChildNode3());
                    if (! emptyNode.isLeaf()) {
                        emptyNode.getChildNode1().setParentNode(emptyNode);
                    }
                    child1.setChildNode3(null);
                } else {
                    emptyNode.addKey(parent.getKey2());
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
                    child1.setChildNode3(null);
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
                child2.removeKey(child2.getKey1());
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

    public static void main(String[] args) throws IOException {
        BufferedWriter addNormal = new BufferedWriter(new FileWriter("Project/extra/2-3-BenchAddNormal.csv"));
        BufferedWriter removeNormal = new BufferedWriter(new FileWriter("Project/extra/2-3-BenchRemoveNormal.csv"));
        BufferedWriter addZipf = new BufferedWriter(new FileWriter("Project/extra/2-3-BenchAddZipf.csv"));
        BufferedWriter removeZipf = new BufferedWriter(new FileWriter("Project/extra/2-3-BenchRemoveZipf.csv"));
        BufferedWriter searchNormal = new BufferedWriter(new FileWriter("Project/extra/2-3-BenchSearchNormal.csv"));
        BufferedWriter searchZipf = new BufferedWriter(new FileWriter("Project/extra/2-3-BenchSearchZipf.csv"));
        Random rand = new Random();

        TwoThreeTree<Integer> tree = new TwoThreeTree<>();

        int size = 1000;
        int testsize = 100;

        for (int i = 0; i < testsize; i++) {
            addNormal.append(String.valueOf(size + i * 1000)).append(",");
            removeNormal.append(String.valueOf(size + i * 1000)).append(",");
            addZipf.append(String.valueOf(size + i * 1000)).append(",");
            removeZipf.append(String.valueOf(size + i * 1000)).append(",");
            searchNormal.append(String.valueOf(size + i * 1000)).append(",");
            searchZipf.append(String.valueOf(size + i * 1000)).append(",");
        }

        addNormal.append("\n");
        removeNormal.append("\n");
        addZipf.append("\n");
        removeZipf.append("\n");
        searchNormal.append("\n");
        searchZipf.append("\n");

        for (int i = 0; i < testsize; i++) {
            Sampler sampler = new Sampler(rand, size);
            ZipfSampler zipfSampler = new ZipfSampler(rand, size);

            List<Integer> list = sampler.sample(size);
            List<Integer> zipfList = zipfSampler.sample(size);
            long startAddNormal = System.currentTimeMillis();
            for (Integer el : list) {
                tree.add(el);
                tree.contains(el);
            }
            addNormal.append(String.valueOf(System.currentTimeMillis() - startAddNormal)).append(",");

            long startSearchNormal = System.currentTimeMillis();
            for (Integer el : list) {
                tree.contains(el);
            }
            searchNormal.append(String.valueOf(System.currentTimeMillis() - startSearchNormal)).append(",");

            long startRemoveNormal = System.currentTimeMillis();
            for (Integer el : list) {
                tree.remove(el);
                tree.contains(el);
            }
            removeNormal.append(String.valueOf(System.currentTimeMillis() - startRemoveNormal)).append(",");

            tree.clear();
            long startAddZipf = System.currentTimeMillis();
            for (Integer el : zipfList) {
                tree.add(el);
                tree.contains(el);
            }
            addZipf.append(String.valueOf(System.currentTimeMillis() - startAddZipf)).append(",");

            long startSearchZipf = System.currentTimeMillis();
            for (Integer el : zipfList) {
                tree.contains(el);
            }
            searchZipf.append(String.valueOf(System.currentTimeMillis() - startSearchZipf)).append(",");

            long startRemoveZipf = System.currentTimeMillis();
            for (Integer el : zipfList) {
                tree.remove(el);
                tree.contains(el);
            }
            removeZipf.append(String.valueOf(System.currentTimeMillis() - startRemoveZipf)).append(",");


            size += 1000;
        }
        addNormal.close();
        removeNormal.close();
        addZipf.close();
        removeZipf.close();
        searchNormal.close();
        searchZipf.close();
    }
}