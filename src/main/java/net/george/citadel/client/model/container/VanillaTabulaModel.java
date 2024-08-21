package net.george.citadel.client.model.container;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * @author pau101
 * @since 1.0.0
 */
@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class VanillaTabulaModel implements UnbakedModel {
    private final TabulaModelContainer model;
    private final SpriteIdentifier particle;
    private final Collection<SpriteIdentifier> textures;
    private final ImmutableMap<ModelTransformation.Mode, AffineTransformation> transforms;

    public VanillaTabulaModel(TabulaModelContainer model, SpriteIdentifier particle, ImmutableList<SpriteIdentifier> textures, ImmutableMap<ModelTransformation.Mode, AffineTransformation> transforms) {
        this.model = model;
        this.particle = particle;
        this.textures = textures;
        this.transforms = transforms;
    }

    public Collection<Identifier> getModelDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
        return this.textures;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
        return null;
    }
}
