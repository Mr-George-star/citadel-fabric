package net.george.citadel.server.entity;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;

/**
 * @author Alexthe666
 * @since 1.7.0
 *
 * CitadelTag is a data synced tag for LivingEntity provided by citadel to be used by various mods.
 */
@SuppressWarnings("unused")
public class CitadelEntityData {
    public static NbtCompound getOrCreateCitadelTag(LivingEntity entity) {
        NbtCompound nbt = getCitadelTag(entity);
        return nbt == null ? new NbtCompound() : nbt;
    }

    public static NbtCompound getCitadelTag(LivingEntity entity) {
        return entity instanceof ICitadelDataEntity ? ((ICitadelDataEntity) entity).getCitadelEntityData() : new NbtCompound();
    }

    public static void setCitadelTag(LivingEntity entity, NbtCompound nbt) {
        if(entity instanceof ICitadelDataEntity){
            ((ICitadelDataEntity) entity).setCitadelEntityData(nbt);
        }
    }
}
