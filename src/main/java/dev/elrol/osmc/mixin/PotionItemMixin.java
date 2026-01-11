package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.LivingConsumeEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionItem.class)
public class PotionItemMixin {

    @Unique
    private PotionItem getThis() { return (PotionItem) (Object) this; }

    @Inject(method = "finishUsing", at = @At("RETURN"))
    public void osmc$finishUsing(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        LivingConsumeEvent.POTION.invoker().consumed(user, stack, getThis());
    }
}
