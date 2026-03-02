package dev.elrol.osmc.mixin;

import com.cobblemon.mod.common.block.ApricornBlock;
import dev.elrol.osmc.events.HarvestEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ApricornBlock.class)
public class BlockApricornMixin {

    @Inject(method = "onUse", at = @At("RETURN"))
    private void osmc$onUse(BlockState state, World level, BlockPos pos, PlayerEntity player, BlockHitResult blockHitResult, CallbackInfoReturnable<ActionResult> cir) {
        if(player instanceof ServerPlayerEntity serverPlayer) {
            HarvestEvent.EVENT.invoker().harvest(serverPlayer, state, pos);
        }
    }



}
