package net.george.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.george.citadel.Citadel;
import net.george.citadel.client.gui.data.*;
import net.george.citadel.client.model.TabulaModel;
import net.george.citadel.client.model.TabulaModelHandler;
import net.george.citadel.recipe.SpecialRecipeInGuideBook;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SuppressWarnings({"unused", "deprecation", "CallToPrintStackTrace", "rawtypes"})
public abstract class GuiBasicBook extends Screen {
    private static final Identifier BOOK_PAGE_TEXTURE = new Identifier("citadel:textures/gui/book/book_pages.png");
    private static final Identifier BOOK_BINDING_TEXTURE = new Identifier("citadel:textures/gui/book/book_binding.png");
    private static final Identifier BOOK_WIDGET_TEXTURE = new Identifier("citadel:textures/gui/book/widgets.png");
    private static final Identifier BOOK_BUTTONS_TEXTURE = new Identifier("citadel:textures/gui/book/link_buttons.png");
    protected final List<LineData> lines = new ArrayList<>();
    protected final List<LinkData> links = new ArrayList<>();
    protected final List<ItemRenderData> itemRenders = new ArrayList<>();
    protected final List<RecipeData> recipes = new ArrayList<>();
    protected final List<TabulaRenderData> tabulaRenders = new ArrayList<>();
    protected final List<EntityRenderData> entityRenders = new ArrayList<>();
    protected final List<EntityLinkData> entityLinks = new ArrayList<>();
    protected final List<ImageData> images = new ArrayList<>();
    protected final List<Whitespace> yIndexesToSkip = new ArrayList<>();
    private final Map<String, TabulaModel> renderedTabulaModels = new HashMap<>();
    private final Map<String, Entity> renderedEntities = new HashMap<>();
    private final Map<String, Identifier> textureMap = new HashMap<>();
    protected ItemStack bookStack;
    protected int xSize = 390;
    protected int ySize = 320;
    protected int currentPageCounter = 0;
    protected int maxPagesFromPrinting = 0;
    protected int linesFromJSON = 0;
    protected int linesFromPrinting = 0;
    protected Identifier prevPageJSON;
    protected Identifier currentPageJSON;
    protected Identifier currentPageText = null;
    protected BookPageButton buttonNextPage;
    protected BookPageButton buttonPreviousPage;
    protected BookPage internalPage = null;
    protected String writtenTitle = "";
    protected int preservedPageIndex = 0;
    protected String entityTooltip;
    private int mouseX;
    private int mouseY;

    public GuiBasicBook(ItemStack bookStack, Text title) {
        super(title);
        this.bookStack = bookStack;
        this.currentPageJSON = getRootPage();
    }

