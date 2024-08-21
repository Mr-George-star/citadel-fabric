package net.george.citadel.client.model.container;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.json.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class TabulaModelBlock {
    private static final Logger LOGGER = LogManager.getLogger();
    @VisibleForTesting
    static final Gson SERIALIZER = (new GsonBuilder())
            .registerTypeAdapter(TabulaModelBlock.class, new Deserializer())
            .registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
            .registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
            .registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
            .registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
            .registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
            .registerTypeAdapter(ModelOverride.class, new ModelOverride.Deserializer()).create();
    private final List<ModelElement> elements;
    private final boolean gui3d;
    public final boolean ambientOcclusion;
    private final ModelTransformation cameraTransforms;
    private final List<ModelOverride> overrides;
    public String name = "";
    @VisibleForTesting
    public final Map<String, String> textures;
    @VisibleForTesting
    public TabulaModelBlock parent;
    @VisibleForTesting
    protected Identifier parentLocation;

    public static TabulaModelBlock deserialize(Reader reader) {
        return JsonHelper.deserialize(SERIALIZER, reader, TabulaModelBlock.class, false);
    }

    public static TabulaModelBlock deserialize(String jsonString) {
        return deserialize(new StringReader(jsonString));
    }

    public TabulaModelBlock(@Nullable Identifier parentLocation, List<ModelElement> elements, Map<String, String> textures, boolean ambientOcclusion, boolean gui3d, ModelTransformation cameraTransforms, List<ModelOverride> overrides) {
        this.elements = elements;
        this.ambientOcclusion = ambientOcclusion;
        this.gui3d = gui3d;
        this.textures = textures;
        this.parentLocation = parentLocation;
        this.cameraTransforms = cameraTransforms;
        this.overrides = overrides;
    }

    public List<ModelElement> getElements() {
        return this.elements.isEmpty() && this.hasParent() ? this.parent.getElements() : this.elements;
    }

    private boolean hasParent() {
        return this.parent != null;
    }

    public boolean isAmbientOcclusion() {
        return this.hasParent() ? this.parent.isAmbientOcclusion() : this.ambientOcclusion;
    }

    public boolean isGui3d() {
        return this.gui3d;
    }

    public boolean isResolved() {
        return this.parentLocation == null || this.parent != null && this.parent.isResolved();
    }

    public void getParentFromMap(Map<Identifier, TabulaModelBlock> p_178299_1_) {
        if (this.parentLocation != null) {
            this.parent = p_178299_1_.get(this.parentLocation);
        }
    }

    public Collection<Identifier> getOverrideLocations() {
        Set<Identifier> set = Sets.newHashSet();

        for (ModelOverride override : this.overrides) {
            set.add(override.getModelId());
        }

        return set;
    }

    public List<ModelOverride> getOverrides() {
        return this.overrides;
    }

    public boolean isTexturePresent(String textureName) {
        return !"missingno".equals(this.resolveTextureName(textureName));
    }

    public String resolveTextureName(String textureName) {
        if (!this.startsWithHash(textureName)) {
            textureName = '#' + textureName;
        }

        return this.resolveTextureName(textureName, new Bookkeep(this));
    }

    private String resolveTextureName(String textureName, Bookkeep bookkeep) {
        if (this.startsWithHash(textureName)) {
            if (this == bookkeep.modelExt) {
                LOGGER.warn("Unable to resolve texture due to upward reference: {} in {}", textureName, this.name);
                return "missingno";
            } else {
                String texture = this.textures.get(textureName.substring(1));

                if (texture == null && this.hasParent()) {
                    texture = this.parent.resolveTextureName(textureName, bookkeep);
                }

                bookkeep.modelExt = this;

                if (texture != null && this.startsWithHash(texture)) {
                    texture = bookkeep.model.resolveTextureName(texture, bookkeep);
                }

                return texture != null && !this.startsWithHash(texture) ? texture : "missingno";
            }
        } else {
            return textureName;
        }
    }

    private boolean startsWithHash(String hash)
    {
        return hash.charAt(0) == '#';
    }

    @Nullable
    public Identifier getParentLocation()
    {
        return this.parentLocation;
    }

    public TabulaModelBlock getRootModel()
    {
        return this.hasParent() ? this.parent.getRootModel() : this;
    }

    public ModelTransformation getAllTransforms() {
        Transformation thirdPersonLeftHand = this.getTransform(ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND);
        Transformation thirdPersonRightHand = this.getTransform(ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
        Transformation firstPersonLeftHand = this.getTransform(ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND);
        Transformation secondPersonRightHand = this.getTransform(ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
        Transformation head = this.getTransform(ModelTransformation.Mode.HEAD);
        Transformation gui = this.getTransform(ModelTransformation.Mode.GUI);
        Transformation ground = this.getTransform(ModelTransformation.Mode.GROUND);
        Transformation fixed = this.getTransform(ModelTransformation.Mode.FIXED);
        return new ModelTransformation(thirdPersonLeftHand, thirdPersonRightHand, firstPersonLeftHand, secondPersonRightHand, head, gui, ground, fixed);
    }

    private Transformation getTransform(ModelTransformation.Mode type) {
        return this.parent != null && !this.cameraTransforms.isTransformationDefined(type) ? this.parent.getTransform(type) : this.cameraTransforms.getTransformation(type);
    }

    public static void checkModelHierarchy(Map<Identifier, TabulaModelBlock> map) {
        for (TabulaModelBlock TabulaModelBlock : map.values()) {
            try {
                TabulaModelBlock parent = TabulaModelBlock.parent;

                for (TabulaModelBlock block = parent.parent; parent != block; block = block.parent.parent) {
                    parent = parent.parent;
                }

                throw new LoopException();
            }
            catch (NullPointerException ignored) {
            }
        }
    }

    @Environment(EnvType.CLIENT)
    static final class Bookkeep {
        public final TabulaModelBlock model;
        public TabulaModelBlock modelExt;

        private Bookkeep(TabulaModelBlock block)
        {
            this.model = block;
        }
    }

    public static class Deserializer implements JsonDeserializer<TabulaModelBlock> {
        @Override
        public TabulaModelBlock deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            List<ModelElement> list = this.getModelElements(context, object);
            String parent = this.getParent(object);
            Map<String, String> map = this.getTextures(object);
            boolean flag = this.getAmbientOcclusionEnabled(object);
            ModelTransformation transformation = ModelTransformation.NONE;

            if (object.has("display")) {
                JsonObject display = JsonHelper.getObject(object, "display");
                transformation = context.deserialize(display, ModelTransformation.class);
            }

            List<ModelOverride> list1 = this.getItemOverrides(context, object);
            Identifier id = parent.isEmpty() ? null : new Identifier(parent);
            return new TabulaModelBlock(id, list, map, flag, true, transformation, list1);
        }

        protected List<ModelOverride> getItemOverrides(JsonDeserializationContext deserializationContext, JsonObject object) {
            List<ModelOverride> list = Lists.newArrayList();

            if (object.has("overrides")) {
                for (JsonElement jsonelement : JsonHelper.getArray(object, "overrides")) {
                    list.add(deserializationContext.deserialize(jsonelement, ModelOverride.class));
                }
            }

            return list;
        }

        private Map<String, String> getTextures(JsonObject object) {
            Map<String, String> map = Maps.newHashMap();

            if (object.has("textures")) {
                JsonObject textures = object.getAsJsonObject("textures");

                for (Map.Entry<String, JsonElement> entry : textures.entrySet()) {
                    map.put(entry.getKey(), entry.getValue().getAsString());
                }
            }

            return map;
        }

        private String getParent(JsonObject object) {
            return JsonHelper.getString(object, "parent", "");
        }

        protected boolean getAmbientOcclusionEnabled(JsonObject object) {
            return JsonHelper.getBoolean(object, "ambientocclusion", true);
        }

        protected List<ModelElement> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
            List<ModelElement> list = Lists.newArrayList();

            if (object.has("elements")) {
                for (JsonElement element : JsonHelper.getArray(object, "elements")) {
                    list.add(deserializationContext.deserialize(element, ModelElement.class));
                }
            }

            return list;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class LoopException extends RuntimeException {
    }
}
