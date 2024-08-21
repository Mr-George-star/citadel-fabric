package net.george.citadel.animation;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.CancelableEvent;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.api.event.marker.Cancelable;

@SuppressWarnings("unused")
public abstract class AnimationEvent extends CancelableEvent {
    public static final Event<Start.Context> START = EventFactory.createArrayBacked(Start.Context.class,
            (listeners) -> event -> {
                for (Start.Context callback : listeners) {
                    CitadelEventManager.INSTANCE.send(event);
                }
            });
    public static final Event<Tick.Context> TICK = EventFactory.createArrayBacked(Tick.Context.class,
            (listeners) -> event -> {
                for (Tick.Context callback : listeners) {
                    CitadelEventManager.INSTANCE.send(event);
                }
            });

    protected Animation animation;
    private final IAnimatedEntity entity;

    AnimationEvent(IAnimatedEntity entity, Animation animation) {
        this.entity = entity;
        this.animation = animation;
    }

    public IAnimatedEntity getEntity() {
        return this.entity;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    @Cancelable
    public static class Start extends AnimationEvent {
        public Start(IAnimatedEntity entity, Animation animation) {
            super(entity, animation);
        }

        public void setAnimation(Animation animation) {
            this.animation = animation;
        }

        @Override
        public void interact() {
            START.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(Start event);
        }
    }

    public static class Tick extends AnimationEvent {
        protected int tick;

        public Tick(IAnimatedEntity entity, Animation animation, int tick) {
            super(entity, animation);
            this.tick = tick;
        }

        public int getTick() {
            return this.tick;
        }

        @Override
        public void interact() {
            TICK.invoker().interact(this);
        }

        @FunctionalInterface
        public interface Context {
            void interact(Tick event);
        }
    }
}
