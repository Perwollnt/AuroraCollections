package gg.auroramc.collections.collection;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MetaChecker {

    private static final Map<String, Function<ItemMeta, String>> parsers = new HashMap<>();

    static {
        parsers.put("customModelData", meta -> meta.hasCustomModelData() ? String.valueOf(meta.getCustomModelData()) : null);
        parsers.put("displayName", meta -> meta.hasDisplayName() ? StringStripper.getPlainTextString(meta.displayName()) : null);
    }

    public static void addMetaChecker(String key, Function<ItemMeta, String> parser) {
        parsers.put(key, parser);
    }

    public static boolean hasMetaChecker(String key) {
        return parsers.containsKey(key);
    }

    public static void removeMetaChecker(String key) {
        parsers.remove(key);
    }

    public static boolean matches(String key, String value, ItemMeta meta) {
        Function<ItemMeta, String> parser = parsers.get(key);

        if (parser != null) {
            String parsedValue = parser.apply(meta);
            return parsedValue != null && parsedValue.equals(value);
        }


        return false;
    }

    private static class StringStripper {
        public static String getPlainTextString(Component text) {
            if (text == null) return null;
            return PlainTextComponentSerializer.plainText().serialize(text);
        }
    }
}
