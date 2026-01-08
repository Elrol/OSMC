package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.EnchantingEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {

    @Unique
    private EnchantmentScreenHandler getThis() { return (EnchantmentScreenHandler) (Object) this; }

    @Final @Shadow public int[] enchantmentLevel;

    @Inject(method = "onButtonClick", at = @At(value = "RETURN"))
    private void osmc$onButtonClick(PlayerEntity playerEntity, int id, CallbackInfoReturnable<Boolean> cir) {
        if(playerEntity instanceof ServerPlayerEntity player) {
            if(cir.getReturnValue()) {
                ItemStack result = getThis().getSlot(0).getStack();
                //result.getEnchantments().getEnchantmentEntries()
                EnchantingEvent.EVENT.invoker().enchanted(player, result, enchantmentLevel[id], id + 1);
            }
        }
    }

}
