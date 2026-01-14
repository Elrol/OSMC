package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.PlayerCraftingEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    private ItemStack self() { return (ItemStack)(Object) this; }

    @Inject(method = "onCraftByPlayer", at = @At("TAIL"))
    public void osmc$onCraftedByPlayer(World world, PlayerEntity player, int amount, CallbackInfo ci) {
        if(player instanceof ServerPlayerEntity serverPlayer)
            PlayerCraftingEvent.EVENT.invoker().craft(serverPlayer, self(), amount);
    }

}
