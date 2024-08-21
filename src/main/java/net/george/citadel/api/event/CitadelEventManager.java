package net.george.citadel.api.event;

@SuppressWarnings("unused")
public enum CitadelEventManager {
    INSTANCE;

    public void send(CitadelEventBase event) {
        event.interact();
    }

    public boolean send(ICancelableEvent event) {
        event.interact();
        return event.isCanceled();
    }

    public EventResult send(IResultReturningEvent event) {
        event.interact();
        return event.getResult();
    }
}
