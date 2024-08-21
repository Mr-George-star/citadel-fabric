package net.george.citadel.server.tick.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class LocalPositionTickRateModifier extends LocalTickRateModifier {
    private Vec3d center;

    public LocalPositionTickRateModifier(Vec3d center, double range, RegistryKey<World> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.LOCAL_POSITION, range, dimension, durationInMasterTicks, tickRateMultiplier);
        this.center = center;
    }

    public LocalPositionTickRateModifier(NbtCompound nbt) {
        super(nbt);
        this.center = new Vec3d(nbt.getDouble("CenterX"), nbt.getDouble("CenterY"), nbt.getDouble("CenterZ"));
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = super.toTag();
        nbt.putDouble("CenterX", this.center.x);
        nbt.putDouble("CenterY", this.center.y);
        nbt.putDouble("CenterZ", this.center.z);
        return nbt;
    }

    public Vec3d getCenter() {
        return this.center;
    }

    @Override
    public Vec3d getCenter(World world) {
        return getCenter();
    }

    public void setCenter(Vec3d center) {
        this.center = center;
    }
}
