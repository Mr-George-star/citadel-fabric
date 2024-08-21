package net.george.citadel.server.tick.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class CelestialTickRateModifier extends TickRateModifier {
    public CelestialTickRateModifier(int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.CELESTIAL, durationInMasterTicks, tickRateMultiplier);
    }

    public CelestialTickRateModifier(NbtCompound nbt) {
        super(nbt);
    }

    @Override
    public boolean appliesTo(World world, double x, double y, double z) {
        return false;
    }
}
