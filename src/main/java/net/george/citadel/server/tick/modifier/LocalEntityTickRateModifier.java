package net.george.citadel.server.tick.modifier;

import net.george.citadel.server.entity.IModifiesTime;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class LocalEntityTickRateModifier extends LocalTickRateModifier {
    private int entityId;
    private EntityType<?> expectedEntityType;
    private final boolean isEntityValid = true;

    public LocalEntityTickRateModifier(int entityId, EntityType<?> expectedEntityType, double range, RegistryKey<World> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.LOCAL_ENTITY, range, dimension, durationInMasterTicks, tickRateMultiplier);
        this.entityId = entityId;
        this.expectedEntityType = expectedEntityType;
    }

    public LocalEntityTickRateModifier(NbtCompound nbt) {
        super(nbt);
        this.entityId = nbt.getInt("EntityId");
        EntityType<?> type = Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("EntityType")));
        this.expectedEntityType = type == null ? EntityType.PIG : type;
    }

    @Override
    public Vec3d getCenter(World world) {
        Entity entity = world.getEntityById(this.entityId);
        if (isEntityValid(world) && entity != null) {
            return entity.getPos();
        }
        return Vec3d.ZERO;
    }

    @Override
    public boolean appliesTo(World world, double x, double y, double z) {
        return super.appliesTo(world, x, y, z) && isEntityValid(world);
    }

    public boolean isEntityValid(World world) {
        Entity entity = world.getEntityById(this.entityId);
        return entity != null && entity.getType().equals(this.expectedEntityType) && entity.isAlive() && (!(entity instanceof IModifiesTime) || ((IModifiesTime)entity).isTimeModificationValid(this));
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = super.toTag();
        nbt.putInt("EntityId", this.entityId);
        Identifier id = Registry.ENTITY_TYPE.getId(this.expectedEntityType);
        if (id != null) {
            nbt.putString("EntityType", id.toString());
        }
        return nbt;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public EntityType<?> getExpectedEntityType() {
        return this.expectedEntityType;
    }

    public void setExpectedEntityType(EntityType<?> expectedEntityType) {
        this.expectedEntityType = expectedEntityType;
    }
}
