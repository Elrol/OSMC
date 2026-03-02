package dev.elrol.osmc.mixin;

import com.cobblemon.mod.common.block.BerryBlock;
import dev.elrol.osmc.events.HarvestEvent;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BerryBlock.class)
public class BlockBerryMixin {

    @Inject(method = "harvestBerry", at = @At("RETURN"))
    private void osmc$harvestBerry(World world, BlockState state, BlockPos pos, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(player instanceof ServerPlayerEntity serverPlayer) {
            HarvestEvent.EVENT.invoker().harvest(serverPlayer, state, pos);
        }
    }

}
