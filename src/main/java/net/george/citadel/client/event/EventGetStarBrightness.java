package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.ResultReturningEvent;
import net.george.citadel.api.event.marker.HasResult;
import net.minecraft.client.world.ClientWorld;

@SuppressWarnings("unused")
@HasResult
public class EventGetStarBrightness extends ResultReturningEvent {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> event -> {
                for (Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final ClientWorld world;
    private float brightness;
    private final float tickDelta;

    public EventGetStarBrightness(ClientWorld world, float brightness, float tickDelta) {
        this.world = world;
        this.brightness = brightness;
        this.tickDelta = tickDelta;
    }

    public ClientWorld getWorld() {
        return this.world;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    public float getBrightness() {
        return this.brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context {
        void interact(EventGetStarBrightness event);
    }
}
