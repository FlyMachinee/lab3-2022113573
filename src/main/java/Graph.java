import java.util.ArrayList; // 动态数组
import java.util.Collections; // 集合工具类
import java.util.HashMap; // 哈希表
import java.util.HashSet; // 哈希集合
import java.util.List; // 列表接口
import java.util.Map; // 映射接口
import java.util.PriorityQueue; // 优先队列
import java.util.Set; // 集合接口

/**
 * 图类，表示一个有向图，节点为单词，边为单词间的相邻关系。.
 * 提供了添加边、获取邻接节点、计算最短路径和PageRank等功能
 * 注意：图中的节点不区分大小写，所有单词均转为小写处理。
 */
public class Graph { // 图类
  // wordToId: 单词到内部ID的映射
  private final Map<String, Integer> wordToId = new HashMap<>();
  // idToWord: 内部ID到单词的映射
  private final Map<Integer, String> idToWord = new HashMap<>();
  // nextNodeId: 用于生成新的节点ID
  private int nextNodeId = 0;

  // successors: 后继邻接表，存储 node_id -> {neighbor_id -> weight} 的映射
  private final List<Map<Integer, Integer>> successors = new ArrayList<>();
  // predecessors: 前驱邻接表，存储 node_id -> {predecessor_id} 的集合 (主要用于PageRank)
  private final List<Set<Integer>> predecessors = new ArrayList<>();

  /**
   * 默认构造函数，初始化一个空图。.
   */
  public Graph() {}

  // 获取或创建单词对应的ID (内部使用，确保节点存在并返回其ID)
  private int getOrCreateId(String word) {
    // 单词在图中不区分大小写，统一转为小写处理
    String lowerCaseWord = word.toLowerCase();
    if (!wordToId.containsKey(lowerCaseWord)) { // 如果单词不存在于映射中
      wordToId.put(lowerCaseWord, nextNodeId); // 添加新单词到映射
      idToWord.put(nextNodeId, lowerCaseWord); // 添加新ID到映射
      successors.add(new HashMap<>()); // 为新节点扩展后继邻接表
      predecessors.add(new HashSet<>()); // 为新节点扩展前驱邻接表
      nextNodeId++; // ID自增
    }
    return wordToId.get(lowerCaseWord); // 返回单词对应的ID
  }

  /**
   * 根据ID获取单词.
   *
   * @param id 节点ID
   * @return 对应的单词
   */
  public String getWordById(int id) {
    return idToWord.get(id);
  }

  /**
   * 根据单词获取ID.
   *
   * @param word 单词
   * @return 对应的ID，如果单词不存在则返回null
   */
  public Integer getIdByWord(String word) {
    return wordToId.get(word.toLowerCase()); // 统一转小写查询
  }

  /**
   * 检查图中是否存在指定单词的节点.
   *
   * @param word 单词
   * @return true 如果存在，false 如果不存在
   */
  public boolean hasNode(String word) {
    return wordToId.containsKey(word.toLowerCase()); // 统一转小写检查
  }

  /**
   * 获取图中所有节点的单词集合.
   *
   * @return 所有节点的单词集合
   */
  public Set<String> getAllNodes() {
    return new HashSet<>(wordToId.keySet()); // 返回wordToId中所有键的副本，避免外部修改
  }

  /**
   * 获取图中节点的数量.
   *
   * @return 节点数量
   */
  public int getNodeCount() {
    return nextNodeId;
  }

  /**
   * 添加一条有向边.
   * 如果边已存在，则权重增加1 (表示相邻出现次数增加)
   *
   * @param fromWord 起始单词
   * @param toWord 终止单词
   */
  public void addEdge(String fromWord, String toWord) {
    int fromId = getOrCreateId(fromWord); // 获取或创建起始节点ID
    int toId = getOrCreateId(toWord); // 获取或创建终止节点ID

    Map<Integer, Integer> fromSuccessors = successors.get(fromId); // 获取起始节点的后继列表
    // 更新边的权重 (相邻出现次数)
    fromSuccessors.put(toId, fromSuccessors.getOrDefault(toId, 0) + 1);

    Set<Integer> toPredecessors = predecessors.get(toId); // 获取终止节点的前驱列表
    toPredecessors.add(fromId); // 添加前驱关系
  }

  /**
   * 获取指定单词的所有后继节点及其对应的边权重.
   *
   * @param word 单词
   * @return 后继节点及其权重的映射
   */
  public Map<String, Integer> getSuccessorsWithWeights(String word) {
    if (!hasNode(word)) {
      return Collections.emptyMap(); // 如果节点不存在，返回空映射
    }
    int wordId = getIdByWord(word); // 获取单词ID
    Map<String, Integer> result = new HashMap<>(); // 存储结果
    // 遍历该节点的后继ID和权重
    for (Map.Entry<Integer, Integer> entry : successors.get(wordId).entrySet()) {
      result.put(idToWord.get(entry.getKey()), entry.getValue()); // 将后继ID转为单词并存入结果
    }
    return result;
  }

