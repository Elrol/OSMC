package dev.elrol.osmc.events;


import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public interface SkillLevelUpEvent {

    Event<SkillLevelUpEvent> EVENT = EventFactory.createArrayBacked(SkillLevelUpEvent.class, (listeners) -> (player, skillID, level) -> {
        for(SkillLevelUpEvent listener : listeners) {
            listener.onLevelUp(player, skillID, level);
        }
    });

    void onLevelUp(ServerPlayerEntity player, Identifier skillID, int level);

}
