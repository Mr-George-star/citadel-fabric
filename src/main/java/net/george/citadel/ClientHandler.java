package net.george.citadel;

import io.github.fabricators_of_create.porting_lib.event.client.RenderPlayerEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.george.citadel.animation.IAnimatedEntity;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.EventResult;
import net.george.citadel.client.CitadelItemStackRenderer;
import net.george.citadel.client.event.EventRenderSplashText;
import net.george.citadel.client.event.ScreenOpeningEvent;
import net.george.citadel.client.event.ScreenRenderEvents;
import net.george.citadel.client.game.Tetris;
import net.george.citadel.client.gui.GuiCitadelBook;
import net.george.citadel.client.model.TabulaModel;
import net.george.citadel.client.model.TabulaModelHandler;
import net.george.citadel.client.render.BuiltinModelItemRendererRegistry;
import net.george.citadel.client.render.CitadelLecternRenderer;
import net.george.citadel.client.rewards.CitadelPatreonRenderer;
import net.george.citadel.client.tick.ClientTickRateTracker;
import net.george.citadel.config.ServerConfig;
import net.george.citadel.item.ItemWithHoverAnimation;
import net.george.citadel.server.entity.CitadelEntityData;
import net.george.citadel.server.event.EventChangeEntityTickRate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.BackupPromptScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("unused")
public class ClientHandler implements ClientModInitializer {
    public static final ClientHandler HANDLER = new ClientHandler();
    public static TabulaModel CITADEL_MODEL;
    public static boolean hideFollower = false;
    private final Map<ItemStack, Float> prevMouseOverProgresses = new HashMap<>();

    private final Map<ItemStack, Float> mouseOverProgresses = new HashMap<>();
    private ItemStack lastHoveredItem = null;

    private Tetris aprilFoolsTetrisGame = null;

