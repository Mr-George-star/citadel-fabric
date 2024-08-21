package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.ResultReturningEvent;
import net.george.citadel.api.event.marker.HasResult;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.util.math.MatrixStack;

@SuppressWarnings("unused")
public abstract class EventRenderSplashText extends ResultReturningEvent {
    public static final Event<Pre.Context> PRE = EventFactory.createArrayBacked(Pre.Context.class,
            (listeners) -> event -> {
                for (Pre.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public static final Event<Post.Context> POST = EventFactory.createArrayBacked(Post.Context.class,
            (listeners) -> event -> {
                for (Post.Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private String splashText;
    private final MatrixStack matrices;
    private final TitleScreen titleScreen;
    private final float tickDelta;

    public EventRenderSplashText(String splashText, MatrixStack matrices, TitleScreen titleScreen, float tickDelta) {
        this.splashText = splashText;
        this.matrices = matrices;
        this.titleScreen = titleScreen;
        this.tickDelta = tickDelta;
    }

    public String getSplashText() {
        return this.splashText;
    }

    public void setSplashText(String splashText) {
        this.splashText = splashText;
    }

    public MatrixStack getMatrices() {
        return this.matrices;
    }

    public TitleScreen getTitleScreen() {
        return this.titleScreen;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    @Override
    public abstract void interact();

    @HasResult
    public static class Pre extends EventRenderSplashText {
        private int splashTextColor;

        public Pre(String splashText, MatrixStack matrices, TitleScreen titleScreen, float tickDelta, int splashTextColor) {
            super(splashText, matrices, titleScreen, tickDelta);
            this.splashTextColor = splashTextColor;
        }

        public int getSplashTextColor() {
            return this.splashTextColor;
        }

        public void setSplashTextColor(int splashTextColor) {
            this.splashTextColor = splashTextColor;
        }

        @Override
        public void interact() {
            PRE.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(Pre event);
        }
    }

    @HasResult
    public static class Post extends EventRenderSplashText {
        public Post(String splashText, MatrixStack matrices, TitleScreen titleScreen, float tickDelta) {
            super(splashText, matrices, titleScreen, tickDelta);
        }

        @Override
        public void interact() {
            POST.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(Post event);
        }
    }
}
