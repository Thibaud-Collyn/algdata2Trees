import opgave.SearchTree;
import opgave.samplers.Sampler;
import oplossing.BottomUpSemiSplayTwoThreeTree;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class BottomUpSemiSplayTwoThreeTreeTest implements SearchTreeTest {
    @Override
    public SearchTree<Integer> createTree() {
        return new BottomUpSemiSplayTwoThreeTree<>();
    }

    @Test
    void testSplayContains() {
        SearchTree<Integer> tree = createTree();
        for (int i = 1; i < 8; i++) {
            assertTrue(tree.add(i));
        }
        assertFalse(tree.contains(0));
    }

    @Test
    void rmOne() {
        SearchTree<Integer> tree = createTree();
        tree.add(1);
        assertTrue(tree.remove(1));
        assertTrue(tree.isEmpty());
    }

    @Test
    void removeTen() {
        SearchTree<Integer>tree = createTree();

        for (int i = 0; i < 10; i++) {
            assertTrue(tree.add(i), String.format("should change when adding %d", i));
        }
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.remove(i), String.format("should change when removing %d", i));
        }
        assertEquals(0, tree.size(), "should be empty");
    }

    @Test
    void addRandom() {
        SearchTree<Integer> tree = createTree();
        Sampler random=new Sampler(new Random(),1000000);

        System.out.println();
        long start=System.currentTimeMillis();
        for (Integer el:random.getElements()) {
            assertTrue(tree.add(el));
        }
        System.out.println("Time to add 1.000.000 : " + (System.currentTimeMillis() - start));
        for (Integer el:random.getElements()) {
            assertTrue(tree.contains(el),("should contain "+ el));
        }
    }

    @Test
    void removeRandom() {
        SearchTree<Integer> tree = createTree();
        Sampler random=new Sampler(new Random(),100000);
        for (Integer el:random.getElements()) {
            System.out.print(el+", ");
        }

        System.out.println();
        //long start=System.currentTimeMillis();
        for (Integer el:random.getElements()) {
            assertTrue(tree.add(el));
        }
        //System.out.println("Time to add 1.000.000 : " + (System.currentTimeMillis() - start));
        for (Integer el:random.getElements()) {
            assertTrue(tree.remove(el),("should have deleted "+ el));
        }
        assertTrue(tree.isEmpty(),("Tree should be empty."));
    }

    @Test
    void randAddMultiple() {
        for (int i = 0; i < 20; i++) {
            addRandom();
        }
    }

    @Test
    void addRandCase() {
        SearchTree<Integer> tree = createTree();
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(19, 28, 20, 24, 1, 0, 7, 16, 9, 6, 3, 22, 11, 23, 4, 2, 13, 12, 18, 17, 21, 27, 10, 29, 25, 8, 14, 26, 5, 15));
        //long start=System.currentTimeMillis();
        for (Integer el:list) {
            assertTrue(tree.add(el));
        }
        for (Integer el:list) {
            assertTrue(tree.contains(el),("should contain "+ el));
        }
        //System.out.println("Time: " + (System.currentTimeMillis() - start));
    }

    @Test
    void removeRandCase() {
        SearchTree<Integer> tree = createTree();
        ArrayList<Integer> list = new ArrayList<>(Arrays.asList(4, 8, 1, 2, 7, 3, 5, 6, 9, 0));
        //long start=System.currentTimeMillis();
        for (Integer el:list) {
            assertTrue(tree.add(el));
        }
        for (Integer el:list) {
            assertTrue(tree.remove(el),("should contain "+ el));
        }
        assertTrue(tree.isEmpty(),("Tree should be empty."));
        //System.out.println("Time: " + (System.currentTimeMillis() - start));
    }
}
