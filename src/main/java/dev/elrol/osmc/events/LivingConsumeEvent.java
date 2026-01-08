package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class LivingConsumeEvent {

    public static final Event<ConsumeFoodCallback> FOOD = EventFactory.createArrayBacked(ConsumeFoodCallback.class,
            (listeners) -> (living, food) -> {
                for(ConsumeFoodCallback listener : listeners) {
                    listener.consumed(living, food);
                }
            }
    );

    public static final Event<ConsumePotionCallback> POTION = EventFactory.createArrayBacked(ConsumePotionCallback.class,
            (listeners) -> (living, potion) -> {
                for (ConsumePotionCallback listener : listeners) {
                    listener.consumed(living, potion);
                }
            });

    @FunctionalInterface
    public interface ConsumeFoodCallback {
        void consumed(LivingEntity entity, ItemStack stack);
    }

    @FunctionalInterface
    public interface ConsumePotionCallback {
        void consumed(LivingEntity entity, ItemStack stack);
    }

}
