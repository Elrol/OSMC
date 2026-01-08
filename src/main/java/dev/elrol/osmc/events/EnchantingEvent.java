package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public interface EnchantingEvent {

    Event<EnchantingEvent> EVENT = EventFactory.createArrayBacked(EnchantingEvent.class, (listeners) -> (player, before, after, xpSpent) -> {
        for (EnchantingEvent listener : listeners) {
            listener.enchanted(player, before, after, xpSpent);
        }
    });

    void enchanted(ServerPlayerEntity player, ItemStack enchantedItem, int enchantPower, int xpSpent);
}
