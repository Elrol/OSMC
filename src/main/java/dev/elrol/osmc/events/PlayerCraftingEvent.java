package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerCraftingEvent {

    Event<PlayerCraftingEvent> EVENT = EventFactory.createArrayBacked(PlayerCraftingEvent.class, (listeners) -> (player, stack, amount) -> {
        for(PlayerCraftingEvent listener : listeners) listener.craft(player, stack, amount);
    });

    void craft(ServerPlayerEntity player, ItemStack stack, int amount);
}
