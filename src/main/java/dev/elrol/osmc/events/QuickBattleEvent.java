package dev.elrol.osmc.events;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import quick.battle.battle.QuickBattleResult;

public interface QuickBattleEvent {

    Event<QuickBattleEvent> EVENT = EventFactory.createArrayBacked(QuickBattleEvent.class, listeners -> (attacker, defender, result) -> {
        for(QuickBattleEvent listener : listeners) {
            listener.battleEnd(attacker, defender, result);
        }
    });

    void battleEnd(Pokemon attacker, Pokemon defender, QuickBattleResult result);

}
