package net.george.citadel.client.model.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TextureOffset {
    /**
     * The x coordinate offset of the texture
     */
    public final int textureOffsetX;
    /**
     * The y coordinate offset of the texture
     */
    public final int textureOffsetY;

    public TextureOffset(int textureOffsetX, int textureOffsetY) {
        this.textureOffsetX = textureOffsetX;
        this.textureOffsetY = textureOffsetY;
    }
}
