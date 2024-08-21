package net.george.citadel.client.texture;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class CitadelTextureManager {
    private static final Map<Identifier, Identifier> COLOR_MAPPED_TEXTURES = new HashMap<>();

    public static Identifier getColorMappedTexture(Identifier textureId, int[] colors) {
        return getColorMappedTexture(textureId, textureId, colors);
    }

    public static Identifier getColorMappedTexture(Identifier namespace, Identifier textureId, int[] colors) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture texture = textureManager.getOrDefault(namespace, MissingSprite.getMissingSpriteTexture());
        if (texture == MissingSprite.getMissingSpriteTexture()) {
            textureManager.registerTexture(namespace, new ColorMappedTexture(textureId, colors));
        }
        return namespace;
    }

    public static VideoFrameTexture getVideoTexture(Identifier namespace, int defaultWidth, int defaultHeight) {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        AbstractTexture texture = textureManager.getOrDefault(namespace, MissingSprite.getMissingSpriteTexture());
        if (texture == MissingSprite.getMissingSpriteTexture()) {
            texture = new VideoFrameTexture(new NativeImage(defaultWidth, defaultHeight, false));
            textureManager.registerTexture(namespace, texture);
        }
        return texture instanceof VideoFrameTexture ? (VideoFrameTexture) texture : null;
    }
}
