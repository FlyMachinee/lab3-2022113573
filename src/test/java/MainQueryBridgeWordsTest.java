import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainQueryBridgeWordsTest {

  private Graph graph;
  // Main class is where queryBridgeWords is.
  // If queryBridgeWords is static, call Main.queryBridgeWords.
  // If Main needs instantiation, create an instance.
  // For this example, assuming queryBridgeWords is a static method in Main.

  @BeforeEach
  void setUp() {
    graph = new Graph();
    graph.addEdge("explore", "to");
    graph.addEdge("to", "seek");
    graph.addEdge("to", "find");
    graph.addEdge("seek", "new");
    graph.addEdge("find", "new");
    graph.addEdge("new", "worlds");
    graph.addEdge("new", "life");
    graph.addEdge("alpha", "beta");
    graph.addEdge("test", "BRIDGE"); // Stored as "bridge"
    graph.addEdge("BRIDGE", "case"); // Stored as "bridge", "case"
  }

  @Test
  void test1_bothWordsUnknown() {
    String result = Main.queryBridgeWords(graph, "unknown1", "unknown2");
    assertEquals("图中不存在 \"unknown1\" 和 \"unknown2\"!", result);
  }

  @Test
  void test2_word1Unknown() {
    String result = Main.queryBridgeWords(graph, "unknown", "explore");
    assertEquals("图中不存在 \"unknown\"!", result);
  }

  @Test
  void test3_word2Unknown() {
    String result = Main.queryBridgeWords(graph, "explore", "unknown");
    assertEquals("图中不存在 \"unknown\"!", result);
  }

  @Test
  void test4_noBridgeWords() {
    String result = Main.queryBridgeWords(graph, "alpha", "beta");
    assertEquals("从 \"alpha\" 到 \"beta\" 不存在桥接词!", result);
  }

  @Test
  void test5_oneBridgeWord() {
    // test -> bridge -> case
    String result = Main.queryBridgeWords(graph, "test", "case");
    assertEquals("从 \"test\" 到 \"case\" 的桥接词是: \"bridge\".", result);
  }

  @Test
  void test6_multipleBridgeWords() {
    // to -> seek -> new
    // to -> find -> new
    // Bridges: find, seek. Sorted: find, seek.
    String result = Main.queryBridgeWords(graph, "to", "new");
    assertEquals("从 \"to\" 到 \"new\" 的桥接词有: \"find\", and \"seek\".", result);
  }
}
