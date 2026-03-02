package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public final class BlockPlaceEvent {

    public static final Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, (listeners) -> (context) -> {
        for (Pre listener : listeners) {
            ActionResult result = listener.preplace(context);
            if(result != ActionResult.PASS) return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    });

    public static final Event<Post> POST = EventFactory.createArrayBacked(Post.class, (listeners) -> (pos, world, player, stack, state) -> {
        for (Post listener : listeners) {
            listener.postplace(pos, world, player, stack, state);
        }
    });

    @FunctionalInterface
    public interface Pre {
        ActionResult preplace(ItemPlacementContext context);
    }

    @FunctionalInterface
    public interface Post {
        void postplace(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state);
    }

}
