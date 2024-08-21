package net.george.citadel.api.event;

import net.george.citadel.api.event.marker.Cancelable;

@SuppressWarnings("unused")
public interface ICancelableEvent extends CitadelEventBase {
    /**
     * @return Gets whether this event has been canceled.
     */
    boolean isCanceled();

    /**
     * Sets whether this event is canceled.
     * @param canceled the new canceled state
     */
    void setCanceled(boolean canceled);

    /**
     * Gets whether this event is annotated as {@link Cancelable cancelable}.
     * @return whether it is annotated as {@link Cancelable cancelable}.
     */
    boolean isCancelable();

    @Override
    void interact();
}
