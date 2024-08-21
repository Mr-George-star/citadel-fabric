package net.george.citadel.server.message;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.george.citadel.ClientHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;

public class SyncClientTickRateMessage implements S2CPacket {
    private final NbtCompound nbt;

    public SyncClientTickRateMessage(NbtCompound nbt) {
        this.nbt = nbt;
    }

    public static SyncClientTickRateMessage decode(PacketByteBuf buf) {
        return new SyncClientTickRateMessage(PacketBufferUtils.readTag(buf));
    }

    @Override
    public void encode(PacketByteBuf buf) {
        PacketBufferUtils.writeTag(buf, this.nbt);
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> ClientHandler.HANDLER.handleClientTickRatePacket(this.nbt));
    }
}
