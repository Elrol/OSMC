package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.VillagerTradeEvent;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin {

    @Shadow
    @Nullable
    public abstract PlayerEntity getCustomer();

    @Unique
    private MerchantEntity self() { return (MerchantEntity) (Object) this; }

    @Inject(method = "trade", at = @At("HEAD"))
    private void osmc$trade(TradeOffer offer, CallbackInfo ci){
        if(getCustomer() instanceof ServerPlayerEntity player) {
            VillagerTradeEvent.EVENT.invoker().onTrade(player, self(), offer);
        }
    }

}
