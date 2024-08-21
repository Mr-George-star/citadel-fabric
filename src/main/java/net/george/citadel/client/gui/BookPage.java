package net.george.citadel.client.gui;

import com.google.gson.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.citadel.client.gui.data.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.JsonHelper;

import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class BookPage {
    public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(BookPage.class, new Deserializer()).create();
    public String translatableTitle = null;
    private final String parent;
    private final String textFileToReadFrom;
    private final List<LinkData> linkedButtons;
    private final List<EntityLinkData> linkedEntities;
    private final List<ItemRenderData> itemRenders;
    private final List<RecipeData> recipes;
    private final List<TabulaRenderData> tabulaRenders;
    private final List<EntityRenderData> entityRenders;
    private final List<ImageData> images;
    private final String title;

    public BookPage(String parent, String textFileToReadFrom, List<LinkData> linkedButtons, List<EntityLinkData> linkedEntities, List<ItemRenderData> itemRenders, List<RecipeData> recipes, List<TabulaRenderData> tabulaRenders, List<EntityRenderData> entityRenders, List<ImageData> images, String title) {
        this.parent = parent;
        this.textFileToReadFrom = textFileToReadFrom;
        this.linkedButtons = linkedButtons;
        this.itemRenders = itemRenders;
        this.linkedEntities = linkedEntities;
        this.recipes = recipes;
        this.tabulaRenders = tabulaRenders;
        this.entityRenders = entityRenders;
        this.images = images;
        this.title = title;
    }

    public static BookPage deserialize(Reader reader) {
        return JsonHelper.deserialize(GSON, reader, BookPage.class);
    }

    public static BookPage deserialize(String jsonString) {
        return deserialize(new StringReader(jsonString));
    }

    public String getParent() {
        return this.parent;
    }

    public String getTitle() {
        return this.title;
    }

    public String getTextFileToReadFrom() {
        return this.textFileToReadFrom;
    }

    public List<LinkData> getLinkedButtons() {
        return this.linkedButtons;
    }

    public List<EntityLinkData> getLinkedEntities() {
        return this.linkedEntities;
    }

    public List<ItemRenderData> getItemRenders() {
        return this.itemRenders;
    }

    public List<RecipeData> getRecipes() {
        return this.recipes;
    }

    public List<TabulaRenderData> getTabulaRenders() {
        return this.tabulaRenders;
    }

    public List<EntityRenderData> getEntityRenders() {
        return this.entityRenders;
    }

    public List<ImageData> getImages() {
        return this.images;
    }

    public String generateTitle() {
        if (this.translatableTitle != null) {
            return I18n.translate(this.translatableTitle);
        }
        return this.title;
    }

    @Environment(EnvType.CLIENT)
    public static class Deserializer implements JsonDeserializer<BookPage> {
        public BookPage deserialize(JsonElement element, Type p_deserialize_2_, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = JsonHelper.asObject(element, "book page");
            LinkData[] linkedPageRead = JsonHelper.deserialize(object, "linked_page_buttons", new LinkData[0], context, LinkData[].class);
            EntityLinkData[] linkedEntitiesRead = JsonHelper.deserialize(object, "entity_buttons", new EntityLinkData[0], context, EntityLinkData[].class);
            ItemRenderData[] itemRendersRead = JsonHelper.deserialize(object, "item_renders", new ItemRenderData[0], context, ItemRenderData[].class);
            RecipeData[] recipesRead = JsonHelper.deserialize(object, "recipes", new RecipeData[0], context, RecipeData[].class);
            TabulaRenderData[] tabulaRendersRead = JsonHelper.deserialize(object, "tabula_renders", new TabulaRenderData[0], context, TabulaRenderData[].class);
            EntityRenderData[] entityRendersRead = JsonHelper.deserialize(object, "entity_renders", new EntityRenderData[0], context, EntityRenderData[].class);
            ImageData[] imagesRead = JsonHelper.deserialize(object, "images", new ImageData[0], context, ImageData[].class);

            String readParent = "";
            if (object.has("parent")) {
                readParent = JsonHelper.getString(object, "parent");
            }

            String readTextFile = "";
            if (object.has("text")) {
                readTextFile = JsonHelper.getString(object, "text");
            }

            String title = "";
            if (object.has("title")) {
                title = JsonHelper.getString(object, "title");
            }


            BookPage page = new BookPage(readParent, readTextFile, Arrays.asList(linkedPageRead), Arrays.asList(linkedEntitiesRead), Arrays.asList(itemRendersRead), Arrays.asList(recipesRead), Arrays.asList(tabulaRendersRead), Arrays.asList(entityRendersRead), Arrays.asList(imagesRead), title);
            if (object.has("title")) {
                page.translatableTitle = JsonHelper.getString(object, "title");
            }
            return page;
        }
    }
}
