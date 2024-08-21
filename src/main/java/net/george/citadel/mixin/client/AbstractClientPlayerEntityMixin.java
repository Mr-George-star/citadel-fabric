package net.george.citadel.mixin.client;

import com.mojang.authlib.GameProfile;
import net.george.citadel.CitadelConstants;
import net.george.citadel.client.rewards.CitadelCapes;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("DefaultAnnotationParam")
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {
    public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, pos, yaw, gameProfile, publicKey);
    }

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "getCapeTexture", cancellable = true)
    private void citadel$getCapeTexture(CallbackInfoReturnable<Identifier> cir) {
        CitadelCapes.Cape cape = CitadelCapes.getCurrentCape(this);
        if (cape != null) {
            cir.setReturnValue(cape.getTexture());
        }
    }

    @Inject(at = @At("HEAD"), remap = CitadelConstants.REMAPREFS, method = "getElytraTexture", cancellable = true)
    private void citadel$getElytraTexture(CallbackInfoReturnable<Identifier> cir) {
        CitadelCapes.Cape cape = CitadelCapes.getCurrentCape(this);
        if(cape != null){
            cir.setReturnValue(cape.getTexture());
        }
    }
}
