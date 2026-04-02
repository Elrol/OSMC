package dev.elrol.osmc.events;

import dev.elrol.osmc.data.Skill;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;

public final class AbilityEvent {

    public static final Event<Activate> ACTIVATE = EventFactory.createArrayBacked(Activate.class, (listeners) -> (player, skill) -> {
        for (Activate listener : listeners) {
            listener.activate(player, skill);
        }
    });

    public static final Event<Deactivate> DEACTIVATE = EventFactory.createArrayBacked(Deactivate.class, (listeners) -> (player, skill) -> {
        for (Deactivate listener : listeners) {
            listener.deactivate(player, skill);
        }
    });

    @FunctionalInterface
    public interface Activate {
        void activate(ServerPlayerEntity player, Skill skill);
    }

    @FunctionalInterface
    public interface Deactivate {
        void deactivate(ServerPlayerEntity player, Skill skill);
    }

}
