package net.george.citadel.server.tick.modifier;

import net.george.citadel.Citadel;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public abstract class TickRateModifier {
    private final TickRateModifierType type;
    private float maxDuration;
    private float duration;
    private float tickRateMultiplier;

    public TickRateModifier(TickRateModifierType type, int maxDuration, float tickRateMultiplier) {
        this.type = type;
        this.maxDuration = maxDuration;
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public TickRateModifier(NbtCompound nbt) {
        this.type = TickRateModifierType.fromId(nbt.getInt("TickRateType"));
        this.maxDuration = nbt.getFloat("MaxDuration");
        this.duration = nbt.getFloat("Duration");
        this.tickRateMultiplier = nbt.getFloat("SpeedMultiplier");
    }

    public TickRateModifierType getType() {
        return this.type;
    }

    public float getMaxDuration() {
        return this.maxDuration;
    }

    public float getTickRateMultiplier() {
        return this.tickRateMultiplier;
    }

    public void setMaxDuration(float maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setTickRateMultiplier(float tickRateMultiplier) {
        this.tickRateMultiplier = tickRateMultiplier;
    }

    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();
        tag.putInt("TickRateType", this.type.toId());
        tag.putFloat("MaxDuration", this.maxDuration);
        tag.putFloat("Duration", this.duration);
        tag.putFloat("SpeedMultiplier", this.tickRateMultiplier);
        return tag;
    }

    public static TickRateModifier fromTag(NbtCompound nbt) {
        TickRateModifierType typeFromNbt = TickRateModifierType.fromId(nbt.getInt("TickRateType"));
        try {
            return typeFromNbt.getTickRateClass().getConstructor(NbtCompound.class).newInstance(nbt);
        } catch (Exception exception) {
            Citadel.LOGGER.catching(exception);
        }
        return null;
    }

    public boolean isGlobal() {
        return this.type.isLocal();
    }

    public void masterTick() {
        this.duration++;
    }

    public boolean doRemove() {
        float f = this.tickRateMultiplier == 0 || this.getType() == TickRateModifierType.CELESTIAL ? 1.0F : 1F / this.tickRateMultiplier;
        return this.duration >= this.maxDuration * f;
    }

    public abstract boolean appliesTo(World world, double x, double y, double z);
}
