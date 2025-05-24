import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest; // For random nature

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

// Assuming Main.java and Graph.java are accessible
// Main.randomWalk uses a SecureRandom, making outputs non-deterministic for paths.
// Tests will check for expected termination messages and path structure patterns.
// For more deterministic tests, SecureRandom could be mocked (more advanced).

public class MainRandomWalkTest {

  private Graph g1;
  private Graph g2;
  private Graph g3;
  private Graph g4;

  @BeforeEach
  void setUp() {
    try {
      g1 = new Graph();

      Method getOrCreateId = g1.getClass().getDeclaredMethod("getOrCreateId", String.class);
      getOrCreateId.setAccessible(true); // Make private method accessible
      Field wordToId = g1.getClass().getDeclaredField("wordToId");
      wordToId.setAccessible(true); // Make private field accessible
      Field idToWord = g1.getClass().getDeclaredField("idToWord");
      idToWord.setAccessible(true); // Make private field accessible

      g2 = new Graph();
      g2.addEdge("hello", "world");
      ((Map<String, Integer>) wordToId.get(g2)).clear(); // Reset for new graph
      ((Map<Integer, String>) idToWord.get(g2)).clear(); // Reset for new graph

      g3 = new Graph();
      getOrCreateId.invoke(g3, "lonely"); // Create node "lonely"

      g4 = new Graph();
      g4.addEdge("a", "b");
      g4.addEdge("b", "a"); // Creates a 2-node loop
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      fail("Setup failed: " + e.getMessage());
    } catch (Exception e) {
      fail("Unexpected setup error: " + e.getMessage());
    }
  }

  @Test
  void rw_tc1_emptyGraph() { // Path 1
    String result = Main.randomWalk(g1);
    assertEquals("图为空，无法进行随机游走。", result);
  }

  @Test
  void rw_tc2_inconsistentGraph() { // Path 2
    String result = Main.randomWalk(g2);
    assertTrue(result.matches("图中无节点，无法开始随机游走。"),
        "Expected '图中无节点，无法开始随机游走。', but got: " + result);
  }

  @Test
  void rw_tc3_startNodeHasNoOutEdges() { // Path 3
    // Graph has only one node "lonely", so it must start there.
    String result = Main.randomWalk(g3);
    assertTrue(result.matches("lonely \\(游走停止: 当前节点无出边\\)"),
        "Expected 'lonely (游走停止: 当前节点无出边)', but got: " + result);
  }

  @RepeatedTest(5) // Repeat to account for random start if graph had more nodes
  void rw_tc4_pathEncountersRepeatedEdge() { // Path 4
    // Graph: a <-> b. Start can be 'a' or 'b'.
    // Expected paths: "a b a b (游走停止: 出现重复边)" or "b a b a (游走停止: 出现重复边)"
    String result = Main.randomWalk(g4);
    boolean matched = Pattern.matches("(a b a b|b a b a) \\(游走停止: 出现重复边\\)", result);
    assertTrue(matched, "Output was: " + result);
  }
}