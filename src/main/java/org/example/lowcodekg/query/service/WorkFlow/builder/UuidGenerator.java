package org.example.lowcodekg.query.service.WorkFlow.builder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UUID和标识符生成器
 */
public class UuidGenerator {

    /**
     * 创建数据类型
     */
    public static Map<String, Object> createDataType(String dataType) {
        Map<String, Object> type = new HashMap<>();
        type.put("type", dataType);
        type.put("uuid", UUID.randomUUID().toString().replace("-", ""));
        return type;
    }

    /**
     * 生成UUID
     */
    public static String generateUuid(String kind) {
        String randomPart = UUID.randomUUID().toString().replace("-", "");
        return "u_" + kind + "_U_" + randomPart + "_u";
    }

    private static int identifierCounter = 1;
    
    /**
     * 生成标识符
     */
    public static String generateIdentifier(String name) {
        // 将中文转换为英文标识符
        String englishName = convertToEnglishIdentifier(name);
        // 使用递增计数器确保唯一性
        return englishName + "_" + (identifierCounter++);
    }
    private static String convertToEnglishIdentifier(String name) {
        // 检查是否包含中文字符
        if (containsChinese(name)) {
            return "param";
        }
        // 如果不是中文，直接返回原名称
        return name;
    }
    
    /**
     * 检查字符串是否包含中文字符
     */
    private static boolean containsChinese(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            // 中文字符的Unicode范围
            if (c >= 0x4E00 && c <= 0x9FFF) {
                return true;
            }
        }
        return false;
    }
}