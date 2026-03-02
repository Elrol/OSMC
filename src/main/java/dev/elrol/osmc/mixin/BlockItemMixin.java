package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.BlockPlaceEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void osmc$place(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = BlockPlaceEvent.PRE.invoker().preplace(context);
        if(result == ActionResult.FAIL) cir.setReturnValue(ActionResult.FAIL);
    }

    @Inject(method = "postPlacement", at = @At("RETURN"))
    private void osmc$postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if(cir.getReturnValue()) {
            BlockPlaceEvent.POST.invoker().postplace(pos, world, player, stack, state);
        }
    }

}
