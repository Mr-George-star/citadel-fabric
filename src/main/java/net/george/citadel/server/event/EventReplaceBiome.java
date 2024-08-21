package net.george.citadel.server.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.george.citadel.api.event.ResultReturningEvent;
import net.george.citadel.api.event.marker.HasResult;
import net.george.citadel.server.world.ExpandedBiomeSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

@SuppressWarnings("unused")
@HasResult
public class EventReplaceBiome extends ResultReturningEvent {
    public static final Event<Context> EVENT = EventFactory.createArrayBacked(Context.class,
            (listeners) -> event -> {
                for (Context callback : listeners) {
                    callback.interact(event);
                }
            });
    public RegistryEntry<Biome> biomeToGenerate;
    public ExpandedBiomeSource biomeSource;
    public float continentalness;
    public float erosion;
    public float temperature;
    public float humidity;
    public float weirdness;
    public float depth;

    private final int x;
    private final int y;
    private final int z;

    public EventReplaceBiome(ExpandedBiomeSource biomeSource, RegistryEntry<Biome> biome, int x, int y, int z, float continentalness, float erosion, float temperature, float humidity, float weirdness, float depth) {
        this.biomeSource = biomeSource;
        this.biomeToGenerate = biome;
        this.continentalness = continentalness;
        this.erosion = erosion;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weirdness = weirdness;
        this.depth = depth;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RegistryEntry<Biome> getBiomeToGenerate() {
        return this.biomeToGenerate;
    }

    public float getContinentalness() {
        return this.continentalness;
    }

    public float getErosion() {
        return this.erosion;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public float getHumidity() {
        return this.humidity;
    }

    public float getWeirdness() {
        return this.weirdness;
    }

    public float getDepth() {
        return this.depth;
    }

    public boolean testContinentalness(float min, float max) {
        return this.continentalness >= min && this.continentalness <= max;
    }

    public boolean testErosion(float min, float max) {
        return this.erosion >= min && this.erosion <= max;
    }

    public boolean testTemperature(float min, float max) {
        return this.temperature >= min && this.temperature <= max;
    }

    public boolean testHumidity(float min, float max) {
        return this.humidity >= min && this.humidity <= max;
    }

    public boolean testWeirdness(float min, float max) {
        return this.weirdness >= min && this.weirdness <= max;
    }

    public boolean testDepth(float min, float max) {
        return this.depth >= min && this.depth <= max;
    }

    public ExpandedBiomeSource getBiomeSource(){
        return this.biomeSource;
    }

    public void setBiomeToGenerate(RegistryEntry<Biome> biome){
        this.biomeToGenerate = biome;
    }

    public BlockPos getSamplePosition(){
        return new BlockPos(this.x, this.y, this.z);
    }

    @Override
    public void interact() {
        EVENT.invoker().interact(this);
    }

    @FunctionalInterface
    public interface Context {
        void interact(EventReplaceBiome event);
    }
}
