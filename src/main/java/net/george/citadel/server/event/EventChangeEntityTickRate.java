package net.george.citadel.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.CancelableEvent;
import net.george.citadel.api.event.marker.Cancelable;
import net.minecraft.entity.Entity;

@SuppressWarnings("unused")
@Cancelable
public class EventChangeEntityTickRate extends CancelableEvent {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> event -> {
                for (Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final Entity entity;
    private final float targetTickRate;

    public EventChangeEntityTickRate(Entity entity, float targetTickRate) {
        this.entity = entity;
        this.targetTickRate = targetTickRate;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public float getTargetTickRate() {
        return this.targetTickRate;
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context {
        void interact(EventChangeEntityTickRate event);
    }
}