  /**
   * 获取指定节点的所有前驱节点ID集合.
   *
   * @param nodeId 节点ID
   * @return 前驱节点ID集合
   */
  public Set<Integer> getPredecessorIds(int nodeId) {
    if (nodeId >= 0 && nodeId < predecessors.size()) { // 检查ID是否有效
      return predecessors.get(nodeId); // 返回前驱ID集合
    }
    return Collections.emptySet(); // 无效ID则返回空集合
  }

  /**
   * 获取指定节点的所有后继节点ID及其对应的边权重.
   *
   * @param nodeId 节点ID
   * @return 后继节点ID及其权重的映射
   */
  public Map<Integer, Integer> getSuccessorIdsWithWeights(int nodeId) {
    if (nodeId >= 0 && nodeId < successors.size()) { // 检查ID是否有效
      return successors.get(nodeId); // 返回后继ID及权重映射
    }
    return Collections.emptyMap(); // 无效ID则返回空映射
  }

  /**
   * 获取两个单词之间的所有桥接词.
   *
   * @param word1 单词1
   * @param word2 单词2
   * @return 桥接词列表
   */
  public List<String> getBridgeWords(String word1, String word2) {
    Integer id1 = getIdByWord(word1.toLowerCase()); // 获取word1的ID (已转小写)
    Integer id2 = getIdByWord(word2.toLowerCase()); // 获取word2的ID (已转小写)
    List<String> bridgeWords = new ArrayList<>(); // 存储桥接词

    if (id1 == null || id2 == null) { // 如果任一单词不在图中，则无桥接词
      return bridgeWords;
    }

    Map<Integer, Integer> successorsOfWord1 = getSuccessorIdsWithWeights(id1); // 获取word1的所有后继节点
    if (successorsOfWord1.isEmpty()) { // 如果word1没有后继节点，则无桥接词
      return bridgeWords;
    }

    // 遍历word1的每一个后继节点 (潜在的桥接词 word3)
    for (int bridgeId : successorsOfWord1.keySet()) {
      // 获取该潜在桥接词的后继节点
      Map<Integer, Integer> successorsOfBridge = getSuccessorIdsWithWeights(bridgeId);
      // 检查是否存在从该潜在桥接词到word2的边
      if (successorsOfBridge.containsKey(id2)) {
        bridgeWords.add(getWordById(bridgeId)); // 如果存在，则其为桥接词
      }
    }
    // 实验手册未明确要求排序，但排序可以使输出更一致
    Collections.sort(bridgeWords);
    return bridgeWords; // 返回找到的所有桥接词
  }

  // 用于Dijkstra算法的内部类，表示节点及其到源点的距离和路径
  private static class NodeDistance implements Comparable<NodeDistance> {
    int id; // 节点ID
    int distance; // 从源点到此节点的距离
    List<Integer> pathNodes; // 从源点到此节点的路径上的节点ID序列

    NodeDistance(int id, int distance, List<Integer> previousPathNodes) {
      this.id = id;
      this.distance = distance;
      this.pathNodes = new ArrayList<>(previousPathNodes); // 复制前路径
      this.pathNodes.add(id); // 将当前节点加入路径
    }

    @Override
    // 用于优先队列排序，距离小的优先
    public int compareTo(NodeDistance other) {
      return Integer.compare(this.distance, other.distance);
    }
  }

