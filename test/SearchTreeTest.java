import opgave.SearchTree;
import opgave.samplers.Sampler;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public interface SearchTreeTest {

    SearchTree<Integer> createTree();

    @Test
    default void empty() {
        SearchTree<Integer> tree = createTree();
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    default void addOne() {
        SearchTree<Integer>tree = createTree();

        assertFalse(tree.contains(1));
        tree.add(1);
        assertTrue(tree.contains(1));
        assertEquals(1, tree.size());
    }

    @Test
    default void removeOne() {
        SearchTree<Integer>tree = createTree();

        assertFalse(tree.contains(1));
        tree.add(1);
        assertTrue(tree.contains(1));
        tree.remove(1);
        assertFalse(tree.contains(1));
        assertEquals(0, tree.size());
        assertTrue(tree.isEmpty());
    }

    @Test
    default void addMultiple() {
        SearchTree<Integer> tree = createTree();

        for (int i = 0; i < 20; i++) {
            assertTrue(tree.add(i));
        }
        for (int i = 0; i < 20; i++) {
            assertTrue(tree.contains(i), String.format("should contain %d", i));
        }
    }

    @Test
    default void removeMultiple() {
        SearchTree<Integer>tree = createTree();

        for (int i = 0; i < 20; i++) {
            assertTrue(tree.add(i), String.format("should change when adding %d", i));
        }
        for (int i = 0; i < 20; i++) {
            assertTrue(tree.contains(i), String.format("should contain %d", i));
            assertTrue(tree.remove(i), String.format("should change when removing %d", i));
            assertFalse(tree.contains(i), String.format("should not contain %d anymore", i));
        }
        assertEquals(0, tree.size(), "should be empty");
    }

    @Test
    default void iterator() {
        SearchTree<Integer> tree = createTree();
        List<Integer> expected = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.add(i), String.format("should change when adding %d", i));
            expected.add(i);
        }
        assertIterableEquals(expected, tree);
    }
}
