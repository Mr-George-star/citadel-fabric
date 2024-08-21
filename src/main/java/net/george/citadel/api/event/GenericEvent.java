package net.george.citadel.api.event;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public abstract class GenericEvent<T> implements IGenericEvent<T> {
    private Class<T> type;

    public GenericEvent() {
    }

    protected GenericEvent(Class<T> type) {
        this.type = type;
    }

    @Override
    public Type getGenericType() {
        return this.type;
    }

    @Override
    public abstract void interact();
}
