package net.george.citadel.api.event;

import java.lang.reflect.Type;

@SuppressWarnings("unused")
public interface IGenericEvent<T> extends CitadelEventBase {
    Type getGenericType();

    @Override
    void interact();
}
