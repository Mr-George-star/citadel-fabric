package net.george.citadel.server.message;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.george.citadel.ServerHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class DanceJukeboxMessage implements C2SPacket {
    public int entityId;
    public boolean dance;
    public BlockPos jukeBox;

    public DanceJukeboxMessage(int entityId, boolean dance, BlockPos jukeBox) {
        this.entityId = entityId;
        this.dance = dance;
        this.jukeBox = jukeBox;
    }

    public static DanceJukeboxMessage decode(PacketByteBuf buf) {
        return new DanceJukeboxMessage(buf.readInt(), buf.readBoolean(), buf.readBlockPos());
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeBoolean(this.dance);
        buf.writeBlockPos(this.jukeBox);
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        server.execute(() -> {
            if (player != null) {
                ServerHandler.HANDLER.handleJukeboxPacket(player.world, this.entityId, this.jukeBox, this.dance);
            }
        });
    }
}
