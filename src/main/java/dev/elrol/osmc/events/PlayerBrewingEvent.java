package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.UUID;

public interface PlayerBrewingEvent {

    Event<PlayerBrewingEvent> EVENT = EventFactory.createArrayBacked(PlayerBrewingEvent.class, (listeners) -> ((uuid, ingredient, beforePotion, afterPotion) -> {
        for (PlayerBrewingEvent listener : listeners) {
            listener.onBrew(uuid, ingredient, beforePotion, afterPotion);
        }
    }));

    void onBrew(UUID uuid, ItemStack ingredient,List<ItemStack> beforePotion, List<ItemStack> afterPotion);

}
