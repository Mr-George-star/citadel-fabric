package net.george.citadel.server.world;

@SuppressWarnings("unused")
public interface ModifiableTickRateServer {
    void setGlobalTickLengthMs(long msPerTick);

    long getMasterMs();

    default void resetGlobalTickLengthMs(){
        setGlobalTickLengthMs(-1);
    }
}
