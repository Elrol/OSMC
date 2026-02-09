package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface LootContextBuildEvent {

    Event<LootContextBuildEvent> EVENT = EventFactory.createArrayBacked(LootContextBuildEvent.class, (listeners) -> (player, tableID) -> {
        for (LootContextBuildEvent listener : listeners) {
            listener.build(player, tableID);
        }
    });

    void build(ServerPlayerEntity player, Identifier tableID);

}
