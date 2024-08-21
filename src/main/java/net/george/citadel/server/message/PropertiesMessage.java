package net.george.citadel.server.message;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.george.citadel.server.entity.CitadelEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class PropertiesMessage implements C2SPacket {
    private final String propertyId;
    private final NbtCompound nbt;
    private final int entityId;

    public PropertiesMessage(String propertyId, NbtCompound nbt, int entityId) {
        this.propertyId = propertyId;
        this.nbt = nbt;
        this.entityId = entityId;
    }

    public static PropertiesMessage decode(PacketByteBuf buf) {
        return new PropertiesMessage(PacketBufferUtils.readUTF8String(buf), PacketBufferUtils.readTag(buf), buf.readInt());
    }

    @Override
    public void encode(PacketByteBuf buf) {
        PacketBufferUtils.writeUTF8String(buf, this.propertyId);
        PacketBufferUtils.writeTag(buf, this.nbt);
        buf.writeInt(this.entityId);
    }

    @Override
    public void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler listener, PacketSender responseSender, SimpleChannel channel) {
        server.execute(() -> {
            Entity entity = player.world.getEntityById(this.entityId);
            if (entity instanceof LivingEntity && (this.propertyId.equals("CitadelPatreonConfig") || this.propertyId.equals("CitadelTagUpdate"))) {
                CitadelEntityData.setCitadelTag((LivingEntity) entity, this.nbt);
            }
        });
    }
}
