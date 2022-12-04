package oplossing;

import opgave.samplers.Sampler;
import opgave.samplers.ZipfSampler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class BottomUpSemiSplayTwoThreeTree<E extends Comparable<E>> extends TwoThreeTree<E>{
    @Override
    public boolean contains(E key) {
        if (root == null) {
            return false;
        }
        splay(getSplayPath(key));
        return search(key, root);
    }

    @Override
    public boolean add(E o){
        if (root == null) {
            root = new Node<>(o, null, null);
            return true;
        } else if (search(o, root)) {
            splay(getSplayPath(o));
            return false;
        }
        return splayAdd(o, root);
    }

    public boolean splayAdd(E key, Node<E> currentNode) {
        if (! currentNode.hasTwoKeys() && currentNode.getChildNode1() == null && currentNode.getChildNode2() == null) {
            currentNode.addKey(key);
            splay(getSplayPath(key));
        } else {
            if (key.compareTo(currentNode.getKey1()) < 0) {
                if (currentNode.getChildNode1() == null) {
                    currentNode.setChildNode1(new Node<>(key, null, currentNode));
                    splay(getSplayPath(key));
                } else {
                    splayAdd(key, currentNode.getChildNode1());
                }
            } else if (currentNode.hasTwoKeys() && key.compareTo(currentNode.getKey2()) > 0) {
                if (currentNode.getChildNode3() == null) {
                    currentNode.setChildNode3(new Node<>(key, null, currentNode));
                    splay(getSplayPath(key));
                } else {
                    splayAdd(key, currentNode.getChildNode3());
                }
            } else {
                if (currentNode.getChildNode2() == null) {
                    currentNode.setChildNode2(new Node<>(key, null, currentNode));
                    splay(getSplayPath(key));
                } else {
                    splayAdd(key, currentNode.getChildNode2());
                }
            }
        }
        return true;
    }

    @Override
    public boolean remove(E e) {
        if (root == null) {
            return false;
        }
        return splayRemove(e);
    }

    public boolean splayRemove(E e) {
        Node<E> node = getNode(e);
        if (node == null) {
            splay(getSplayPath(e));
            return false;
        } else {
            if (node.isLeaf()) {
                if (node.hasTwoKeys()) {
                    node.removeKey(e);
                } else if (! isRoot(node)) {
                    rmNode(node);
                } else {
                    root = null;
                    return true;
                }
            } else {
                if (!node.hasTwoKeys()) {
                    if (node.getChildNode1() != null && node.getChildNode2() == null) {
                        switchNode(node.getChildNode1());
                    } else if (node.getChildNode1() == null && node.getChildNode2() != null) {
                        switchNode(node.getChildNode2());
                    } else {
                        Node<E> grtstLChild = findGreatestLeftChild(node, e);
                        if (grtstLChild.hasTwoKeys()) {
                            node.setKey1(grtstLChild.getKey2());
                            grtstLChild.setKey2(null);
                        } else {
                            node.setKey1(grtstLChild.getKey1());
                            if (grtstLChild.isLeaf()) {
                                rmNode(grtstLChild);
                            } else {
                                switchNode(grtstLChild.getChildNode1());
                            }
                        }
                    }
                } else {
                    if (node.getChildNode1() != null && node.getChildNode2() != null && node.getChildNode3() != null) {
                        Node<E> grtstLChild = findGreatestLeftChild(node, e);
                        if (e.compareTo(node.getKey1()) == 0) {
                            if (grtstLChild.hasTwoKeys()) {
                                node.setKey1(grtstLChild.getKey2());
                                grtstLChild.setKey2(null);
                            } else {
                                node.setKey1(grtstLChild.getKey1());
                                if (grtstLChild.isLeaf()) {
                                    rmNode(grtstLChild);
                                } else {
                                    switchNode(grtstLChild.getChildNode1());
                                }
                            }
                        } else {
                            if (grtstLChild.hasTwoKeys()) {
                                node.setKey2(grtstLChild.getKey2());
                                grtstLChild.setKey2(null);
                            } else {
                                node.setKey2(grtstLChild.getKey1());
                                if (grtstLChild.isLeaf()) {
                                    rmNode(grtstLChild);
                                } else {
                                    switchNode(grtstLChild.getChildNode1());
                                }
                            }
                        }
                    } else if (e.compareTo(node.getKey1()) == 0 && node.getChildNode1() != null && node.getChildNode2() != null) {
                        Node<E> grtstLChild = findGreatestLeftChild(node, e);
                        if (grtstLChild.hasTwoKeys()) {
                            node.setKey1(grtstLChild.getKey2());
                            grtstLChild.setKey2(null);
                        } else {
                            node.setKey1(grtstLChild.getKey1());
                            if (grtstLChild.isLeaf()) {
                                rmNode(grtstLChild);
                            } else {
                                switchNode(grtstLChild.getChildNode1());
                            }
                        }
                    } else if (e.compareTo(node.getKey2()) == 0 && node.getChildNode2() != null && node.getChildNode3() != null) {
                        Node<E> grtstLChild = findGreatestLeftChild(node, e);
                        if (grtstLChild.hasTwoKeys()) {
                            node.setKey2(grtstLChild.getKey2());
                            grtstLChild.setKey2(null);
                        } else {
                            node.setKey2(grtstLChild.getKey1());
                            if (grtstLChild.isLeaf()) {
                                rmNode(grtstLChild);
                            } else {
                                switchNode(grtstLChild.getChildNode1());
                            }
                        }
                    } else if (e.compareTo(node.getKey1()) == 0) {
                        node.removeKey(node.getKey1());
                        if (node.getChildNode2() != null) {
                            node.setChildNode1(node.getChildNode2());
                        }
                        node.setChildNode2(node.getChildNode3());
                        node.setChildNode3(null);
                    } else {
                        node.removeKey(node.getKey2());
                        if (node.getChildNode3() != null) {
                            node.setChildNode2(node.getChildNode3());
                            node.setChildNode3(null);
                        }
                    }
                }
            }
            splay(getSplayPath(node.getKey1()));
            return true;
        }
    }

    public void rmNode(Node<E> node) {
        if (node.getParentNode() != null) {
            if (node.getParentNode().getChildNode1() != null && node.getParentNode().getChildNode1().equals(node)) {
                node.getParentNode().setChildNode1(null);
            } else if (node.getParentNode().getChildNode2() != null && node.getParentNode().getChildNode2().equals(node)) {
                node.getParentNode().setChildNode2(null);
            } else {
                node.getParentNode().setChildNode3(null);
            }
        } else {
            root = null;
        }
    }

    public void switchNode(Node<E> node) {
        Node<E> parent = node.getParentNode();
        if (isRoot(parent)) {
            root = node;
            node.setParentNode(null);
        } else {
            node.setParentNode(parent.getParentNode());
            if (parent.getParentNode().getChildNode1() != null && parent.getParentNode().getChildNode1().equals(parent)) {
                parent.getParentNode().setChildNode1(node);
            } else if (parent.getParentNode().getChildNode2() != null && parent.getParentNode().getChildNode2().equals(parent)) {
                parent.getParentNode().setChildNode2(node);
            } else {
                parent.getParentNode().setChildNode3(node);
            }
        }
    }

    public Stack<Node<E>> getSplayPath(E key) {
        Stack<Node<E>> splayPath = new Stack<>();
        Node<E> currentNode = root;
        boolean found = false;
        while (!found) {
            splayPath.push(currentNode);
            if (currentNode.getKey1().compareTo(key) == 0) {
                found = true;
            } else if (currentNode.hasTwoKeys() && currentNode.getKey2().compareTo(key) == 0) {
                found = true;
            } else if (currentNode.isLeaf()) {
                found = true;
            } else {
                if (currentNode.nextNode(key) == null) {
                    found = true;
                } else {
                    currentNode = currentNode.nextNode(key);
                }
            }
        }
        return splayPath;
    }

    public void splay(Stack<Node<E>> splayPath) {
        Node<E> node = splayPath.pop();
        while (splayPath.size() >= 2) {
            Node<E> parent = splayPath.pop();
            Node<E> grandParent = splayPath.pop();
            Node<E> greatGrandParent = grandParent.getParentNode();
            int caseNumber;
            if (greatGrandParent == null) {
                caseNumber = 0;
            } else if (grandParent.equals(greatGrandParent.getChildNode1())) {
                caseNumber = 1;
            } else if (grandParent.equals(greatGrandParent.getChildNode2())) {
                caseNumber = 2;
            } else {
                caseNumber = 3;
            }
            Node<E> tempTree;

            if (grandParent.hasTwoKeys()) {
                if (parent.equals(grandParent.getChildNode1())) {
                    if (parent.hasTwoKeys()) {
                        if (node.equals(parent.getChildNode1()) || node.equals(parent.getChildNode2())) {
                            tempTree = parent;
                            grandParent.setChildNode1(parent.getChildNode3());
                            if (grandParent.getChildNode1() != null) {
                                grandParent.getChildNode1().setParentNode(grandParent);
                            }
                            tempTree.setChildNode3(grandParent);
                            grandParent.setParentNode(tempTree);
                        } else {
                            tempTree = node;
                            parent.setChildNode3(node.getChildNode1());
                            if (parent.getChildNode3() != null) {
                                parent.getChildNode3().setParentNode(parent);
                            }
                            if (node.hasTwoKeys()) {
                                grandParent.setChildNode1(node.getChildNode3());
                                if (grandParent.getChildNode1() != null) {
                                    grandParent.getChildNode1().setParentNode(grandParent);
                                }
                                tempTree.setChildNode1(parent);
                                tempTree.setChildNode3(grandParent);
                                tempTree.getChildNode1().setParentNode(tempTree);
                                tempTree.getChildNode3().setParentNode(tempTree);
                            } else {
                                grandParent.setChildNode1(node.getChildNode2());
                                if (grandParent.getChildNode1() != null) {
                                    grandParent.getChildNode1().setParentNode(grandParent);
                                }
                                tempTree.setChildNode1(parent);
                                tempTree.setChildNode2(grandParent);
                                tempTree.getChildNode1().setParentNode(tempTree);
                                tempTree.getChildNode2().setParentNode(tempTree);
                            }
                        }
                    } else {
                        if (node.equals(parent.getChildNode1())) {
                            tempTree = parent;
                            grandParent.setChildNode1(parent.getChildNode2());
                            if (grandParent.getChildNode1() != null) {
                                grandParent.getChildNode1().setParentNode(grandParent);
                            }
                            tempTree.setChildNode2(grandParent);
                            tempTree.getChildNode2().setParentNode(tempTree);
                        } else {
                            tempTree = node;
                            parent.setChildNode2(node.getChildNode1());
                            if (parent.getChildNode2() != null) {
                                parent.getChildNode2().setParentNode(parent);
                            }
                            if (node.hasTwoKeys()) {
                                grandParent.setChildNode1(node.getChildNode3());
                                tempTree.setChildNode3(grandParent);
                            } else {
                                grandParent.setChildNode1(node.getChildNode2());
                                tempTree.setChildNode2(grandParent);
                            }
                            grandParent.setParentNode(tempTree);
                            if (grandParent.getChildNode1() != null) {
                                grandParent.getChildNode1().setParentNode(grandParent);
                            }
                            tempTree.setChildNode1(parent);
                            tempTree.getChildNode1().setParentNode(tempTree);
                        }
                    }
                } else if (parent.equals(grandParent.getChildNode2())) {
                    E tempKey;
                    tempTree = grandParent;
                    if (parent.hasTwoKeys()) {
                        if (node.equals(parent.getChildNode1())) {
                            tempKey = grandParent.getKey2();
                            grandParent.setKey2(parent.getKey1());
                            parent.setKey1(parent.getKey2());
                            parent.setKey2(tempKey);
                            Node<E> tempNode = grandParent.getChildNode3();
                            tempTree.setChildNode3(parent);
                            tempTree.setChildNode2(node);
                            node.setParentNode(tempTree);
                            parent.setChildNode1(parent.getChildNode2());
                            parent.setChildNode2(parent.getChildNode3());
                            parent.setChildNode3(tempNode);
                            if (tempNode != null) {
                                tempNode.setParentNode(parent);
                            }
                        } else if (node.equals(parent.getChildNode2())) {
                            tempKey = parent.getKey2();
                            parent.setKey2(parent.getKey1());
                            parent.setKey1(grandParent.getKey1());
                            grandParent.setKey1(node.getKey1());
                            Node<E> tempNode = grandParent.getChildNode1();
                            Node<E> tempNode2 = node.getChildNode1();
                            tempTree.setChildNode1(parent);
                            tempTree.setChildNode2(node);
                            node.setParentNode(tempTree);
                            parent.setChildNode2(parent.getChildNode1());
                            parent.setChildNode1(tempNode);
                            if (tempNode != null) {
                                tempNode.setParentNode(parent);
                            }
                            node.setChildNode1(node.getChildNode2());
                            if (node.hasTwoKeys()) {
                                node.setKey1(node.getKey2());
                                node.setKey2(tempKey);
                                node.setChildNode2(node.getChildNode3());
                                node.setChildNode3(parent.getChildNode3());
                                if (node.getChildNode3() != null) {
                                    node.getChildNode3().setParentNode(node);
                                }
                            } else {
                                node.setKey1(tempKey);
                                node.setChildNode2(parent.getChildNode3());
                            }
                            parent.setChildNode3(tempNode2);
                            if (node.getChildNode2() != null) {
                                node.getChildNode2().setParentNode(node);
                            }
                            if (tempNode2 != null) {
                                tempNode2.setParentNode(parent);
                            }
                        } else {
                            tempKey = grandParent.getKey1();
                            grandParent.setKey1(parent.getKey2());
                            parent.setKey2(parent.getKey1());
                            parent.setKey1(tempKey);
                            Node<E> tempNode = grandParent.getChildNode1();
                            tempTree.setChildNode1(parent);
                            tempTree.setChildNode2(node);
                            node.setParentNode(tempTree);
                            parent.setChildNode3(parent.getChildNode2());
                            parent.setChildNode2(parent.getChildNode1());
                            parent.setChildNode1(tempNode);
                            if (tempNode != null) {
                                tempNode.setParentNode(parent);
                            }
                        }
                    } else {
                        tempKey = parent.getKey1();
                        Node<E> tempNode;
                        if (node.equals(parent.getChildNode1())) {
                            tempNode = grandParent.getChildNode3();
                            parent.setKey1(grandParent.getKey2());
                            grandParent.setKey2(tempKey);
                            tempTree.setChildNode3(parent);
                            tempTree.setChildNode2(node);
                            node.setParentNode(tempTree);
                            parent.setChildNode1(parent.getChildNode2());
                            parent.setChildNode2(tempNode);
                        } else {
                            tempNode = grandParent.getChildNode1();
                            parent.setKey1(grandParent.getKey1());
                            grandParent.setKey1(tempKey);
                            tempTree.setChildNode1(parent);
                            tempTree.setChildNode2(node);
                            node.setParentNode(tempTree);
                            parent.setChildNode2(parent.getChildNode1());
                            parent.setChildNode2(tempNode);
                        }
                        if (tempNode != null) {
                            tempNode.setParentNode(parent);
                        }
                    }
                } else {
                    if (parent.hasTwoKeys()) {
                        if (node.equals(parent.getChildNode2()) || node.equals(parent.getChildNode3())) {
                            tempTree = parent;
                            grandParent.setChildNode3(parent.getChildNode1());
                            if (grandParent.getChildNode3() != null) {
                                grandParent.getChildNode3().setParentNode(grandParent);
                            }
                            tempTree.setChildNode1(grandParent);
                            grandParent.setParentNode(tempTree);
                        } else {
                            tempTree = node;
                            grandParent.setChildNode3(node.getChildNode1());
                            if (grandParent.getChildNode3() != null) {
                                grandParent.getChildNode3().setParentNode(grandParent);
                            }
                            tempTree.setChildNode1(grandParent);
                            grandParent.setParentNode(tempTree);
                            if (node.hasTwoKeys()) {
                                parent.setChildNode1(node.getChildNode3());
                                tempTree.setChildNode3(parent);
                            } else {
                                parent.setChildNode1(node.getChildNode2());
                                tempTree.setChildNode2(parent);
                            }
                            parent.setParentNode(tempTree);
                            if (parent.getChildNode1() != null) {
                                parent.getChildNode1().setParentNode(parent);
                            }
                        }
                    } else {
                        if (node.equals(parent.getChildNode2())) {
                            tempTree = parent;
                            grandParent.setChildNode3(parent.getChildNode1());
                            if (grandParent.getChildNode3() != null) {
                                grandParent.getChildNode3().setParentNode(grandParent);
                            }
                            tempTree.setChildNode1(grandParent);
                            grandParent.setParentNode(tempTree);
                        } else {
                            tempTree = node;
                            grandParent.setChildNode3(node.getChildNode1());
                            if (grandParent.getChildNode3() != null) {
                                grandParent.getChildNode3().setParentNode(grandParent);
                            }
                            tempTree.setChildNode1(grandParent);
                            grandParent.setParentNode(tempTree);
                            if (node.hasTwoKeys()) {
                                parent.setChildNode1(node.getChildNode3());
                                tempTree.setChildNode3(parent);
                            } else {
                                parent.setChildNode1(node.getChildNode2());
                                tempTree.setChildNode2(parent);
                            }
                            parent.setParentNode(tempTree);
                            if (parent.getChildNode1() != null) {
                                parent.getChildNode1().setParentNode(parent);
                            }
                        }
                    }
                }
            } else {
                if (parent.equals(grandParent.getChildNode1()) && (node.equals(parent.getChildNode1()) || (node.equals(parent.getChildNode2()) && parent.hasTwoKeys()))) {
                    tempTree = parent;
                    if (parent.hasTwoKeys()) {
                        grandParent.setChildNode1(parent.getChildNode3());
                        tempTree.setChildNode3(grandParent);
                    } else {
                        grandParent.setChildNode1(parent.getChildNode2());
                        tempTree.setChildNode2(grandParent);
                    }
                    grandParent.setParentNode(tempTree);
                    if (grandParent.getChildNode1() != null) {
                        grandParent.getChildNode1().setParentNode(grandParent);
                    }
                } else if (parent.equals(grandParent.getChildNode1()) && node.equals(parent.getChildNode2()) && !parent.hasTwoKeys()) {
                    tempTree = node;
                    parent.setChildNode2(node.getChildNode1());
                    if (parent.getChildNode2() != null) {
                        parent.getChildNode2().setParentNode(parent);
                    }
                    tempTree.setChildNode1(parent);
                    parent.setParentNode(tempTree);
                    if (node.hasTwoKeys()) {
                        grandParent.setChildNode1(node.getChildNode3());
                        node.setChildNode3(grandParent);
                    } else {
                        grandParent.setChildNode1(node.getChildNode2());
                        node.setChildNode2(grandParent);
                    }
                    grandParent.setParentNode(tempTree);
                    if (grandParent.getChildNode1() != null) {
                        grandParent.getChildNode1().setParentNode(grandParent);
                    }
                } else if (parent.equals(grandParent.getChildNode1()) && parent.getChildNode3() != null && node.equals(parent.getChildNode3())) {
                    tempTree = node;
                    parent.setChildNode3(node.getChildNode1());
                    if (parent.getChildNode3() != null) {
                        parent.getChildNode3().setParentNode(parent);
                    }
                    tempTree.setChildNode1(parent);
                    parent.setParentNode(tempTree);
                    if (node.hasTwoKeys()) {
                        grandParent.setChildNode1(node.getChildNode3());
                        node.setChildNode3(grandParent);
                    } else {
                        grandParent.setChildNode1(node.getChildNode2());
                        node.setChildNode2(grandParent);
                    }
                    grandParent.setParentNode(tempTree);
                    if (grandParent.getChildNode1() != null) {
                        grandParent.getChildNode1().setParentNode(grandParent);
                    }
                } else if (parent.equals(grandParent.getChildNode2()) && node.equals(parent.getChildNode1())) {
                    tempTree = node;
                    grandParent.setChildNode2(node.getChildNode1());
                    if (grandParent.getChildNode2() != null) {
                        grandParent.getChildNode2().setParentNode(grandParent);
                    }
                    tempTree.setChildNode1(grandParent);
                    grandParent.setParentNode(tempTree);
                    if (node.hasTwoKeys()) {
                        parent.setChildNode1(node.getChildNode3());
                        tempTree.setChildNode3(parent);
                    } else {
                        parent.setChildNode1(node.getChildNode2());
                        tempTree.setChildNode2(parent);
                    }
                    if (parent.getChildNode1() != null) {
                        parent.getChildNode1().setParentNode(parent);
                    }
                    parent.setParentNode(tempTree);
                } else {
                    tempTree = parent;
                    grandParent.setChildNode2(parent.getChildNode1());
                    if (grandParent.getChildNode2() != null) {
                        grandParent.getChildNode2().setParentNode(grandParent);
                    }
                    tempTree.setChildNode1(grandParent);
                    grandParent.setParentNode(tempTree);
                }
            }
            if (caseNumber == 0) {
                root = tempTree;
                tempTree.setParentNode(null);
            } else if (caseNumber == 1) {
                greatGrandParent.setChildNode1(tempTree);
            } else if (caseNumber == 2) {
                greatGrandParent.setChildNode2(tempTree);
            } else {
                greatGrandParent.setChildNode3(tempTree);
            }
            tempTree.setParentNode(greatGrandParent);
            node = tempTree;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter addNormal = new BufferedWriter(new FileWriter("Project/extra/BottomUpBenchAddNormal.csv"));
        BufferedWriter removeNormal = new BufferedWriter(new FileWriter("Project/extra/BottomUpBenchRemoveNormal.csv"));
        BufferedWriter addZipf = new BufferedWriter(new FileWriter("Project/extra/BottomUpBenchAddZipf.csv"));
        BufferedWriter removeZipf = new BufferedWriter(new FileWriter("Project/extra/BottomUpBenchRemoveZipf.csv"));
        BufferedWriter searchNormal = new BufferedWriter(new FileWriter("Project/extra/BottomUpBenchSearchNormal.csv"));
        BufferedWriter searchZipf = new BufferedWriter(new FileWriter("Project/extra/BottomUpBenchSearchZipf.csv"));
        Random rand = new Random();

        BottomUpSemiSplayTwoThreeTree<Integer> tree = new BottomUpSemiSplayTwoThreeTree<>();

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
