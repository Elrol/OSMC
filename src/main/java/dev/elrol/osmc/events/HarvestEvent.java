package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface HarvestEvent {

    Event<HarvestEvent> EVENT = EventFactory.createArrayBacked(HarvestEvent.class, listeners -> (player, state, pos) -> {
        for (HarvestEvent listener : listeners) {
            listener.harvest(player, state, pos);
        }
    });

    void harvest(ServerPlayerEntity player, BlockState state, BlockPos pos);

}
