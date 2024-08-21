package net.george.citadel.server.capability;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;

import java.util.IdentityHashMap;

@SuppressWarnings("unused")
public enum CapabilityManager {
    INSTANCE;

    static final Logger LOGGER = LogManager.getLogger();
    private final IdentityHashMap<String, Capability<?>> providers = new IdentityHashMap<>();

    CapabilityManager() {
    }

    public static <T> Capability<T> get(CapabilityToken<T> type) {
        return INSTANCE.get(type.getType(), false);
    }

    @SuppressWarnings({"unchecked", "SynchronizationOnLocalVariableOrMethodParameter", "SameParameterValue"})
    <T> Capability<T> get(String realName, boolean registering) {
        Capability<T> capability;
        synchronized (this.providers) {
            realName = realName.intern();
            capability = (Capability<T>) this.providers.computeIfAbsent(realName, Capability::new);
        }

        if (registering) {
            synchronized (capability) {
                if (capability.isRegistered()) {
                    LOGGER.error(MarkerManager.getMarker("CAPABILITIES"), "Cannot register capability implementation multiple times : {}", realName);
                    throw new IllegalArgumentException("Cannot register a capability implementation multiple times : " + realName);
                }

                capability.onRegister();
            }
        }

        return capability;
    }
}