  /**
   * 计算两个单词之间的最短路径及其长度.
   *
   * @param word1 单词1
   * @param word2 单词2
   * @return 包含路径和长度的映射
   */
  public Map<String, Object> getShortestPath(String word1, String word2) {
    Integer startId = getIdByWord(word1.toLowerCase()); // 起始节点ID
    Integer endId = getIdByWord(word2.toLowerCase()); // 终止节点ID
    Map<String, Object> result = new HashMap<>(); // 存储结果 (路径和长度)
    result.put("path", new ArrayList<String>()); // 初始化路径为空列表
    result.put("distance", -1); // 初始化距离为-1 (表示未找到或错误)

    if (startId == null || endId == null) { // 如果任一节点不存在
      result.put("error", "一个或两个词不在图中。");
      return result;
    }
    if (startId.equals(endId)) { // 如果起始和终止节点相同
      result.put("path", Collections.singletonList(getWordById(startId))); // 路径只包含该节点
      result.put("distance", 0); // 距离为0
      return result;
    }

    PriorityQueue<NodeDistance> pq = new PriorityQueue<>(); // 优先队列
    Map<Integer, Integer> distances = new HashMap<>(); // 存储从源点到各节点的最短距离估计
    // 初始化所有距离为无穷大
    for (int i = 0; i < nextNodeId; i++) {
      distances.put(i, Integer.MAX_VALUE);
    }

    distances.put(startId, 0); // 源点到自身的距离为0
    pq.add(new NodeDistance(startId, 0, new ArrayList<>())); // 将源点加入优先队列

    while (!pq.isEmpty()) { // 当优先队列不为空
      NodeDistance current = pq.poll(); // 取出距离最小的节点
      int u = current.id; // 当前节点ID

      if (current.distance > distances.get(u)) {
        continue; // 如果是已处理过的更长路径，跳过
      }

      if (u == endId) { // 如果已到达目标节点
        List<String> stringPath = new ArrayList<>(); // 构建字符串表示的路径
        for (int nodeId : current.pathNodes) {
          stringPath.add(getWordById(nodeId));
        }
        result.put("path", stringPath); // 存入路径
        result.put("distance", current.distance); // 存入距离
        return result; // 返回结果
      }

      // 遍历当前节点的所有邻居
      for (Map.Entry<Integer, Integer> neighborEntry : getSuccessorIdsWithWeights(u).entrySet()) {
        int v = neighborEntry.getKey(); // 邻居节点ID
        int weight = neighborEntry.getValue(); // 到邻居的边的权重
        // 如果通过当前节点u到达邻居v的路径更短
        if (distances.get(u) != Integer.MAX_VALUE && distances.get(u) + weight < distances.get(v)) {
          distances.put(v, distances.get(u) + weight); // 更新到v的距离
          // 将v加入优先队列，路径为到达u的路径加上v
          pq.add(new NodeDistance(v, distances.get(v), current.pathNodes));
        }
      }
    }
    result.put("error", "未找到路径。"); // 如果循环结束仍未到达目标节点，则表示无路径
    return result;
  }

  /**
   * 计算图中所有节点的PageRank值.
   *
   * @param d 阻尼系数 (通常为0.85)
   * @param iterations 迭代次数
   * @return 节点的PageRank值映射 (单词 -> PageRank值)
   */
  public Map<String, Double> calculatePageRank(double d, int iterations) {
    int numNodes = getNodeCount(); //图中节点总数
    if (numNodes == 0) {
      return Collections.emptyMap(); //空图则返回空
    }

    Map<Integer, Double> pr = new HashMap<>(); //存储每个节点的PR值
    // 初始化：所有节点的PR值均设为 1/N
    for (int i = 0; i < numNodes; i++) {
      pr.put(i, 1.0 / numNodes);
    }

    Map<Integer, Integer> outDegree = new HashMap<>(); //存储每个节点的出度
    // 计算每个节点的出度 L(v)
    for (int i = 0; i < numNodes; i++) {
      outDegree.put(i, getSuccessorIdsWithWeights(i).size());
    }

    // 进行指定次数的迭代
    for (int iter = 0; iter < iterations; iter++) {
      Map<Integer, Double> newPr = new HashMap<>(); //存储本次迭代计算出的新PR值
      double danglingSum = 0.0; //用于累加悬挂节点(出度为0的节点)的PR值
      // 找出所有悬挂节点，并累加它们的PR值
      for (int i = 0; i < numNodes; i++) {
        if (outDegree.get(i) == 0) {
          danglingSum += pr.get(i);
        }
      }

      // 对图中每个节点j，计算其新的PR值
      for (int j = 0; j < numNodes; j++) {
        double sumFromIncoming = 0.0; //从指向节点j的节点i传播过来的PR值之和
        // 遍历所有指向节点j的节点i (即j的前驱节点)
        // B_u 是所有指向u的节点集合
        for (int i : getPredecessorIds(j)) {
          if (outDegree.get(i) > 0) { // 如果节点i有出边 (不是悬挂节点)
            sumFromIncoming += pr.get(i) / outDegree.get(i); // PR(v_i) / L(v_i)
          }
        }
        // PageRank计算公式
        // PR(u) = (1-d)/N + d * ( sum(PR(v)/L(v)) + sum_dangling(PR(dangling)/N) )
        // 此处将悬挂节点的PR值均分给图中所有其他节点
        double rankForJ = (1.0 - d) / numNodes + d * (sumFromIncoming + danglingSum / numNodes);
        newPr.put(j, rankForJ); //存储节点j的新PR值
      }
      pr = newPr; //用新计算的PR值更新旧值，准备下一次迭代
    }

    Map<String, Double> result = new HashMap<>(); //将结果从ID映射转为单词映射
    for (Map.Entry<Integer, Double> entry : pr.entrySet()) {
      result.put(getWordById(entry.getKey()), entry.getValue());
    }
    return result; //返回最终的PageRank值
  }
}
