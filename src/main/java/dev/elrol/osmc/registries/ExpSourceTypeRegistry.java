package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.*;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ExpSourceTypeRegistry {

    public static final ExpSourceType<BlockBreakExpSource>              BREAK_EXP_SOURCE =                  register(OSMCConstants.BLOCK_BREAK_EXP_ID,              new ExpSourceType<>(BlockBreakExpSource.CODEC));
    public static final ExpSourceType<BlockInteractionExpSource>        BLOCK_INTERACT_EXP_SOURCE =         register(OSMCConstants.BLOCK_INTERACT_EXP_ID,           new ExpSourceType<>(BlockInteractionExpSource.CODEC));
    public static final ExpSourceType<ConsumeFoodExpSource>             CONSUME_FOOD_EXP_SOURCE =           register(OSMCConstants.CONSUME_FOOD_EXP_ID,             new ExpSourceType<>(ConsumeFoodExpSource.CODEC));
    public static final ExpSourceType<ConsumePotionExpSource>           CONSUME_POTION_EXP_SOURCE =         register(OSMCConstants.CONSUME_POTION_EXP_ID,           new ExpSourceType<>(ConsumePotionExpSource.CODEC));
    public static final ExpSourceType<CraftExpSource>                   CRAFT_EXP_SOURCE =                  register(OSMCConstants.CRAFT_EXP_ID,                    new ExpSourceType<>(CraftExpSource.CODEC));
    public static final ExpSourceType<EnchantExpSource>                 ENCHANT_EXP_SOURCE =                register(OSMCConstants.ENCHANT_EXP_ID,                  new ExpSourceType<>(EnchantExpSource.CODEC));
    public static final ExpSourceType<EntityInteractionExpSource>       ENTITY_INTERACT_EXP_SOURCE =        register(OSMCConstants.ENTITY_INTERACTION_EXP_ID,       new ExpSourceType<>(EntityInteractionExpSource.CODEC));
    public static final ExpSourceType<EntityKillExpSource>              ENTITY_KILL_EXP_SOURCE =            register(OSMCConstants.ENTITY_KILL_EXP_ID,              new ExpSourceType<>(EntityKillExpSource.CODEC));
    public static final ExpSourceType<ItemUseExpSource>                 ITEM_USE_EXP_SOURCE =               register(OSMCConstants.ITEM_USE_EXP_ID,                 new ExpSourceType<>(ItemUseExpSource.CODEC));
    public static final ExpSourceType<PotionBrewExpSource>              POTION_BREW_EXP_SOURCE =            register(OSMCConstants.POTION_BREW_EXP_ID,              new ExpSourceType<>(PotionBrewExpSource.CODEC));
    public static final ExpSourceType<VillagerTradeExpSource>           VILLAGER_TRADE_EXP_SOURCE =         register(OSMCConstants.VILLAGER_TRADE_EXP_ID,           new ExpSourceType<>(VillagerTradeExpSource.CODEC));

    //public static final ExpSourceType<ExpSource> _EXP_SOURCE = register(OSMCConstants., new ExpSourceType<>());

    public static void init() {}

    public static <T extends ExpSource> ExpSourceType<T> register(String id, ExpSourceType<T> type) {
        return Registry.register(ExpSourceType.REGISTRY, OSMCConstants.osmcID(id), type);
    }

}
