package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.ResultReturningEvent;
import net.george.citadel.api.event.marker.HasResult;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

@SuppressWarnings("unused")
@HasResult
public class EventPosePlayerHand extends ResultReturningEvent {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> event -> {
                for (Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final LivingEntity entity;
    private final BipedEntityModel<LivingEntity> model;
    private final boolean left;

    public EventPosePlayerHand(LivingEntity entity, BipedEntityModel<LivingEntity> model, boolean left) {
        this.entity = entity;
        this.model = model;
        this.left = left;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public BipedEntityModel<LivingEntity> getModel() {
        return this.model;
    }

    public boolean isLeftHand() {
        return this.left;
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context {
        void interact(EventPosePlayerHand event);
    }
}
