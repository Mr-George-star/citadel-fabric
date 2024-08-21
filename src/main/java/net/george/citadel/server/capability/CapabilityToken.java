package net.george.citadel.server.capability;

@SuppressWarnings("unused")
public abstract class CapabilityToken<T> {
    public CapabilityToken() {
    }

    protected abstract String getType();

    public String toString() {
        return "CapabilityToken[" + this.getType() + "]";
    }
}
