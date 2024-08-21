package net.george.citadel.api.event;

import net.george.citadel.api.event.marker.HasResult;

/**
 * A base event class, an event extended from this can have a result. <br>
 * @apiNote The event extended from this class must be annotated as {@link HasResult has result}, otherwise this class won't work.
 * @author Mr.George
 */
public abstract class ResultReturningEvent implements IResultReturningEvent {
    protected EventResult result = EventResult.ALLOW;

    public ResultReturningEvent() {}

    @Override
    public EventResult getResult() {
        return this.result;
    }

    @Override
    public void setResult(EventResult result) {
        this.result = result;
    }

    @Override
    public boolean hasResult() {
        return this.getClass().isAnnotationPresent(HasResult.class);
    }

    @Override
    public abstract void interact();
}
