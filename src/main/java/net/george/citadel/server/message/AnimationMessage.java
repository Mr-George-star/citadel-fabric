package net.george.citadel.server.message;

import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.george.citadel.ClientHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class AnimationMessage implements S2CPacket {
    private final int entityId;
    private final int index;

    public AnimationMessage(int entityId, int index) {
        this.entityId = entityId;
        this.index = index;
    }

    public static AnimationMessage decode(PacketByteBuf buf) {
        return new AnimationMessage(buf.readInt(), buf.readInt());
    }

    @Override
    public void encode(PacketByteBuf buf) {
        buf.writeInt(this.entityId);
        buf.writeInt(this.index);
    }

    @Override
    public void handle(MinecraftClient client, ClientPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        client.execute(() -> ClientHandler.HANDLER.handleAnimationPacket(this.entityId, this.index));
    }
}
