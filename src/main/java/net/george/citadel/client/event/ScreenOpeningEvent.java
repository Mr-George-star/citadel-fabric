package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.CancelableEvent;
import net.george.citadel.api.event.marker.Cancelable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@Cancelable
@SuppressWarnings("unused")
public class ScreenOpeningEvent extends CancelableEvent {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> context -> {
                for (Context callback : listeners) {
                    callback.interact(context);
                }
            });

    private final @Nullable Screen currentScreen;
    private Screen newScreen;

    @ApiStatus.Internal
    public ScreenOpeningEvent(@Nullable Screen currentScreen, Screen screen) {
        super();
        this.currentScreen = currentScreen;
        this.newScreen = screen;
    }

    public @Nullable Screen getCurrentScreen() {
        return this.currentScreen;
    }

    public @Nullable Screen getNewScreen() {
        return this.newScreen;
    }

    public void setNewScreen(Screen newScreen) {
        this.newScreen = newScreen;
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context {
        void interact(ScreenOpeningEvent context);
    }
}
