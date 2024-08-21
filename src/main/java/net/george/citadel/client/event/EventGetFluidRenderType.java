package net.george.citadel.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.ResultReturningEvent;
import net.george.citadel.api.event.marker.HasResult;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.FluidState;

@SuppressWarnings("unused")
@HasResult
public class EventGetFluidRenderType extends ResultReturningEvent {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> event -> {
                for (Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final FluidState fluidState;
    private RenderLayer renderLayer;

    public EventGetFluidRenderType(FluidState fluidState, RenderLayer renderLayer) {
        this.fluidState = fluidState;
        this.renderLayer = renderLayer;
    }

    public FluidState getFluidState() {
        return this.fluidState;
    }

    public RenderLayer getRenderLayer() {
        return this.renderLayer;
    }

    public void setRenderLayer(RenderLayer renderLayer) {
        this.renderLayer = renderLayer;
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context {
        void interact(EventGetFluidRenderType event);
    }
}