    @Override
    public void onInitializeClient() {
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException exception) {
            Citadel.LOGGER.catching(exception);
        }
        BuiltinModelItemRendererRegistry.register(Citadel.EFFECT_ITEM.get(), new CitadelItemStackRenderer());
        BuiltinModelItemRendererRegistry.register(Citadel.FANCY_ITEM.get(), new CitadelItemStackRenderer());
        BuiltinModelItemRendererRegistry.register(Citadel.ICON_ITEM.get(), new CitadelItemStackRenderer());
        BlockEntityRendererFactories.register(Citadel.LECTERN_BLOCK_ENTITY.get(), CitadelLecternRenderer::new);
        this.registerEvents();
    }

    private void registerEvents() {
        renderTooltipColor();
        clientTick();
        renderSplashTextBefore();
        onOpenGui();
        playerRender();
        screenRender();
    }

    public void screenRender() {
        ScreenRenderEvents.PRE.register((screen, matrices, mouseX, mouseY, tickDelta) -> {
            if (screen instanceof TitleScreen && CitadelConstants.isAprilFools()) {
                if (this.aprilFoolsTetrisGame == null) {
                    this.aprilFoolsTetrisGame = new Tetris();
                } else {
                    this.aprilFoolsTetrisGame.render((TitleScreen) screen, matrices, tickDelta);
                }
            }
            return false;
        });
        ScreenRenderEvents.POST.register((screen, matrices, mouseX, mouseY, tickDelta) -> {
            if (screen instanceof TitleScreen && CitadelConstants.isAprilFools()) {
                if (this.aprilFoolsTetrisGame == null) {
                    this.aprilFoolsTetrisGame = new Tetris();
                } else {
                    this.aprilFoolsTetrisGame.render((TitleScreen) screen, matrices, tickDelta);
                }
            }
            return false;
        });
    }

    public void playerRender() {
        RenderPlayerEvents.POST.register((player, renderer, tickDelta, matrices, vertices, light) -> {
            String username = player.getName().getString();
            if (!player.isPartVisible(PlayerModelPart.CAPE)) {
                return;
            }
            if (Citadel.PATRONS.contains(username)) {
                NbtCompound tag = CitadelEntityData.getOrCreateCitadelTag(MinecraftClient.getInstance().player);
                String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
                if (!rendererName.equals("none") && !hideFollower) {
                    CitadelPatreonRenderer patreonRenderer = CitadelPatreonRenderer.get(rendererName);
                    if (patreonRenderer != null) {
                        float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
                        float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
                        float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
                        patreonRenderer.render(matrices, vertices, light, tickDelta, player, distance, speed, height);
                    }
                }
            }
        });
    }

    public void onOpenGui() {
        ScreenOpeningEvent.EVENT.register(event -> {
            if (ServerConfig.skipWarnings) {
                try{
                    if (event.getNewScreen() instanceof BackupPromptScreen backupPromptScreen) {
                        MutableText title = Text.translatable("selectWorld.backupQuestion.experimental");

                        if (backupPromptScreen.getTitle().equals(title)) {
                            backupPromptScreen.callback.proceed(false, true);
                        }
                    }
                    if (event.getNewScreen() instanceof ConfirmScreen confirmScreen) {
                        MutableText title = Text.translatable("selectWorld.backupQuestion.experimental");
                        if (confirmScreen.getTitle().equals(title)) {
                            confirmScreen.callback.accept(true);
                        }
                    }
                } catch (Exception exception) {
                    Citadel.LOGGER.warn("Citadel couldn't skip world loadings");
                }
            }
        });
    }

    public void renderSplashTextBefore() {
        EventRenderSplashText.PRE.register(event -> {
            if (CitadelConstants.isAprilFools() && this.aprilFoolsTetrisGame != null) {
                event.setResult(EventResult.ALLOW);
                float hue = (System.currentTimeMillis() % 6000) / 6000f;
                event.getMatrices().multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float)Math.sin(hue * Math.PI) * 10));
                if (!this.aprilFoolsTetrisGame.isStarted()) {
                    event.setSplashText("Psst... press 'T' ;)");
                } else {
                    event.setSplashText("");
                }
                int rainbow = Color.HSBtoRGB(hue, 0.6f, 1);
                event.setSplashTextColor(rainbow);
            }
        });
    }

    public void clientTick() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!isGamePaused()) {
                ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).masterTick();
                tickMouseOverAnimations();
            }
            if (!isGamePaused() && CitadelConstants.isAprilFools()) {
                if (this.aprilFoolsTetrisGame != null) {
                    if (MinecraftClient.getInstance().currentScreen instanceof TitleScreen){
                        this.aprilFoolsTetrisGame.tick();
                    } else {
                        this.aprilFoolsTetrisGame.reset();
                    }
                }
            }
        });
    }

    private void tickMouseOverAnimations() {
        this.prevMouseOverProgresses.putAll(this.mouseOverProgresses);
        if (this.lastHoveredItem != null) {
            float prev = this.mouseOverProgresses.getOrDefault(this.lastHoveredItem, 0F);
            float maxTime = 5F;
            if (this.lastHoveredItem.getItem() instanceof ItemWithHoverAnimation hoverOver) {
                maxTime = hoverOver.getMaxHoverOverTime(this.lastHoveredItem);
            }
            if (prev < maxTime) {
                this.mouseOverProgresses.put(this.lastHoveredItem, prev + 1);
            }
        }

        if (!this.mouseOverProgresses.isEmpty()) {
            Iterator<Map.Entry<ItemStack, Float>> iterator = this.mouseOverProgresses.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ItemStack, Float> next = iterator.next();
                float progress = next.getValue();
                if (this.lastHoveredItem == null || next.getKey() != this.lastHoveredItem) {
                    if (progress == 0) {
                        iterator.remove();
                    } else {
                        next.setValue(progress - 1);
                    }
                }
            }
        }
        this.lastHoveredItem = null;
    }

    public void renderTooltipColor() {
        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            if (stack.getItem() instanceof ItemWithHoverAnimation hoverOver && hoverOver.canHoverOver(stack)) {
                this.lastHoveredItem = stack;
            } else {
                this.lastHoveredItem = null;
            }
        });
    }

    public float getMouseOverProgress(ItemStack itemStack){
        float prev = this.prevMouseOverProgresses.getOrDefault(itemStack, 0F);
        float current = this.mouseOverProgresses.getOrDefault(itemStack, 0F);
        float lerped = prev + (current - prev) * MinecraftClient.getInstance().getTickDelta();
        float maxTime = 5F;
        if(itemStack.getItem() instanceof ItemWithHoverAnimation hoverOver){
            maxTime = hoverOver.getMaxHoverOverTime(itemStack);
        }
        return lerped / maxTime;
    }

    public void handleAnimationPacket(int entityId, int index) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            IAnimatedEntity entity = (IAnimatedEntity) player.world.getEntityById(entityId);
            if (entity != null) {
                if (index == -1) {
                    entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                } else {
                    entity.setAnimation(entity.getAnimations()[index]);
                }
                entity.setAnimationTick(0);
            }
        }
    }

    public void handleClientTickRatePacket(NbtCompound nbt) {
        ClientTickRateTracker.getForClient(MinecraftClient.getInstance()).syncFromServer(nbt);
    }

    public void openBookGUI(ItemStack book) {
        MinecraftClient.getInstance().setScreen(new GuiCitadelBook(book));
    }

    public boolean isGamePaused() {
        return MinecraftClient.getInstance().isPaused();
    }

    public PlayerEntity getClientSidePlayer() {
        return MinecraftClient.getInstance().player;
    }

    public boolean canEntityTickClient(World world, Entity entity) {
        ClientTickRateTracker tracker = ClientTickRateTracker.getForClient(MinecraftClient.getInstance());
        if (tracker.isTickingHandled(entity)) {
            return false;
        } else if(!tracker.hasNormalTickRate(entity)) {
            EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
            if (CitadelEventManager.INSTANCE.send(event)) {
                return true;
            } else {
                tracker.addTickBlockedEntity(entity);
                return false;
            }
        }
        return true;
    }
}
