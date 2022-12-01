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
        Sampler random=new Sampler(new Random(),50000000);
        long start=System.currentTimeMillis();
        for (Integer el:random.getElements()) {
            assertTrue(tree.add(el));
            assertTrue(tree.contains(el),("should contain "+ el));
        }
        System.out.println("Time: " + (System.currentTimeMillis() - start));
    }

    @Test
    public void removeRandom(){
        SearchTree<Integer>tree = createTree();
        Sampler random = new Sampler(new Random(),1000000);
        for (Integer el : random.getElements()) {
            assertTrue(tree.add(el), String.format("should change when adding %d", el));
        }
        Sampler random2 = new Sampler(new Random(),1000000);
        for (Integer el : random2.getElements()) {
            assertTrue(tree.contains(el), String.format("should contain %d", el));
            assertTrue(tree.remove(el), String.format("should change when removing %d", el));
            assertFalse(tree.contains(el), String.format("should not contain %d anymore", el));
        }
        assertEquals(0, tree.size(), "should be empty");
    }
}
