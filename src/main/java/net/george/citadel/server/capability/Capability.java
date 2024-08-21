package net.george.citadel.server.capability;

import net.george.citadel.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Capability<T> {
    private final String name;
    List<Consumer<Capability<T>>> listeners = new ArrayList<>();

    public String getName() {
        return this.name;
    }

    public <V> @NotNull LazyOptional<V> orEmpty(Capability<V> toCheck, LazyOptional<T> inst) {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    public boolean isRegistered() {
        return this.listeners == null;
    }

    public synchronized Capability<T> addListener(Consumer<Capability<T>> listener) {
        if (this.isRegistered()) {
            listener.accept(this);
        } else {
            this.listeners.add(listener);
        }

        return this;
    }

    Capability(String name) {
        this.name = name;
    }

    void onRegister() {
        List<Consumer<Capability<T>>> listeners = this.listeners;
        this.listeners = null;
        listeners.forEach(listener -> listener.accept(this));
    }
}
