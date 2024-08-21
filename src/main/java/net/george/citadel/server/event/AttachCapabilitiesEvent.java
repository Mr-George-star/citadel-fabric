package net.george.citadel.server.event;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.GenericEvent;
import net.george.citadel.server.capability.CapabilityDispatcher;
import net.george.citadel.server.capability.ICapabilityProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked", "unused"})
public class AttachCapabilitiesEvent<T> extends GenericEvent<T> {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> event -> {
                for (Context callback : listeners) {
                    callback.interact(event);
                }
            });
    private final T object;
    private final Map<Identifier, ICapabilityProvider> capabilities = Maps.newLinkedHashMap();
    private final Map<Identifier, ICapabilityProvider> view;
    private final List<Runnable> listeners;
    private final List<Runnable> listenersView;

    public AttachCapabilitiesEvent(Class<T> type, T object) {
        super(type);
        this.view = Collections.unmodifiableMap(this.capabilities);
        this.listeners = Lists.newArrayList();
        this.listenersView = Collections.unmodifiableList(this.listeners);
        this.object = object;
    }

    public T getObject() {
        return this.object;
    }

    public void addCapability(Identifier key, ICapabilityProvider capability) {
        if (this.capabilities.containsKey(key)) {
            throw new IllegalStateException("Duplicate Capability Key: " + key + " " + capability);
        } else {
            this.capabilities.put(key, capability);
        }
    }

    public Map<Identifier, ICapabilityProvider> getCapabilities() {
        return this.view;
    }

    public void addListener(Runnable listener) {
        this.listeners.add(listener);
    }

    public List<Runnable> getListeners() {
        return this.listenersView;
    }

    public static <T extends ICapabilityProvider> @Nullable CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider) {
        return gatherCapabilities(type, provider, null);
    }

    public static <T extends ICapabilityProvider> @Nullable CapabilityDispatcher gatherCapabilities(Class<? extends T> type, T provider, @Nullable ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent(type, provider), parent);
    }

    private static @Nullable CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event, @Nullable ICapabilityProvider parent) {
        event.interact();
        return event.getCapabilities().isEmpty() && parent == null ? null : new CapabilityDispatcher(event.getCapabilities(), event.getListeners(), parent);
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context<T> {
        void interact(AttachCapabilitiesEvent<T> event);
    }
}
