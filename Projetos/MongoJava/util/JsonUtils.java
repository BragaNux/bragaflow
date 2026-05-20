package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonUtils {
    private static final Pattern STRING_FIELD = Pattern.compile("\\\"([^\\\"]+)\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");

    private JsonUtils() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public static Map<String, String> parseObject(String json) {
        Map<String, String> values = new HashMap<>();
        if (json == null) {
            return values;
        }

        Matcher matcher = STRING_FIELD.matcher(json);
        while (matcher.find()) {
            values.put(matcher.group(1), matcher.group(2));
        }
        return values;
    }

    public static List<String> parseStringArray(String json, String key) {
        List<String> values = new ArrayList<>();
        if (json == null || key == null || key.isBlank()) {
            return values;
        }

        Pattern arrayPattern = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher arrayMatcher = arrayPattern.matcher(json);
        if (!arrayMatcher.find()) {
            return values;
        }

        Matcher itemMatcher = Pattern.compile("\\\"([^\\\"]*)\\\"").matcher(arrayMatcher.group(1));
        while (itemMatcher.find()) {
            values.add(itemMatcher.group(1));
        }
        return values;
    }
}