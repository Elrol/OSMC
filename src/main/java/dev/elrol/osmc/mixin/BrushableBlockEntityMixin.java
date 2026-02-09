package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.BlockBrushEvent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrushableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrushableBlockEntity.class)
public abstract class BrushableBlockEntityMixin extends BlockEntity {

    public BrushableBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "finishBrushing", at = @At("TAIL"))
    public void osmc$finishBrushing(PlayerEntity player, CallbackInfo ci) {
        if(world != null && world.getServer() != null && player instanceof ServerPlayerEntity serverPlayer) {
            BlockBrushEvent.EVENT.invoker().brush(serverPlayer, getCachedState());
        }
    }

}
