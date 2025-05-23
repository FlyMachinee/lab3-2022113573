import java.io.IOException; // IO异常
import java.nio.file.Files; // 文件操作
import java.nio.file.Paths; // 路径操作
import java.util.*; // Java工具类

public class Main { // 主类

    private static final Graph graph = new Graph(); // 图的实例

    public static void main(String[] args) { // 主函数入口
        Scanner scanner = new Scanner(System.in); // 用于接收用户输入

        System.out.println("软件工程实验一 - 图处理器"); // 欢迎信息
        System.out.print("请输入文本文件的路径: "); // 提示用户输入文件路径
        String filePath = scanner.nextLine(); // 读取文件路径

        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath))); // 读取文件所有字节并转为字符串
            List<String> words = TextProcessor.cleanAndTokenize(content); // 清洗并切分文本为单词列表 [cite: 5]

            if (words.size() < 2) { // 如果单词数量不足以构成边
                System.out.println("文件中单词数量不足以构成图的边。");
                scanner.close(); // 关闭scanner
                return; // 程序退出
            }

            // 根据相邻单词生成图的边
            for (int i = 0; i < words.size() - 1; i++) {
                graph.addEdge(words.get(i), words.get(i + 1)); // 添加边 [cite: 7]
            }
            System.out.println("已成功根据输入文件生成图。");

        } catch (IOException e) { //捕获IO异常
            System.err.println("读取或处理文件时出错: " + e.getMessage());
            scanner.close(); // 关闭scanner
            return; // 程序退出
        }

        // 交互式菜单
        while (true) {
            System.out.println("\n可选操作:");
            System.out.println("1. 显示有向图"); // [cite: 9]
            System.out.println("2. 查询桥接词"); // [cite: 10]
            System.out.println("3. 根据桥接词生成新文本"); // [cite: 15]
            System.out.println("4. 计算两个单词之间的最短路径"); // [cite: 16]
            System.out.println("5. 计算PageRank (阻尼系数d=0.85)"); // [cite: 17]
            System.out.println("6. 随机游走"); // [cite: 18]
            System.out.println("7. 退出");
            System.out.print("请输入您的选择 (1-7): ");

            int choice; // 用户选择
            try {
                choice = Integer.parseInt(scanner.nextLine()); // 读取用户输入并转为整数
            } catch (NumberFormatException e) { //捕获数字格式异常
                System.out.println("输入无效。请输入1到7之间的数字。");
                continue; // 继续下一次循环
            }

            switch (choice) { // 根据用户选择执行不同操作
                case 1:
                    showDirectedGraph(graph); // 调用显示有向图函数 [cite: 9]
                    break;
                case 2:
                    System.out.print("请输入第一个词 (word1): ");
                    String bwWord1 = scanner.nextLine(); // 读取第一个词
                    System.out.print("请输入第二个词 (word2): ");
                    String bwWord2 = scanner.nextLine(); // 读取第二个词
                    System.out.println(queryBridgeWords(graph, bwWord1, bwWord2)); // 调用查询桥接词函数并输出结果 [cite: 10]
                    break;
                case 3:
                    System.out.print("请输入一行新文本: ");
                    String inputText = scanner.nextLine(); // 读取新文本
                    System.out.println("生成的新文本为: " + generateNewText(graph, inputText)); // 调用生成新文本函数并输出结果 [cite: 15]
                    break;
                case 4:
                    System.out.print("请输入最短路径的起始词: ");
                    String spWord1 = scanner.nextLine(); // 读取起始词
                    System.out.print("请输入最短路径的结束词: ");
                    String spWord2 = scanner.nextLine(); // 如果为空则设为null
                    System.out.println(calcShortestPath(graph, spWord1, spWord2)); // 调用计算最短路径函数并输出结果 [cite: 16]
                    break;
                case 5:
                    Map<String, Double> pageRanks = getAllPageRanks(graph); // 获取所有PageRank值 (d=0.85) [cite: 17]
                    if (pageRanks.isEmpty()) {
                        System.out.println("图为空或无法计算PageRank。");
                    } else {
                        System.out.println("PageRank值 (d=0.85, 按PR值降序排列):");
                        pageRanks.entrySet().stream()
                                .sorted(Map.Entry.<String, Double>comparingByValue().reversed()) // 按PR值降序排序
                                .forEach(entry -> System.out.printf("- %s: %.5f\n", entry.getKey(), entry.getValue())); // 格式化输出
                    }
                    break;
                case 6:
                    String randomWalkPath = randomWalk(graph); // 执行随机游走 [cite: 18]
                    System.out.println("随机游走路径: " + randomWalkPath);
                    try { // 将结果输出到文件 [cite: 18]
                        Files.write(Paths.get("random_walk_output.txt"), randomWalkPath.getBytes()); // 写入文件
                        System.out.println("随机游走路径已保存到 random_walk_output.txt 文件。");
                    } catch (IOException e) { // 捕获IO异常
                        System.err.println("写入随机游走路径到文件时出错: " + e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("程序退出。");
                    scanner.close(); // 关闭scanner
                    return; // 退出程序
                default:
                    System.out.println("选择无效，请重试。");
            }
        }
    }

    // 函数：显示有向图 [cite: 9]
    // 参数 G 的类型 type 由开发者自行定义，此处使用我们定义的 Graph 类 [cite: 20]
    public static void showDirectedGraph(Graph G) {
        System.out.println("\n--- 有向图结构 ---");
        if (G == null || G.getNodeCount() == 0) {
            System.out.println("图当前为空。");
            return;
        }
        List<String> sortedNodes = new ArrayList<>(G.getAllNodes()); // 获取所有节点
        Collections.sort(sortedNodes); // 对节点排序以便于一致性显示

        for (String node : sortedNodes) { // 遍历每个节点
            Map<String, Integer> successors = G.getSuccessorsWithWeights(node); // 获取其后继节点及权重
            if (successors.isEmpty()) { // 如果没有出边
                System.out.println(node + " -> (无出边)");
            } else {
                StringJoiner sj = new StringJoiner(", ", node + " -> {", "}"); // 用于拼接字符串
                List<String> sortedSuccessors = new ArrayList<>(successors.keySet()); // 获取所有后继节点
                Collections.sort(sortedSuccessors); // 对后继节点排序
                for (String successor : sortedSuccessors) { // 遍历每个后继节点
                    sj.add(successor + "(权重:" + successors.get(successor) + ")"); // 添加到字符串
                }
                System.out.println(sj); // 输出当前节点的表示
            }
        }
        System.out.println("--- 图结构结束 ---");
    }

    // 函数：查询桥接词 [cite: 10]
    public static String queryBridgeWords(Graph G, String word1, String word2) {
        String w1Lower = word1.toLowerCase(); // 转小写，因为图中节点不区分大小写 [cite: 7]
        String w2Lower = word2.toLowerCase(); // 转小写
        boolean w1Exists = G.hasNode(w1Lower); // 检查word1是否存在
        boolean w2Exists = G.hasNode(w2Lower); // 检查word2是否存在

        // 根据实验手册中的示例输出格式处理 [cite: 10, 13]
        if (!w1Exists && !w2Exists) return "图中不存在 \"" + word1 + "\" 和 \"" + word2 + "\"!";
        if (!w1Exists) return "图中不存在 \"" + word1 + "\"!";
        if (!w2Exists) return "图中不存在 \"" + word2 + "\"!";
        // 上述特定检查匹配了 "No "exciting" in the graph!" 等情况。
        // 若严格按 "No word1 or word2 in the graph!"，则:
        // if (!w1Exists || !w2Exists) return "No " + word1 + " or " + word2 + " in the graph!";

        List<String> bridges = G.getBridgeWords(w1Lower, w2Lower); // 获取桥接词列表
        if (bridges.isEmpty()) return "从 \"" + word1 + "\" 到 \"" + word2 + "\" 不存在桥接词!"; // [cite: 10]

        if (bridges.size() == 1) { // 如果只有一个桥接词 [cite: 11]
            return "从 \"" + word1 + "\" 到 \"" + word2 + "\" 的桥接词是: \"" + bridges.get(0) + "\".";
        } else { // 如果有多个桥接词 [cite: 11]
            StringBuilder result = new StringBuilder("从 \"" + word1 + "\" 到 \"" + word2 + "\" 的桥接词有: ");
            for (int i = 0; i < bridges.size(); i++) {
                result.append("\"").append(bridges.get(i)).append("\""); // 添加带引号的桥接词
                if (i < bridges.size() - 2) result.append(", "); // 如果后面还有至少两个词，用逗号分隔
                else if (i == bridges.size() - 2) result.append(", and "); // 如果是倒数第二个词，用 ", and " 连接
            }
            result.append("."); // 句末标点
            return result.toString();
        }
    }

    // 函数：根据bridge word生成新文本 [cite: 15]
    public static String generateNewText(Graph G, String inputText) {
        List<String> words = TextProcessor.cleanAndTokenize(inputText); // 清洗和切分输入文本
        if (words.size() < 2) return inputText; // 如果单词少于2个，无法插入桥接词

        StringBuilder newText = new StringBuilder(); // 用于构建新文本
        Random random = new Random(); // 用于随机选择桥接词

        for (int i = 0; i < words.size(); i++) { // 遍历输入文本的单词
            newText.append(words.get(i)); // 添加当前单词
            if (i < words.size() - 1) { // 如果不是最后一个单词
                // 查询相邻两个单词间的桥接词 (忽略大小写)
                List<String> bridges = G.getBridgeWords(words.get(i).toLowerCase(), words.get(i + 1).toLowerCase());
                if (!bridges.isEmpty()) { // 如果存在桥接词
                    // 随机选择一个桥接词插入 [cite: 15]
                    newText.append(" ").append(bridges.get(random.nextInt(bridges.size())));
                }
                newText.append(" "); // 在原单词后（或桥接词后）加空格，准备下一个原单词
            }
        }
        return newText.toString().trim(); // 去除末尾可能的多余空格
    }

    // 函数：计算两个单词之间的最短路径 [cite: 16]
    public static String calcShortestPath(Graph G, String word1, String word2) {
        String w1Lower = word1.toLowerCase(); // 统一转小写
        String w2Lower = word2.toLowerCase(); // 结束词可能为null

        // 检查起始词是否存在，如果指定了结束词，也检查结束词是否存在
        if (!G.hasNode(w1Lower) || !G.hasNode(w2Lower)) {
            return "一个或两个词不在图中。"; // 简化提示
        }

        Map<String, Object> pathResult = G.getShortestPath(w1Lower, w2Lower);
        // 检查路径是否存在或是否有错误
        if (pathResult.containsKey("error") || ((List<String>)pathResult.get("path")).isEmpty()) {
            return "未找到从 \"" + word1 + "\" 到 \"" + word2 + "\" 的路径 (可能不可达)。"; // [cite: 16]
        }
        List<String> path = (List<String>) pathResult.get("path"); // 获取路径列表
        int distance = (int) pathResult.get("distance"); // 获取路径长度
        // 实验手册要求突出显示路径，此处CLI仅文本输出 [cite: 16]
        return "最短路径: " + String.join(" -> ", path) + "\n路径长度: " + distance;
    }

    // 函数：计算PageRank (对应实验手册中的 calPageRank) [cite: 17]
    // PageRank通常为全图计算，此处返回所有节点的PR值
    // 实验手册指定阻尼系数 d = 0.85 [cite: 19]
    public static Map<String, Double> getAllPageRanks(Graph G) {
        // 迭代次数可设为合理默认值，例如100
        return G.calculatePageRank(0.85, 100);
    }

    // 函数：随机游走 [cite: 18]
    public static String randomWalk(Graph G) {
        if (G.getNodeCount() == 0) return "图为空，无法进行随机游走。";

        List<String> allNodesList = new ArrayList<>(G.getAllNodes()); // 获取图中所有节点
        if (allNodesList.isEmpty()) return "图中无节点，无法开始随机游走。";

        Random random = new Random(); // 用于随机选择
        String currentNode = allNodesList.get(random.nextInt(allNodesList.size())); // 随机选择起始节点 [cite: 18]
        StringBuilder pathBuilder = new StringBuilder(currentNode); // 用于构建游走路径字符串
        Set<String> visitedEdges = new HashSet<>(); // 用于记录访问过的边，格式为 "from->to"，以检测重复边 [cite: 18]

        while (true) { // 开始游走
            Map<String, Integer> successors = G.getSuccessorsWithWeights(currentNode); // 获取当前节点的出边
            if (successors.isEmpty()) { // 如果当前节点没有出边 [cite: 18]
                pathBuilder.append(" (游走停止: 当前节点无出边)");
                break; // 停止游走
            }
            // 从出边中随机选择一条
            List<String> successorKeys = new ArrayList<>(successors.keySet());
            String nextNode = successorKeys.get(random.nextInt(successorKeys.size()));
            String edgeKey = currentNode + "->" + nextNode; // 当前边

            if (visitedEdges.contains(edgeKey)) { // 如果该边已被访问过 [cite: 18]
                pathBuilder.append(" ").append(nextNode); // 添加导致重复的节点
                pathBuilder.append(" (游走停止: 出现重复边)");
                break; // 停止游走
            }
            visitedEdges.add(edgeKey); // 记录当前边
            pathBuilder.append(" ").append(nextNode); // 将下一节点加入路径
            currentNode = nextNode; // 移动到下一节点
            // 实验手册中提到的用户随时停止遍历，在命令行版本中未实现 [cite: 18]
        }
        return pathBuilder.toString(); // 返回游走路径
    }
}
