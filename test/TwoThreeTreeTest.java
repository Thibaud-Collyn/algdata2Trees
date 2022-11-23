import opgave.SearchTree;
import oplossing.TwoThreeTree;

import opgave.samplers.Sampler;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TwoThreeTreeTest implements SearchTreeTest {
    @Override
    public SearchTree<Integer> createTree() {
        return new TwoThreeTree<>();
    }

    @Test
    void addRandom() {
        SearchTree<Integer> tree = createTree();
        Sampler random=new Sampler(new Random(),10000);
        for (Integer el:random.getElements()) {
            assertTrue(tree.add(el));
            assertTrue(tree.contains(el),("should contain "+ el));
        }
    }

    @Test
    void addRandomCase1() {
        SearchTree<Integer> tree = createTree();
        ArrayList<Integer> randomLs = new ArrayList<>(Arrays.asList(82, 49, 8, 78, 26, 3, 25, 91, 70, 33, 59, 58, 92, 4, 79));

        for (Integer el:randomLs) {
            assertTrue(tree.add(el));
            assertTrue(tree.contains(el),("should contain "+ el));
        }
    }

    @Test
    void removeRandom(){
        SearchTree<Integer>tree = createTree();

        Sampler random = new Sampler(new Random(),10000);
        for (Integer el : random.getElements()) {
            assertTrue(tree.add(el), String.format("should change when adding %d", el));
            //System.out.print(el + ", ");
        }

        for (Integer el : random.getElements()) {
            assertTrue(tree.contains(el), String.format("should contain %d", el));
            assertTrue(tree.remove(el), String.format("should change when removing %d", el));
            assertFalse(tree.contains(el), String.format("should not contain %d anymore", el));
        }
        assertEquals(0, tree.size(), "should be empty");
    }

    @Test
    void removeCase1() {
        SearchTree<Integer> tree = createTree();
        ArrayList<Integer> randomLs = new ArrayList<>(Arrays.asList());

        for (Integer el : randomLs) {
            assertTrue(tree.add(el), String.format("should change when adding %d", el));
        }

        for (Integer el : randomLs) {
            assertTrue(tree.contains(el), String.format("should contain %d", el));
            assertTrue(tree.remove(el), String.format("should change when removing %d", el));
            assertFalse(tree.contains(el), String.format("should not contain %d anymore", el));
        }
        assertEquals(0, tree.size(), "should be empty");
    }
}
