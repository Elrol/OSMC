package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOffer;

public interface VillagerTradeEvent {

    Event<VillagerTradeEvent> EVENT = EventFactory.createArrayBacked(VillagerTradeEvent.class, (listeners) -> (player, merchant, trade) -> {
        for (VillagerTradeEvent listener : listeners) {
            listener.onTrade(player, merchant, trade);
        }
    });

    void onTrade(ServerPlayerEntity player, MerchantEntity merchant, TradeOffer trade);

}
