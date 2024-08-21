package net.george.citadel.server.tick.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class LocalTickRateModifier extends TickRateModifier {
    private double range;
    private final RegistryKey<World> dimension;

    public LocalTickRateModifier(TickRateModifierType localPosition, double range, RegistryKey<World> dimension, int durationInMasterTicks, float tickRateMultiplier) {
        super(localPosition, durationInMasterTicks, tickRateMultiplier);
        this.range = range;
        this.dimension = dimension;
    }

    public LocalTickRateModifier(NbtCompound nbt) {
        super(nbt);
        this.range = nbt.getDouble("Range");
        RegistryKey<World> dimension = World.OVERWORLD;
        if(nbt.contains("Dimension")){
            dimension = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("dimension")));
        }
        this.dimension = dimension;
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound nbt = super.toTag();
        nbt.putDouble("Range", this.range);
        nbt.putString("Dimension", this.dimension.getValue().toString());
        return nbt;
    }

    public double getRange() {
        return this.range;
    }

    public void setRange(double range) {
        this.range = range;
    }

    public abstract Vec3d getCenter(World world);

    @Override
    public boolean appliesTo(World world, double x, double y, double z) {
        Vec3d center = getCenter(world);
        return center.squaredDistanceTo(x, y, z) < this.range * this.range;
    }
}
