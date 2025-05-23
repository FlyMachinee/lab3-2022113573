import java.util.ArrayList; // 引入动态数组类
import java.util.List; // 引入列表接口

public class TextProcessor { // 文本处理器类

    /**
     * 清洗输入的文本并将其切分为单词列表。
     * - 将文本转换为小写。 [cite: 7]
     * - 将换行符和回车符替换为空格。 [cite: 5]
     * - 将任何非字母字符（包括标点符号）替换为空格，
     * 这实际上是将标点符号视为空格处理，并忽略其他非字母字符。 [cite: 5]
     * - 将多个连续空格规范化为单个空格。
     * - 根据空格切分处理后的文本为单词。
     *
     * @param text 原始输入字符串。
     * @return 清洗和切分后的单词列表。
     */
    public static List<String> cleanAndTokenize(String text) {
        if (text == null || text.isEmpty()) { // 如果文本为空或null
            return new ArrayList<>(); // 返回空列表
        }

        // 转换为小写 (图中的节点不区分大小写) [cite: 7]
        String processedText = text.toLowerCase();

        // 将换行符/回车符替换为空格 [cite: 5]
        processedText = processedText.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");

        // 将非字母字符替换为空格
        StringBuilder sb = new StringBuilder(); // 用于构建处理后的字符串
        for (char c : processedText.toCharArray()) { // 遍历文本中的每个字符
            if (Character.isLetter(c)) { // 如果是字母
                sb.append(c); // 直接添加
            } else { // 如果不是字母 (包括标点、数字、空格等)
                sb.append(' '); // 替换为空格 [cite: 5]
            }
        }
        processedText = sb.toString(); // 获取处理后的字符串

        // 根据一个或多个空格切分字符串，并移除结果中的空字符串
        String[] wordsArray = processedText.trim().split("\\s+"); // trim去除首尾空格，split按空格切分

        List<String> wordsList = new ArrayList<>(); // 存储最终的单词列表
        for (String word : wordsArray) { // 遍历切分后的单词数组
            if (!word.isEmpty()) { // 确保不添加因多个连续空格产生的空字符串
                wordsList.add(word);
            }
        }
        return wordsList; // 返回单词列表
    }
}
