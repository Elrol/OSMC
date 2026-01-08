package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.LivingConsumeEvent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private LivingEntity getThis() { return (LivingEntity)(Object)this; }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    public void osmc$eatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        LivingConsumeEvent.FOOD.invoker().consumed(getThis(), stack.copyWithCount(1));
    }
}
