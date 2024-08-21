package net.george.citadel.api.event;

import net.george.citadel.api.event.marker.Cancelable;

/**
 * A base event class, an event extended from this can be canceled. <br>
 * @apiNote The event extended from this class must be annotated as {@link Cancelable cancelable}, otherwise this class won't work.
 * @author Mr.George
 */
@SuppressWarnings("unused")
public abstract class CancelableEvent implements ICancelableEvent {
    protected boolean isCanceled;

    public CancelableEvent() {
        this.isCanceled = false;
    }

    @Override
    public boolean isCanceled() {
        return this.isCanceled;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.isCanceled = canceled;
    }

    @Override
    public boolean isCancelable() {
        return this.getClass().isAnnotationPresent(Cancelable.class);
    }

    @Override
    public abstract void interact();
}
