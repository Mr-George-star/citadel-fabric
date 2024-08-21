package net.george.citadel.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.george.citadel.Citadel;
import net.george.citadel.client.rewards.CitadelPatreonRenderer;
import net.george.citadel.server.entity.CitadelEntityData;
import net.george.citadel.server.message.PropertiesMessage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public class GuiCitadelPatreonConfig extends GameOptionsScreen {
    private ForgeSlider distSlider;
    private ForgeSlider speedSlider;
    private ForgeSlider heightSlider;
    private ButtonWidget changeButton;
    private float rotateDist;
    private float rotateSpeed;
    private float rotateHeight;
    private String followType;

    public GuiCitadelPatreonConfig(Screen parentScreen, GameOptions gameSettings) {
        super(parentScreen, gameSettings, Text.translatable("citadel.gui.patreon_customization"));
        NbtCompound tag = CitadelEntityData.getOrCreateCitadelTag(MinecraftClient.getInstance().player);
        float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
        float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
        float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
        this.rotateDist = roundTo(distance, 3);
        this.rotateSpeed = roundTo(speed, 3);
        this.rotateHeight = roundTo(height, 3);
        this.followType = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
    }

    private void setSliderValue(int i, float sliderValue) {
        boolean flag = false;
        NbtCompound nbt = CitadelEntityData.getOrCreateCitadelTag(MinecraftClient.getInstance().player);
        if (i == 0) {
            this.rotateDist = roundTo(sliderValue, 3);
            nbt.putFloat("CitadelRotateDistance", this.rotateDist);
            //distSlider.isHovered = false;
        } else if(i == 1) {
            this.rotateSpeed = roundTo(sliderValue, 3);
            nbt.putFloat("CitadelRotateSpeed", this.rotateSpeed);
            //speedSlider.isHovered = false;
        } else {
            this.rotateHeight = roundTo(sliderValue, 3);
            nbt.putFloat("CitadelRotateHeight", this.rotateHeight);
            //heightSlider.isHovered = false;
        }
        CitadelEntityData.setCitadelTag(MinecraftClient.getInstance().player, nbt);
        Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", nbt, MinecraftClient.getInstance().player.getId()));
    }

    public static float roundTo(float value, int places) {
        return value;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        assert MinecraftClient.getInstance() != null;
        super.init();
        int i = this.width / 2;
        int j = this.height / 6;
        this.addDrawableChild(new ButtonWidget(i - 100, j+ 120, 200, 20, ScreenTexts.DONE, (widget) -> this.client.setScreen(this.parent)));
        this.addDrawableChild( this.distSlider = new ForgeSlider(i - 150 / 2 - 25, j + 30, 150, 20, Text.translatable("citadel.gui.orbit_dist").append(Text.translatable( ": ")), Text.translatable( ""), 0.125F, 5F, this.rotateDist, 0.1D, 1, true) {
            @Override
            protected void applyValue() {
                GuiCitadelPatreonConfig.this.setSliderValue(0, (float)getValue());
            }
        });
        this.addDrawableChild(new ButtonWidget(i - 150 / 2 + 135, j+ 30, 40, 20,  Text.translatable("citadel.gui.reset"), (widget) -> this.setSliderValue(0, 0.4F)));
        this.addDrawableChild(this.speedSlider = new ForgeSlider(i - 150 / 2 - 25, j + 60, 150, 20, Text.translatable("citadel.gui.orbit_speed").append(Text.translatable(": ")), Text.translatable( ""), 0.0F, 5F, this.rotateSpeed, 0.1D, 2, true) {
            @Override
            protected void applyValue() {
                GuiCitadelPatreonConfig.this.setSliderValue(1, (float)getValue());
            }
        });
        this.addDrawableChild(new ButtonWidget(i - 150 / 2 + 135, j+ 60, 40, 20,  Text.translatable("citadel.gui.reset"), (widget) -> this.setSliderValue(1, 1F / 5F)));
        this.addDrawableChild(this.heightSlider = new ForgeSlider(i - 150 / 2 - 25, j + 90, 150, 20, Text.translatable("citadel.gui.orbit_height").append(Text.translatable(": ")), Text.translatable( ""), 0.0F, 2F, this.rotateHeight, 0.1D, 2, true) {
            @Override
            protected void applyValue() {
                GuiCitadelPatreonConfig.this.setSliderValue(2, (float)getValue());
            }
        });
        this.addDrawableChild(new ButtonWidget(i - 150 / 2 + 135, j+ 90, 40, 20,  Text.translatable("citadel.gui.reset"), (widget) -> this.setSliderValue(2, 0.5F)));
        this.addDrawableChild(this.changeButton = new ButtonWidget(i - 100, j, 200, 20, getTypeText(), (widget) -> {
            this.followType = CitadelPatreonRenderer.getIdOfNext(this.followType);
            NbtCompound nbt = CitadelEntityData.getOrCreateCitadelTag(MinecraftClient.getInstance().player);
            if (nbt != null) {
                nbt.putString("CitadelFollowerType", this.followType);
                CitadelEntityData.setCitadelTag(MinecraftClient.getInstance().player, nbt);
            }
            Citadel.sendMSGToServer(new PropertiesMessage("CitadelPatreonConfig", nbt, MinecraftClient.getInstance().player.getId()));
            this.changeButton.setMessage(getTypeText());
        }));
    }

    private Text getTypeText(){
        return Text.translatable("citadel.gui.follower_type").append(Text.translatable("citadel.follower." + this.followType));
    }
}
