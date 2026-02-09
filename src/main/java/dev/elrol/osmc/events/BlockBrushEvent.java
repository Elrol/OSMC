package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

public interface BlockBrushEvent {

    Event<BlockBrushEvent> EVENT = EventFactory.createArrayBacked(BlockBrushEvent.class, (listeners) -> (player, state) -> {
        for (BlockBrushEvent listener : listeners) {
            listener.brush(player, state);
        }
    });

    void brush(ServerPlayerEntity player, BlockState state);

}
