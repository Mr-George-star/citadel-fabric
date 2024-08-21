package net.george.citadel.api.event;

import net.george.citadel.api.event.marker.HasResult;

@SuppressWarnings("unused")
public interface IResultReturningEvent extends CitadelEventBase {
    /**
     * @return Get the {@link EventResult result} after event interaction.
     */
    EventResult getResult();

    /**
     * Set the {@link EventResult result} after event interaction.
     * @param result new event result.
     */
    void setResult(EventResult result);

    /**
     * Gets whether this event is annotated as {@link HasResult has result}.
     * @return whether it is annotated as {@link HasResult has result}.
     */
    boolean hasResult();

    @Override
    void interact();
}
