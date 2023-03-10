package oplossing;

import opgave.SearchTree;
import opgave.samplers.Sampler;
import opgave.samplers.ZipfSampler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class TopDownSemiSplayTwoThreeTree<E extends Comparable<E>> extends BottomUpSemiSplayTwoThreeTree<E> {
    private Stack<Node<E>> splayPath = new Stack<>();
    @Override
    public boolean contains(E key) {
        splayPath.clear();
        if (root == null) {
            return false;
        }
        return topDownSearch(root, key);
    }

    public boolean topDownSearch(Node<E> currentNode, E key) {
        if (currentNode.getKey1().compareTo(key) == 0) {
            addToSplayPath(currentNode);
            return true;
        }
        if (currentNode.hasTwoKeys() && currentNode.getKey2().compareTo(key) == 0) {
            addToSplayPath(currentNode);
            return true;
        }
        if (currentNode.isLeaf()) {
            addToSplayPath(currentNode);
            return false;
        } else if (key.compareTo(currentNode.getKey1()) < 0) {
            splayPath.push(currentNode);
            boolean res = currentNode.getChildNode1() != null && topDownSearch(currentNode.getChildNode1(), key);
            if (splayPath.size() == 3) {
                splay(splayPath);
                splayPath.push(currentNode);
            }
            return res;
        } else if (! currentNode.hasTwoKeys() || (currentNode.hasTwoKeys() && key.compareTo(currentNode.getKey2()) < 0)) {
            splayPath.push(currentNode);
            boolean res = currentNode.getChildNode2() != null && topDownSearch(currentNode.getChildNode2(), key);
            if (splayPath.size() == 3) {
                splay(splayPath);
                splayPath.push(currentNode);
            }
            return res;
        } else if (key.compareTo(currentNode.getKey2()) > 0) {
            splayPath.push(currentNode);
            boolean res = currentNode.getChildNode3() != null && topDownSearch(currentNode.getChildNode3(), key);
            if (splayPath.size() == 3) {
                splay(splayPath);
                splayPath.push(currentNode);
            }
            return res;
        }
        splayPath.push(currentNode);
        if (splayPath.size() == 3) {
            splay(splayPath);
        }
        return false;
    }

    public void addToSplayPath(Node<E> node) {
        splayPath.push(node);
        if (splayPath.size() == 3) {
            splay(splayPath);
            splayPath.push(node);
        }
    }

    @Override
    public boolean add(E o){
        splayPath.clear();
        if (root == null) {
            root = new Node<>(o, null, null);
            return true;
        } else if (search(o, root)) {
            contains(o);
            return false;
        }
        return splayAdd(o, root);
    }

    @Override
    public boolean splayAdd(E key, Node<E> currentNode) {
        if (! currentNode.hasTwoKeys() && currentNode.getChildNode1() == null && currentNode.getChildNode2() == null) {
            currentNode.addKey(key);
            contains(key);
        } else {
            if (key.compareTo(currentNode.getKey1()) < 0) {
                if (currentNode.getChildNode1() == null) {
                    currentNode.setChildNode1(new Node<>(key, null, currentNode));
                    contains(key);
                } else {
                    splayAdd(key, currentNode.getChildNode1());
                }
            } else if (currentNode.hasTwoKeys() && key.compareTo(currentNode.getKey2()) > 0) {
                if (currentNode.getChildNode3() == null) {
                    currentNode.setChildNode3(new Node<>(key, null, currentNode));
                    contains(key);
                } else {
                    splayAdd(key, currentNode.getChildNode3());
                }
            } else {
                if (currentNode.getChildNode2() == null) {
                    currentNode.setChildNode2(new Node<>(key, null, currentNode));
                    contains(key);
                } else {
                    splayAdd(key, currentNode.getChildNode2());
                }
            }
        }
        return true;
    }

    @Override
    public boolean splayRemove(E e) {
        Node<E> node = getNode(e);
        if (node == null) {
            contains(e);
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
            contains(node.getKey1());
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        BufferedWriter addNormal = new BufferedWriter(new FileWriter("Project/extra/TopDownBenchAddNormal.csv"));
        BufferedWriter removeNormal = new BufferedWriter(new FileWriter("Project/extra/TopDownBenchRemoveNormal.csv"));
        BufferedWriter addZipf = new BufferedWriter(new FileWriter("Project/extra/TopDownBenchAddZipf.csv"));
        BufferedWriter removeZipf = new BufferedWriter(new FileWriter("Project/extra/TopDownBenchRemoveZipf.csv"));
        BufferedWriter searchNormal = new BufferedWriter(new FileWriter("Project/extra/TopDownBenchSearchNormal.csv"));
        BufferedWriter searchZipf = new BufferedWriter(new FileWriter("Project/extra/TopDownBenchSearchZipf.csv"));
        Random rand = new Random();

        TopDownSemiSplayTwoThreeTree<Integer> tree = new TopDownSemiSplayTwoThreeTree<>();

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
