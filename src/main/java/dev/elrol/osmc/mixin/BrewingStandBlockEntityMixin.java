package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.PlayerBrewingEvent;
import dev.elrol.osmc.interfaces.BrewingStandExtended;
import dev.elrol.osmc.interfaces.PlayerTrackable;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin implements PlayerTrackable, BrewingStandExtended {

    @Unique
    private UUID lastPlayer;

    @Unique
    private final List<ItemStack> baseItems = new ArrayList<>();

    @Unique
    public void osmc$setLastPlayer(UUID uuid) { lastPlayer = uuid; }

    @Unique
    public void osmc$clearLastPlayer() { lastPlayer = null; }

    @Unique
    public UUID osmc$getLastPlayer() { return lastPlayer; }

    @Inject(method = "craft", at = @At("HEAD"))
    private static void osmc$craftHead(World world, BlockPos pos, DefaultedList<ItemStack> slots, CallbackInfo ci) {
        if(world.getBlockEntity(pos) instanceof BrewingStandExtended extended) {
            extended.osmc$setBaseItems(slots);
        }
    }

    @Inject(method = "craft", at = @At("TAIL"))
    private static void osmc$craftTail(World world, BlockPos pos, DefaultedList<ItemStack> slots, CallbackInfo ci) {
        if(world.getBlockEntity(pos) instanceof PlayerTrackable tracker && world.getBlockEntity(pos) instanceof BrewingStandExtended extended) {
            UUID brewerOwner = tracker.osmc$getLastPlayer();

            if(brewerOwner == null) return;

            List<ItemStack> baseItems = extended.osmc$getBaseItems();

            List<ItemStack> basePotions = new ArrayList<>();
            List<ItemStack> resultPotions = new ArrayList<>();
            for(int i = 0; i < 3; i++) {
                basePotions.add(baseItems.get(i));
                resultPotions.add(slots.get(i));
            }
            PlayerBrewingEvent.EVENT.invoker().onBrew(brewerOwner, baseItems.get(3), basePotions, resultPotions);
        }
    }

    @Inject(method = "createScreenHandler", at = @At("HEAD"))
    public void osmc$createScreenHandler(int syncId, PlayerInventory playerInventory, CallbackInfoReturnable<ScreenHandler> cir) {
        osmc$setLastPlayer(playerInventory.player.getUuid());
    }

    @Override
    public void osmc$setBaseItems(DefaultedList<ItemStack> potions) {
        baseItems.clear();
        baseItems.addAll(potions);
    }

    @Override
    public List<ItemStack> osmc$getBaseItems() { return baseItems; }
}
