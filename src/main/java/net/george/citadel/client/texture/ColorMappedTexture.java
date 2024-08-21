package net.george.citadel.client.texture;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.TextureUtil;
import net.george.citadel.Citadel;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

@SuppressWarnings("unused")
public class ColorMappedTexture extends ResourceTexture {
    private final int[] colors;

    public ColorMappedTexture(Identifier id, int[] colors) {
        super(id);
        this.colors = colors;
    }

    @Override
    public void load(ResourceManager resourceManager) {
        NativeImage nativeImage = getNativeImage(resourceManager, this.location);
        if (nativeImage != null) {
            if (resourceManager.getResource(this.location).isPresent()) {
                Resource resource = resourceManager.getResource(this.location).get();
                try {
                    ColorsMetadataSection section = resource.getMetadata().decode(ColorsMetadataSection.SERIALIZER).orElse(new ColorsMetadataSection(null));
                    NativeImage image = getNativeImage(resourceManager, section.colorRamp());
                    if (image != null) {
                        processColorMap(nativeImage, image);
                    }
                } catch (Exception exception) {
                    Citadel.LOGGER.catching(exception);
                }
            }
            TextureUtil.prepareImage(this.getGlId(), nativeImage.getWidth(), nativeImage.getHeight());
            this.bindTexture();
            nativeImage.upload(0, 0, 0, false);
        }
    }

    private NativeImage getNativeImage(ResourceManager resourceManager, @Nullable Identifier id) {
        Resource resource;
        if (id == null) {
            return null;
        }
        try {
            resource = resourceManager.getResourceOrThrow(id);
            InputStream inputStream = resource.getInputStream();
            NativeImage nativeImage = NativeImage.read(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            return nativeImage;
        } catch (Throwable throwable) {
            return null;
        }
    }

    private void processColorMap(NativeImage nativeImage, NativeImage colorMap) {
        int[] fromColorMap = new int[colorMap.getHeight()];
        for (int i = 0; i < fromColorMap.length; i++) {
            fromColorMap[i] = colorMap.getColor(0, i);
        }
        for (int i = 0; i < nativeImage.getWidth(); i++) {
            for (int j = 0; j < nativeImage.getHeight(); j++) {
                int colorAt = nativeImage.getColor(i, j);
                if (NativeImage.getAlpha(colorAt) == 0) {
                    continue;
                }
                int replaceIndex = -1;
                for (int k = 0; k < fromColorMap.length; k++) {
                    if (colorAt == fromColorMap[k]) {
                        replaceIndex = k;
                    }
                }
                if (replaceIndex >= 0 && this.colors.length > replaceIndex) {
                    int r = this.colors[replaceIndex] >> 16 & 255;
                    int g = this.colors[replaceIndex] >> 8 & 255;
                    int b = this.colors[replaceIndex] & 255;
                    nativeImage.setColor(i, j, NativeImage.packColor(NativeImage.getAlpha(colorAt), b, g, r));
                }
            }
        }
    }

    private record ColorsMetadataSection(Identifier colorRamp) {
            public static final ColorsMetadataSectionSerializer SERIALIZER = new ColorsMetadataSectionSerializer();

        private boolean areColorsEqual(int color1, int color2) {
                int r1 = color1 >> 16 & 255;
                int g1 = color1 >> 8 & 255;
                int b1 = color1 & 255;
                int r2 = color2 >> 16 & 255;
                int g2 = color2 >> 8 & 255;
                int b2 = color2 & 255;
                return r1 == r2 && g1 == g2 && b1 == b2;
        }
    }

    private static class ColorsMetadataSectionSerializer implements ResourceMetadataReader<ColorsMetadataSection> {
        private ColorsMetadataSectionSerializer() {
        }

        @Override
        public ColorsMetadataSection fromJson(JsonObject json) {
            return new ColorsMetadataSection(new Identifier(JsonHelper.getString(json, "color_ramp")));
        }

        @Override
        public String getKey() {
            return "colors";
        }
    }
}
