package net.george.citadel.client.rewards;

import net.george.citadel.server.entity.CitadelEntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class CitadelCapes {
    private static final List<Cape> CAPES = new ArrayList<>();
    private static final Map<UUID, Boolean> HAS_CAPES_ENABLED = new LinkedHashMap<>();

    public static void addCapeFor(List<UUID> uuids, String translationKey, Identifier texture) {
        CAPES.add(new Cape(uuids, translationKey, texture));
    }

    public static List<Cape> getCapesFor(UUID uuid){
        return CAPES.isEmpty() ? CAPES : CAPES.stream().filter(cape -> cape.isFor(uuid)).toList();
    }

    public static Cape getNextCape(String currentID, UUID playerUUID) {
        if (CAPES.isEmpty()) {
            return null;
        }
        int currentIndex = -1;
        for (int i = 0; i < CAPES.size(); i++) {
            if (CAPES.get(i).getIdentifier().equals(currentID)) {
                currentIndex = i;
                break;
            }
        }
        boolean flag = false;
        for (int i = currentIndex + 1; i < CAPES.size(); i++) {
            if (CAPES.get(i).isFor(playerUUID)) {
                return CAPES.get(i);
            }
        }
        return null;
    }

    @Nullable
    public static Cape getById(String identifier) {
        for (Cape cape : CAPES) {
            if (cape.getIdentifier().equals(identifier)) {
                return cape;
            }
        }
        return null;
    }

    @Nullable
    private static Cape getFirstApplicable(PlayerEntity player) {
        for (Cape cape : CAPES) {
            if (cape.isFor(player.getUuid())) {
                return cape;
            }
        }
        return null;
    }

    public static Cape getCurrentCape(PlayerEntity player) {
        NbtCompound nbt = CitadelEntityData.getOrCreateCitadelTag(player);
        if (nbt.getBoolean("CitadelCapeDisabled")) {
            return null;
        }
        if (nbt.contains("CitadelCapeType")) {
            if (nbt.getString("CitadelCapeType").isEmpty()) {
                return getFirstApplicable(player);
            } else {
                return CitadelCapes.getById(nbt.getString("CitadelCapeType"));
            }
        } else {
            return null;
        }
    }

    public static class Cape {
        private final List<UUID> isFor;
        private final String identifier;
        private final Identifier texture;

        public Cape(List<UUID> isFor, String identifier, Identifier texture) {
            this.isFor = isFor;
            this.identifier = identifier;
            this.texture = texture;
        }

        public List<UUID> getIsFor() {
            return this.isFor;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public Identifier getTexture() {
            return this.texture;
        }

        public boolean isFor(UUID uuid) {
            return this.isFor.contains(uuid);
        }
    }
}
