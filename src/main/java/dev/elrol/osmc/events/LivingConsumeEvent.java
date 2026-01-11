package dev.elrol.osmc.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;

public class LivingConsumeEvent {

    public static final Event<ConsumeFoodCallback> FOOD = EventFactory.createArrayBacked(ConsumeFoodCallback.class,
            (listeners) -> (living, food) -> {
                for(ConsumeFoodCallback listener : listeners) {
                    listener.consumed(living, food);
                }
            }
    );

    public static final Event<ConsumePotionCallback> POTION = EventFactory.createArrayBacked(ConsumePotionCallback.class,
            (listeners) -> (living, item, potion) -> {
                for (ConsumePotionCallback listener : listeners) {
                    listener.consumed(living, item, potion);
                }
            });

    @FunctionalInterface
    public interface ConsumeFoodCallback {
        void consumed(LivingEntity entity, ItemStack stack);
    }

    @FunctionalInterface
    public interface ConsumePotionCallback {
        void consumed(LivingEntity entity, ItemStack stack, PotionItem potion);
    }

}
