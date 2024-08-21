package net.george.citadel.client.render;

import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BuiltinModelItemRendererRegistry {
    private static final HashMap<Item, BuiltinModelItemRenderer> RENDERERS = new HashMap<>();

    public static BuiltinModelItemRenderer register(@NotNull Item item, @NotNull BuiltinModelItemRenderer renderer) {
        return RENDERERS.putIfAbsent(item, renderer);
    }

    public static BuiltinModelItemRenderer register(@NotNull Item[] items, @NotNull BuiltinModelItemRenderer renderer) {
        for (Item item : items) {
            RENDERERS.putIfAbsent(item, renderer);
        }
        return renderer;
    }

    public static BuiltinModelItemRenderer get(Item item) {
        return RENDERERS.get(item);
    }

    public static boolean contains(Item item) {
        return RENDERERS.containsKey(item);
    }
}
