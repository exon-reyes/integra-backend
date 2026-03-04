package integra.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class JsonParseUtil {

    public static List<String> parseJsonArray(String json) {
        if (isEmptyJson(json)) {
            return List.of();
        }

        try {
            String content = cleanJsonArray(json);
            return content.isBlank() ? List.of() : parseContent(content);
        } catch (Exception e) {
            log.error("Error parseando JSON: {}", json, e);
            return List.of();
        }
    }

    private static boolean isEmptyJson(String json) {
        return json == null || json.isBlank() || json.equals("[]") || json.equals("null");
    }

    private static String cleanJsonArray(String json) {
        String content = json.strip();
        if (content.startsWith("[") && content.endsWith("]")) {
            content = content.substring(1, content.length() - 1);
        }
        return content;
    }

    private static List<String> parseContent(String content) {
        return Arrays.stream(content.split(","))
                .map(item -> item.replace("\"", "").trim())
                .filter(s -> !s.isEmpty())
                .toList();
    }
}