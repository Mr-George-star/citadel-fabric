package net.george.citadel;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.george.citadel.api.event.CitadelEventManager;
import net.george.citadel.server.entity.IDancesToJukebox;
import net.george.citadel.server.event.EventChangeEntityTickRate;
import net.george.citadel.server.tick.ServerTickRateTracker;
import net.george.citadel.server.world.CitadelServerData;
import net.george.citadel.server.world.ModifiableTickRateServer;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class ServerHandler {
    public static ServerHandler HANDLER = new ServerHandler();

    public void onPreInit() {
        onServerTick();
    }

    public void onServerTick() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            ServerTickRateTracker tickRateTracker = CitadelServerData.get(server).getOrCreateTickRateTracker();
            if (server instanceof ModifiableTickRateServer modifiableServer) {
                long l = tickRateTracker.getServerTickLengthMs();
                if (l == MinecraftServer.field_33206) {
                    modifiableServer.resetGlobalTickLengthMs();
                } else {
                    modifiableServer.setGlobalTickLengthMs(tickRateTracker.getServerTickLengthMs());
                }
                tickRateTracker.masterTick();
            }
        });
    }

    public void handleJukeboxPacket(World world, int entityId, BlockPos jukeBox, boolean dancing) {
        Entity entity = world.getEntityById(entityId);
        if (entity instanceof IDancesToJukebox dancer) {
            dancer.setDancing(dancing);
            dancer.setJukeboxPos(dancing ? jukeBox : null);
        }
    }

    public boolean canEntityTickServer(World world, Entity entity) {
        if (world instanceof ServerWorld) {
            ServerTickRateTracker tracker = ServerTickRateTracker.getForServer(((ServerWorld) world).getServer());
            if (tracker.isTickingHandled(entity)) {
                return false;
            } else if (!tracker.hasNormalTickRate(entity)) {
                EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
                if (CitadelEventManager.INSTANCE.send(event)) {
                    return true;
                } else {
                    tracker.addTickBlockedEntity(entity);
                    return false;
                }
            }
        }
        return true;
    }
}
