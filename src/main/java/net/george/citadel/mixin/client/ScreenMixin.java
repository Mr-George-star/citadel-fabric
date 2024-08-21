package net.george.citadel.mixin.client;

import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.client.event.ScreenInitEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow @Final public List<Drawable> drawables;
    @Shadow @Final private List<Selectable> selectables;
    @Shadow @Final private List<Element> children;

    @Shadow protected abstract void remove(Element child);

    @Unique
    private Consumer<Element> addConsumer;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;clearAndInit()V", shift = At.Shift.BEFORE), cancellable = true)
    private void citadel$initPre(MinecraftClient client, int width, int height, CallbackInfo ci) {
        this.addConsumer = (element) -> {
            if (element instanceof Drawable drawable) {
                this.drawables.add(drawable);
            }
            if (element instanceof Selectable selectable) {
                this.selectables.add(selectable);
            }

            this.children.add(element);
        };
        ScreenInitEvents.Pre pre = new ScreenInitEvents.Pre((Screen)(Object)this, this.children, this.addConsumer, this::remove);
        if (CitadelEventManager.INSTANCE.send(pre)) {
            ci.cancel();
        }
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    private void citadel$initPost(MinecraftClient client, int width, int height, CallbackInfo ci) {
        ScreenInitEvents.Post post = new ScreenInitEvents.Post((Screen)(Object)this, this.children, this.addConsumer, this::remove);
        CitadelEventManager.INSTANCE.send(post);
    }
}
