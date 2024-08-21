package net.george.citadel.server.tick.modifier;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class GlobalTickRateModifier extends TickRateModifier {
    public GlobalTickRateModifier(int durationInMasterTicks, float tickRateMultiplier) {
        super(TickRateModifierType.GLOBAL, durationInMasterTicks, tickRateMultiplier);
    }

    public GlobalTickRateModifier(NbtCompound nbt) {
        super(nbt);
    }

    @Override
    public boolean appliesTo(World world, double x, double y, double z) {
        return true;
    }
}
