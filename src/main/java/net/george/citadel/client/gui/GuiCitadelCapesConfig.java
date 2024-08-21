package net.george.citadel.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.citadel.Citadel;
import net.george.citadel.ClientHandler;
import net.george.citadel.client.rewards.CitadelCapes;
import net.george.citadel.server.entity.CitadelEntityData;
import net.george.citadel.server.message.PropertiesMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class GuiCitadelCapesConfig extends GameOptionsScreen {
    @Nullable
    private String capeType;
    private ButtonWidget button;

    public GuiCitadelCapesConfig(Screen parentScreen, GameOptions options) {
        super(parentScreen, options, Text.translatable("citadel.gui.capes"));
        NbtCompound nbt = CitadelEntityData.getOrCreateCitadelTag(MinecraftClient.getInstance().player);
        this.capeType = nbt.contains("CitadelCapeType") && !nbt.getString("CitadelCapeType").isEmpty() ? nbt.getString("CitadelCapeType") : null;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        assert MinecraftClient.getInstance().player != null;
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
        int i = this.width / 2;
        int j = this.height / 6;
        matrices.push();
        ClientHandler.hideFollower = true;
        renderBackwardsEntity(i, j + 144, 60, 0, 0, MinecraftClient.getInstance().player);
        ClientHandler.hideFollower = false;
        matrices.pop();
    }

    public static void renderBackwardsEntity(int x, int y, int size, float angleXComponent, float angleYComponent, LivingEntity entity) {
        MatrixStack viewStack = RenderSystem.getModelViewStack();
        viewStack.push();
        viewStack.translate(x, y, 1050.0D);
        viewStack.scale(1.0F, 1.0F, -1.0F);
        RenderSystem.applyModelViewMatrix();
        MatrixStack matrices = new MatrixStack();
        matrices.translate(0.0D, 0.0D, 1000.0D);
        matrices.scale((float)size, (float)size, (float)size);
        Quaternion quaternion = Vec3f.POSITIVE_Z.getDegreesQuaternion(180.0F);
        Quaternion quaternion1 = Vec3f.POSITIVE_X.getDegreesQuaternion(angleYComponent * 20.0F);
        quaternion.hamiltonProduct(quaternion1);
        quaternion.hamiltonProduct(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrices.multiply(quaternion);
        float f2 = entity.bodyYaw;
        float f3 = entity.getYaw();
        float f4 = entity.getPitch();
        float f5 = entity.prevHeadYaw;
        float f6 = entity.headYaw;
        entity.bodyYaw = 180.0F + angleXComponent * 20.0F;
        entity.setYaw(180.0F + angleXComponent * 40.0F);
        entity.setPitch(-angleYComponent * 20.0F);
        entity.headYaw = entity.getYaw();
        entity.prevHeadYaw = entity.getYaw();
        DiffuseLighting.method_34742();
        EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        quaternion1.conjugate();
        dispatcher.setRotation(quaternion1);
        dispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        RenderSystem.runAsFancy(() -> dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrices, immediate, 15728880));
        immediate.draw();
        dispatcher.setRenderShadows(true);
        entity.bodyYaw = f2;
        entity.setYaw(f3);
        entity.setPitch(f4);
        entity.prevHeadYaw = f5;
        entity.headYaw = f6;
        viewStack.pop();
        RenderSystem.applyModelViewMatrix();
        DiffuseLighting.enableGuiDepthLighting();
    }

    @Override
    protected void init() {
        super.init();

        assert this.client != null;
        assert MinecraftClient.getInstance().player != null;
        int i = this.width / 2;
        int j = this.height / 6;
        this.addDrawableChild(new ButtonWidget(i - 100, j+ 160, 200, 20, ScreenTexts.DONE, (widget) -> this.client.setScreen(this.parent)));

        this.addDrawableChild(this.button = new ButtonWidget(i - 100, j, 200, 20, getTypeText(), (widget) -> {
            CitadelCapes.Cape nextCape = CitadelCapes.getNextCape(this.capeType, MinecraftClient.getInstance().player.getUuid());
            this.capeType = nextCape == null ? null : nextCape.getIdentifier();
            NbtCompound nbt = CitadelEntityData.getOrCreateCitadelTag(MinecraftClient.getInstance().player);
            if (nbt != null) {
                if (this.capeType == null) {
                    nbt.putString("CitadelCapeType", "");
                    nbt.putBoolean("CitadelCapeDisabled", true);
                } else {
                    nbt.putString("CitadelCapeType", this.capeType);
                    nbt.putBoolean("CitadelCapeDisabled", false);
                }
                CitadelEntityData.setCitadelTag(MinecraftClient.getInstance().player, nbt);
            }
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelCapesConfig", nbt, MinecraftClient.getInstance().player.getId()));
            this.button.setMessage(getTypeText());
        }));
    }

    private Text getTypeText(){
        Text suffix;

        if (this.capeType == null) {
            suffix = Text.translatable("citadel.gui.no_cape");
        } else {
            CitadelCapes.Cape cape = CitadelCapes.getById(this.capeType);
            if (cape == null) {
                suffix = Text.translatable("citadel.gui.no_cape");
            } else {
                suffix = Text.translatable("cape." + cape.getIdentifier());
            }
        }
        return Text.translatable("citadel.gui.cape_type").append(" ").append(suffix);
    }
}
