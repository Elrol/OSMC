package dev.elrol.osmc.mixin;

import dev.elrol.osmc.interfaces.PlayerTrackable;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    UUID owner;

    @Unique
    private ScreenHandler self() { return (ScreenHandler)(Object) this; }

    @Inject(method = "onSlotClick", at = @At("HEAD"))
    private void osmc$onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity playerEntity, CallbackInfo ci) {
        if(playerEntity instanceof ServerPlayerEntity player) {
            owner = player.getUuid();

            if(self() instanceof BrewingStandScreenHandler brewingHandler) {
                Inventory inv = ((BrewingStandScreenHandlerMixin)brewingHandler).getInventory();

                if(inv instanceof BrewingStandBlockEntity blockEntity) {
                    ((PlayerTrackable) blockEntity).osmc$setLastPlayer(owner);
                }
            }
        }
    }


    @Inject(method = "onContentChanged", at = @At("HEAD"))
    private void osmc$onContentChanged(Inventory inventory, CallbackInfo ci) {
        if(self() instanceof BrewingStandScreenHandler brewingHandler) {

        }
    }

}
