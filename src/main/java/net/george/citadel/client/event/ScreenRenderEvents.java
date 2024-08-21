package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.ApiStatus;

public class ScreenRenderEvents {
    public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class,
            (listeners) -> (screen, matrices, mouseX, mouseY, tickDelta) -> {
                for (Pre callback : listeners) {
                    return !callback.interact(screen, matrices, mouseX, mouseY, tickDelta);
                }
                return true;
            });

    public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class,
            (listeners) -> (screen, matrices, mouseX, mouseY, tickDelta) -> {
                for (Post callback : listeners) {
                    return !callback.interact(screen, matrices, mouseX, mouseY, tickDelta);
                }
                return true;
            });

    public static void drawScreen(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        matrices.push();
        drawScreenInternal(screen, matrices, mouseX, mouseY, tickDelta);
        matrices.pop();
    }

    private static void drawScreenInternal(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta) {
        //Canceled -> false | Default -> true
        if (PRE.invoker().interact(screen, matrices, mouseX, mouseY, tickDelta)) {
            screen.render(matrices, mouseX, mouseY, tickDelta);
        }

        POST.invoker().interact(screen, matrices, mouseX, mouseY, tickDelta);
    }

    @FunctionalInterface
    public interface Pre {
        /**
         * Callback: true: canceled | false: success
         */
        @ApiStatus.Internal
        boolean interact(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
    }

    @FunctionalInterface
    public interface Post {
        /**
         * Callback: true: canceled | false: success
         */
        @ApiStatus.Internal
        boolean interact(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
    }
}
