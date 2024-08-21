package net.george.citadel.server.tick;

import net.george.citadel.Citadel;
import net.george.citadel.server.message.SyncClientTickRateMessage;
import net.george.citadel.server.tick.modifier.TickRateModifier;
import net.george.citadel.server.tick.modifier.TickRateModifierType;
import net.george.citadel.server.world.CitadelServerData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
public class ServerTickRateTracker extends TickRateTracker {
    public static final Logger LOGGER = LogManager.getLogger("citadel-server-tick");

    public MinecraftServer server;

    public ServerTickRateTracker(MinecraftServer server) {
        this.server = server;
    }

    public ServerTickRateTracker(MinecraftServer server, NbtCompound nbt) {
        this(server);
        fromTag(nbt);
    }

    public void addTickRateModifier(TickRateModifier modifier) {
        this.tickRateModifierList.add(modifier);
        sync();
    }

    @Override
    public void tickEntityAtCustomRate(Entity entity) {
        if (!entity.world.isClient && entity.world instanceof ServerWorld) {
            ((ServerWorld)entity.world).tickEntity(entity);
        }
    }

    @Override
    protected void sync() {
        Citadel.sendMSGToAll(new SyncClientTickRateMessage(toTag()));
    }

    public int getServerTickLengthMs() {
        int i = MinecraftServer.field_33206;
        for (TickRateModifier modifier : tickRateModifierList) {
            if (modifier.getType() == TickRateModifierType.GLOBAL) {
                i *= (int) modifier.getTickRateMultiplier();
            }
        }
        if (i <= 0) {
            return 1;
        }
        return i;
    }

    public static ServerTickRateTracker getForServer(MinecraftServer server) {
        return CitadelServerData.get(server).getOrCreateTickRateTracker();
    }

    public static void modifyTickRate(World world, TickRateModifier modifier) {
        if (world instanceof ServerWorld serverLevel) {
            getForServer(serverLevel.getServer()).addTickRateModifier(modifier);
        }
    }
}