    public static void drawTabulaModelOnScreen(MatrixStack stack, TabulaModel model, Identifier tex, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY) {
        float f = (float) Math.atan(mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrices = new MatrixStack();
        matrices.translate((float) posX, (float) posY, 120.0D);
        matrices.scale(scale, scale, scale);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(0.0F);
        Quaternion quaternion1 = Vec3f.POSITIVE_X.getDegreesQuaternion(f1 * 20.0F);
        if (follow) {
            quaternion.hamiltonProduct(quaternion1);
        }
        matrices.multiply(quaternion);
        if (follow) {
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F + f * 40.0F));
        }
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) -xRot));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) yRot));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) zRot));
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        dispatcher.setRotation(quaternion1);
        dispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> {
            VertexConsumer vertices = immediate.getBuffer(RenderLayer.getEntityCutoutNoCull(tex));
            model.resetToDefaultPose();
            model.render(matrices, vertices, 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        });
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public void drawEntityOnScreen(MatrixStack stack, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity) {
        float customYaw = posX - mouseX;
        float customPitch = posY - mouseY;
        float f = (float) Math.atan(customYaw / 40.0F);
        float f1 = (float) Math.atan(customPitch / 40.0F);

        if (follow) {
            float setX = f1 * 20.0F;
            float setY = f * 20.0F;
            entity.setPitch(setX);
            entity.setYaw(setY);
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).bodyYaw = setY;
                ((LivingEntity) entity).prevBodyYaw = setY;
                ((LivingEntity) entity).headYaw = setY;
                ((LivingEntity) entity).prevHeadYaw = setY;
            }
        } else {
            f = 0;
            f1 = 0;
        }

        MatrixStack matrices = RenderSystem.getModelViewStack();
        matrices.push();
        matrices.translate(posX, posY, 1050.0D);
        matrices.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrices1 = new MatrixStack();
        matrices1.translate(0.0D, 0.0D, 1000.0D);
        matrices1.scale(scale, scale, scale);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180F);
        Quaternion quaternion1 = Vec3f.POSITIVE_X.getDegreesQuaternion(f1 * 20.0F);
        quaternion.hamiltonProduct(quaternion1);
        quaternion.hamiltonProduct(Vec3f.NEGATIVE_X.getDegreesQuaternion((float) xRot));
        quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) yRot));
        quaternion.hamiltonProduct(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) zRot));
        matrices1.multiply(quaternion);

        Vec3f light0 = Util.make(new Vec3f(1, -1.0F, -1.0F), Vec3f::normalize);
        Vec3f light1 = Util.make(new Vec3f(-1, -1.0F, 1.0F), Vec3f::normalize);
        RenderSystem.setShaderLights(light0, light1);
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        dispatcher.setRotation(quaternion1);
        dispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrices1, immediate, 15728880));
        immediate.draw();
        dispatcher.setRenderShadows(true);

        entity.setYaw(0);
        entity.setPitch(0);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).bodyYaw = 0;
            ((LivingEntity) entity).prevHeadYaw = 0;
            ((LivingEntity) entity).headYaw = 0;
        }


        matrices.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    protected void init() {
        super.init();
        playBookOpeningSound();
        addNextPreviousButtons();
        addLinkButtons();
    }

    private void addNextPreviousButtons() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        this.buttonPreviousPage = this.addDrawableChild(new BookPageButton(this, k + 10, l + 180, false, (button) -> this.onSwitchPage(false), true));
        this.buttonNextPage = this.addDrawableChild(new BookPageButton(this, k + 365, l + 180, true, (button) -> this.onSwitchPage(true), true));
    }

    private void addLinkButtons() {
        this.clearChildren();
        addNextPreviousButtons();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (LinkData linkData : links) {
            if (linkData.getPage() == this.currentPageCounter) {
                int maxLength = Math.max(100, MinecraftClient.getInstance().textRenderer.getWidth(linkData.getTitleText()) + 20);
                this.yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - maxLength / 2, linkData.getY(), 100, 20));
                this.addDrawableChild(new LinkButton(this, k + linkData.getX() - maxLength / 2, l + linkData.getY(), maxLength, 20, Text.translatable(linkData.getTitleText()), linkData.getDisplayItem(), (p_213021_1_) -> {
                    this.prevPageJSON = this.currentPageJSON;
                    this.currentPageJSON = new Identifier(getTextFileDirectory() + linkData.getLinkedPage());
                    this.preservedPageIndex = this.currentPageCounter;
                    this.currentPageCounter = 0;
                    addNextPreviousButtons();
                }));
            }
            if (linkData.getPage() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.getPage();
            }
        }

        for (EntityLinkData linkData : this.entityLinks) {
            if (linkData.getPage() == this.currentPageCounter) {
                this.yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - 12, linkData.getY(), 100, 20));
                this.addDrawableChild(new EntityLinkButton(this, linkData, k, l, (p_213021_1_) -> {
                    this.prevPageJSON = this.currentPageJSON;
                    this.currentPageJSON = new Identifier(getTextFileDirectory() + linkData.getLinkedPage());
                    this.preservedPageIndex = this.currentPageCounter;
                    this.currentPageCounter = 0;
                    addNextPreviousButtons();
                }));
            }
            if (linkData.getPage() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.getPage();
            }
        }
    }

    private void onSwitchPage(boolean next) {
        if (next) {
            if (this.currentPageCounter < this.maxPagesFromPrinting) {
                this.currentPageCounter++;
            }
        } else {
            if (this.currentPageCounter > 0) {
                this.currentPageCounter--;
            } else {
                if (this.internalPage != null && !this.internalPage.getParent().isEmpty()) {
                    this.prevPageJSON = this.currentPageJSON;
                    this.currentPageJSON = new Identifier(getTextFileDirectory() + this.internalPage.getParent());
                    this.currentPageCounter = this.preservedPageIndex;
                    this.preservedPageIndex = 0;
                }
            }
        }
        refreshSpacing();
    }

    @Override
    public void render(MatrixStack matrixStack, int x, int y, float partialTicks) {
        this.mouseX = x;
        this.mouseY = y;
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int color = getBindingColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);
        this.renderBackground(matrixStack);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, getBookBindingTexture());
        BookBlit.setRGB(r, g, b, 255);
        BookBlit.blit(matrixStack, k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
        RenderSystem.setShaderTexture(0, getBookPageTexture());
        BookBlit.setRGB(255, 255, 255, 255);
        BookBlit.blit(matrixStack, k, l, 0, 0, this.xSize, this.ySize, this.xSize, this.ySize);
        if (this.internalPage == null || this.currentPageJSON != this.prevPageJSON || this.prevPageJSON == null) {
            this.internalPage = generatePage(this.currentPageJSON);
            if (this.internalPage != null) {
                refreshSpacing();
            }
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (this.internalPage != null) {
            writePageText(matrixStack, x, y);
        }
        super.render(matrixStack, x, y, partialTicks);
        this.prevPageJSON = this.currentPageJSON;
        if (this.internalPage != null) {
            matrixStack.push();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            renderOtherWidgets(matrixStack, x, y, this.internalPage);
            matrixStack.pop();
        }
        if (this.entityTooltip != null) {
            matrixStack.push();
            matrixStack.translate(0, 0, 550);
            renderOrderedTooltip(matrixStack, MinecraftClient.getInstance().textRenderer.wrapLines(Text.translatable(this.entityTooltip), Math.max(this.width / 2 - 43, 170)), x, y);
            this.entityTooltip = null;
            matrixStack.pop();
        }
    }

    private void refreshSpacing() {
        if (this.internalPage != null) {
            String lang = MinecraftClient.getInstance().getLanguageManager().getLanguage().getCode().toLowerCase();
            this.currentPageText = new Identifier(getTextFileDirectory() + lang + "/" + this.internalPage.getTextFileToReadFrom());
            boolean invalid = false;
            try {
                //test if it exists. if no exception, then the language is supported
                InputStream is = MinecraftClient.getInstance().getResourceManager().open(this.currentPageText);
                is.close();
            } catch (Exception e) {
                invalid = true;
                Citadel.LOGGER.warn("Could not find language file for translation, defaulting to english");
                this.currentPageText = new Identifier(getTextFileDirectory() + "en_us/" + this.internalPage.getTextFileToReadFrom());
            }

            readInPageWidgets(this.internalPage);
            addWidgetSpacing();
            addLinkButtons();
            readInPageText(this.currentPageText);
        }
    }

    private Item getItemByRegistryName(String registryName) {
        return Registry.ITEM.get(new Identifier(registryName));
    }

    private Recipe getRecipeByName(String registryName) {
        try {
            RecipeManager manager = MinecraftClient.getInstance().world.getRecipeManager();
            if (manager.get(new Identifier(registryName)).isPresent()) {
                return manager.get(new Identifier(registryName)).get();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private void addWidgetSpacing() {
        this.yIndexesToSkip.clear();
        for (ItemRenderData itemRenderData : this.itemRenders) {
            Item item = getItemByRegistryName(itemRenderData.getItem());
            if (item != null) {
                this.yIndexesToSkip.add(new Whitespace(itemRenderData.getPage(), itemRenderData.getX(), itemRenderData.getY(), (int) (itemRenderData.getScale() * 17), (int) (itemRenderData.getScale() * 15)));
            }
        }
        for (RecipeData recipeData : this.recipes) {
            Recipe recipe = getRecipeByName(recipeData.getRecipe());
            if (recipe != null) {
                this.yIndexesToSkip.add(new Whitespace(recipeData.getPage(), recipeData.getX(), recipeData.getY() - (int) (recipeData.getScale() * 15), (int) (recipeData.getScale() * 35), (int) (recipeData.getScale() * 60), true));
            }
        }
        for (ImageData imageData : this.images) {
            if (imageData != null) {
                this.yIndexesToSkip.add(new Whitespace(imageData.getPage(), imageData.getX(), imageData.getY(), (int) (imageData.getScale() * imageData.getWidth()), (int) (imageData.getScale() * imageData.getHeight() * 0.8F)));
            }
        }
        if (!this.writtenTitle.isEmpty()) {
            this.yIndexesToSkip.add(new Whitespace(0, 20, 5, 70, 15));
        }
    }

    private void renderOtherWidgets(MatrixStack matrixStack, int x, int y, BookPage page) {
        int color = getBindingColor();
        int r = (color & 0xFF0000) >> 16;
        int g = (color & 0xFF00) >> 8;
        int b = (color & 0xFF);

        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (ImageData imageData : this.images) {
            if (imageData.getPage() == this.currentPageCounter) {
                if (imageData != null) {
                    Identifier tex = this.textureMap.get(imageData.getTexture());
                    if (tex == null) {
                        tex = new Identifier(imageData.getTexture());
                        this.textureMap.put(imageData.getTexture(), tex);
                    }
                    // yIndexesToSkip.put(imageData.getPage(), new Whitespace(imageData.getX(), imageData.getY(),(int) (imageData.getScale() * imageData.getWidth()), (int) (imageData.getScale() * imageData.getHeight() * 0.8F)));
                    float scale = (float) imageData.getScale();
                    matrixStack.push();
                    matrixStack.translate(k + imageData.getX(), l + imageData.getY(), 0);
                    matrixStack.scale(scale, scale, scale);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, tex);
                    this.drawTexture(matrixStack, 0, 0, imageData.getU(), imageData.getV(), imageData.getWidth(), imageData.getHeight());
                    matrixStack.pop();
                }
            }
        }
        for (RecipeData recipeData : this.recipes) {
            if (recipeData.getPage() == this.currentPageCounter) {
                matrixStack.push();
                matrixStack.translate(k + recipeData.getX(), l + recipeData.getY(), 0);
                float scale = (float) recipeData.getScale();
                matrixStack.scale(scale, scale, scale);
                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, getBookWidgetTexture());
                this.drawTexture(matrixStack, 0, 0, 0, 88, 116, 53);
                matrixStack.pop();
            }
        }
        for (RecipeData recipeData : this.recipes) {
            if (recipeData.getPage() == this.currentPageCounter) {
                Recipe recipe = getRecipeByName(recipeData.getRecipe());
                if (recipe != null) {
                    MatrixStack poseStack = RenderSystem.getModelViewStack();
                    renderRecipe(poseStack, recipe, recipeData, k, l);
                }
            }
        }

        for (TabulaRenderData tabulaRenderData : this.tabulaRenders) {
            if (tabulaRenderData.getPage() == this.currentPageCounter) {
                TabulaModel model = null;
                Identifier texture;
                if (this.textureMap.get(tabulaRenderData.getTexture()) != null) {
                    texture = this.textureMap.get(tabulaRenderData.getTexture());
                } else {
                    texture = this.textureMap.put(tabulaRenderData.getTexture(), new Identifier(tabulaRenderData.getTexture()));
                }
                if (this.renderedTabulaModels.get(tabulaRenderData.getModel()) != null) {
                    model = this.renderedTabulaModels.get(tabulaRenderData.getModel());
                } else {
                    try {
                        model = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/" + tabulaRenderData.getModel().split(":")[0] + "/" + tabulaRenderData.getModel().split(":")[1]));
                    } catch (Exception exception) {
                        Citadel.LOGGER.warn("Could not load in tabula model for book at " + tabulaRenderData.getModel());
                    }
                    this.renderedTabulaModels.put(tabulaRenderData.getModel(), model);
                }

                if (model != null && texture != null) {
                    float scale = (float) tabulaRenderData.getScale();
                    drawTabulaModelOnScreen(matrixStack, model, texture, k + tabulaRenderData.getX(), l + tabulaRenderData.getY(), 30 * scale, tabulaRenderData.isFollow_cursor(), tabulaRenderData.getRot_x(), tabulaRenderData.getRot_y(), tabulaRenderData.getRot_z(), mouseX, mouseY);
                }
            }
        }
        for (EntityRenderData data : this.entityRenders) {
            if (data.getPage() == this.currentPageCounter) {
                Entity model = null;
                EntityType type = Registry.ENTITY_TYPE.get(new Identifier(data.getEntity()));
                if (type != null) {
                    model = this.renderedEntities.putIfAbsent(data.getEntity(), type.create(MinecraftClient.getInstance().world));
                }
                if (model != null) {
                    float scale = (float) data.getScale();
                    model.age = MinecraftClient.getInstance().player.age;
                    if (data.getEntityData() != null) {
                        try {
                            NbtCompound nbt = StringNbtReader.parse(data.getEntityData());
                            model.readNbt(nbt);
                        } catch (CommandSyntaxException exception) {
                            exception.printStackTrace();
                        }
                    }
                    drawEntityOnScreen(matrixStack, k + data.getX(), l + data.getY(), 30 * scale, data.isFollow_cursor(), data.getRot_x(), data.getRot_y(), data.getRot_z(), mouseX, mouseY, model);
                }
            }
        }

        for (ItemRenderData itemRenderData : itemRenders) {
            if (itemRenderData.getPage() == this.currentPageCounter) {
                Item item = getItemByRegistryName(itemRenderData.getItem());
                if (item != null) {
                    float scale = (float) itemRenderData.getScale();
                    ItemStack stack = new ItemStack(item);
                    if (itemRenderData.getItemTag() != null && !itemRenderData.getItemTag().isEmpty()) {
                        NbtCompound nbt = null;
                        try {
                            nbt = StringNbtReader.parse(itemRenderData.getItemTag());
                        } catch (CommandSyntaxException exception) {
                            exception.printStackTrace();
                        }
                        stack.setNbt(nbt);
                    }
                    this.itemRenderer.zOffset = 100.0F;
                    matrixStack.push();
                    MatrixStack matrices = RenderSystem.getModelViewStack();
                    matrices.push();
                    matrices.translate(k, l, 0);
                    matrices.scale(scale, scale, scale);
                    this.itemRenderer.renderInGuiWithOverrides(stack, itemRenderData.getX(), itemRenderData.getY());
                    this.itemRenderer.zOffset = 0.0F;
                    matrices.pop();
                    matrixStack.pop();
                    RenderSystem.applyModelViewMatrix();
                }
            }
        }
    }

    protected void renderRecipe(MatrixStack matrices, Recipe recipe, RecipeData recipeData, int k, int l) {
        int playerTicks = MinecraftClient.getInstance().player.age;
        float scale = (float) recipeData.getScale();
        DefaultedList<Ingredient> ingredients = recipe instanceof SpecialRecipeInGuideBook ? ((SpecialRecipeInGuideBook)recipe).getDisplayIngredients() : recipe.getIngredients();
        DefaultedList<ItemStack> displayedStacks = DefaultedList.of();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            ItemStack stack = ItemStack.EMPTY;
            if (!ingredient.isEmpty()) {
                if (ingredient.getMatchingStacks().length > 1) {
                    int currentIndex = (int) ((playerTicks / 20F) % ingredient.getMatchingStacks().length);
                    stack = ingredient.getMatchingStacks()[currentIndex];
                } else {
                    stack = ingredient.getMatchingStacks()[0];
                }
            }
            if (!stack.isEmpty()) {
                matrices.push();
                matrices.translate(k, l, 32.0F);
                matrices.translate((int) (recipeData.getX() + (i % 3) * 20 * scale), (int) (recipeData.getY() + (i / 3) * 20 * scale), 0);
                matrices.scale(scale, scale, scale);
                this.itemRenderer.zOffset = 100.0F;
                this.itemRenderer.renderInGuiWithOverrides(stack, 0, 0);
                this.itemRenderer.zOffset = 0.0F;
                matrices.pop();
            }
            displayedStacks.add(i, stack);
        }
        matrices.push();
        matrices.translate(k, l, 32.0F);
        float finScale = scale * 1.5F;
        matrices.translate(recipeData.getX() + 70 * finScale, recipeData.getY() + 10 * finScale, 0);
        matrices.scale(finScale, finScale, finScale);
        this.itemRenderer.zOffset = 100.0F;
        ItemStack result = recipe.getOutput();
        if(recipe instanceof SpecialRecipeInGuideBook){
            result = ((SpecialRecipeInGuideBook) recipe).getDisplayResultFor(displayedStacks);
        }
        this.itemRenderer.renderInGuiWithOverrides(result, 0, 0);
        this.itemRenderer.zOffset = 0.0F;
        matrices.pop();
    }

    protected void writePageText(MatrixStack matrixStack, int x, int y) {
        TextRenderer font = this.textRenderer;
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        for (LineData line : this.lines) {
            if (line.getPage() == this.currentPageCounter) {
                font.draw(matrixStack, line.getText(), k + 10 + line.getXIndex(), l + 10 + line.getYIndex() * 12, getTextColor());
            }
        }
        if (this.currentPageCounter == 0 && !this.writtenTitle.isEmpty()) {
            String actualTitle = I18n.translate(this.writtenTitle);
            matrixStack.push();
            float scale = 2F;
            if (font.getWidth(actualTitle) > 80) {
                scale = 2.0F - MathHelper.clamp((font.getWidth(actualTitle) - 80) * 0.011F, 0, 1.95F);
            }
            matrixStack.translate(k + 10, l + 10, 0);
            matrixStack.scale(scale, scale, scale);
            font.draw(matrixStack, actualTitle, 0, 0, getTitleColor());
            matrixStack.pop();
        }
        this.buttonNextPage.visible = this.currentPageCounter < this.maxPagesFromPrinting;
        this.buttonPreviousPage.visible = this.currentPageCounter > 0 || !this.currentPageJSON.equals(this.getRootPage());
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    protected void playBookOpeningSound() {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ITEM_BOOK_PAGE_TURN, 1.0F));
    }

    protected void playBookClosingSound() {
    }

    protected abstract int getBindingColor();

    protected int getWidgetColor() {
        return getBindingColor();
    }

    protected int getTextColor() {
        return 0X303030;
    }

    protected int getTitleColor() {
        return 0XBAAC98;
    }

    public abstract Identifier getRootPage();

    public abstract String getTextFileDirectory();

    protected Identifier getBookPageTexture() {
        return BOOK_PAGE_TEXTURE;
    }

    protected Identifier getBookBindingTexture() {
        return BOOK_BINDING_TEXTURE;
    }

    protected Identifier getBookWidgetTexture() {
        return BOOK_WIDGET_TEXTURE;
    }

    protected void playPageFlipSound() {
    }

    @Nullable
    protected BookPage generatePage(Identifier res) {
        Optional<Resource> resource;
        BookPage page = null;
        try {
            resource = MinecraftClient.getInstance().getResourceManager().getResource(res);
            try {
                resource = MinecraftClient.getInstance().getResourceManager().getResource(res);
                if (resource.isPresent()) {
                    BufferedReader inputStream = resource.get().getReader();
                    page = BookPage.deserialize(inputStream);
                }

            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } catch (Exception exception) {
            return null;
        }
        return page;
    }

    protected void readInPageWidgets(BookPage page) {
        this.links.clear();
        this.itemRenders.clear();
        this.recipes.clear();
        this.tabulaRenders.clear();
        this.entityRenders.clear();
        this.images.clear();
        this.entityLinks.clear();
        this.links.addAll(page.getLinkedButtons());
        this.entityLinks.addAll(page.getLinkedEntities());
        this.itemRenders.addAll(page.getItemRenders());
        this.recipes.addAll(page.getRecipes());
        this.tabulaRenders.addAll(page.getTabulaRenders());
        this.entityRenders.addAll(page.getEntityRenders());
        this.images.addAll(page.getImages());
        this.writtenTitle = page.generateTitle();
    }

    protected void readInPageText(Identifier res) {
        Resource resource = null;
        int xIndex = 0;
        int actualTextX = 0;
        int yIndex = 0;
        try {
            BufferedReader bufferedreader = MinecraftClient.getInstance().getResourceManager().openAsReader(res);
            try {
                List<String> readStrings = IOUtils.readLines(bufferedreader);
                this.linesFromJSON = readStrings.size();
                this.lines.clear();
                List<String> splitBySpaces = new ArrayList<>();
                for (String line : readStrings) {
                    splitBySpaces.addAll(Arrays.asList(line.split(" ")));
                }
                String lineToPrint = "";
                this.linesFromPrinting = 0;
                int page = 0;
                for (int i = 0; i < splitBySpaces.size(); i++) {
                    String word = splitBySpaces.get(i);
                    int cutoffPoint = xIndex > 100 ? 30 : 35;
                    boolean newline = word.equals("<NEWLINE>");
                    for (Whitespace indexes : yIndexesToSkip) {
                        int indexPage = indexes.getPage();
                        if (indexPage == page) {
                            int buttonX = indexes.getX();
                            int buttonY = indexes.getY();
                            int width = indexes.getWidth();
                            int height = indexes.getHeight();
                            if (indexes.isDown()) {
                                if (yIndex >= (buttonY) / 12F && yIndex <= (buttonY + height) / 12F) {
                                    if (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90) {
                                        yIndex += 2;
                                    }
                                }
                            } else {
                                if (yIndex >= (buttonY - height) / 12F && yIndex <= (buttonY + height) / 12F) {
                                    if (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90) {
                                        yIndex++;
                                    }
                                }
                            }
                        }
                    }
                    boolean last = i == splitBySpaces.size() - 1;
                    actualTextX += word.length() + 1;
                    if (lineToPrint.length() + word.length() + 1 >= cutoffPoint || newline) {
                        this.linesFromPrinting++;
                        if (yIndex > 13) {
                            if (xIndex > 0) {
                                page++;
                                xIndex = 0;
                                yIndex = 0;
                            } else {
                                xIndex = 200;
                                yIndex = 0;
                            }
                        }
                        if (last) {
                            lineToPrint = lineToPrint + " " + word;
                        }
                        this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                        yIndex++;
                        actualTextX = 0;
                        if (newline) {
                            yIndex++;
                        }
                        lineToPrint = "" + (word.equals("<NEWLINE>") ? "" : word);
                    } else {
                        lineToPrint = lineToPrint + " " + word;
                        if (last) {
                            this.linesFromPrinting++;
                            this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                            yIndex++;
                            actualTextX = 0;
                            if (newline) {
                                yIndex++;
                            }
                        }
                    }
                }
                this.maxPagesFromPrinting = page;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } catch (Exception exception) {
            Citadel.LOGGER.warn("Could not load in page .txt from json from page, page: " + res);
        }
    }

    public void setEntityTooltip(String hoverText) {
        this.entityTooltip = hoverText;
    }

    public Identifier getBookButtonsTexture(){
        return BOOK_BUTTONS_TEXTURE;
    }
}
