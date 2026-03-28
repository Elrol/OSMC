package dev.elrol.osmc.libs;

import com.cobblemon.mod.common.pokemon.Species;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.*;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.Objects;

public class OSMCConstants {

    public static final String MODID = "osmc";

    // Identifiers
    public static final Identifier EXP_TYPE_ID = Identifier.of(MODID, "exp_data_type");
    public static final Identifier EFFECT_TYPE_ID = Identifier.of(MODID, "effect_data_type");
    public static final Identifier ABILITY_EFFECT_TYPE_ID = Identifier.of(MODID, "ability_effect_data_type");

    // Registry Keys
    public static final RegistryKey<Registry<ExpSourceType<?>>> EXP_TYPE_KEY = RegistryKey.ofRegistry(EXP_TYPE_ID);
    public static final RegistryKey<Registry<SkillEffectType<?>>> EFFECT_TYPE_KEY = RegistryKey.ofRegistry(EFFECT_TYPE_ID);
    public static final RegistryKey<Registry<AbilityEffectType<?>>> ABILITY_EFFECT_TYPE_KEY = RegistryKey.ofRegistry(ABILITY_EFFECT_TYPE_ID);

    // Files
    public static final File ROOT_DIR = FabricLoader.getInstance().getGameDir().toFile();
    public static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "OSMC");
    public static final File ABILITY_CONFIG_DIR = new File(CONFIG_DIR, "Abilities");
    public static final File SKILL_CONFIG_DIR = new File(CONFIG_DIR, "Skills");
    public static final File TIER_CONFIG_DIR = new File(CONFIG_DIR, "Tiers");

    public static final File OSMC_DATA_DIR = new File(ROOT_DIR, "OSMC");
    public static final File PLAYER_DATA_DIR = new File(OSMC_DATA_DIR, "player_data");

    // Exp Source Type IDs
    public static final String BLOCK_BREAK_EXP_ID               = "block_break";
    public static final String BLOCK_INTERACT_EXP_ID            = "block_interact";
    public static final String BLOCK_BRUSH_EXP_ID               = "block_brush";
    public static final String CONSUME_FOOD_EXP_ID              = "consume_food";
    public static final String CONSUME_POTION_EXP_ID            = "consume_potion";
    public static final String CRAFT_EXP_ID                     = "craft";
    public static final String ENCHANT_EXP_ID                   = "enchant";
    public static final String ENTITY_INTERACTION_EXP_ID        = "entity_interact";
    public static final String ENTITY_KILL_EXP_ID               = "entity_kill";
    public static final String ITEM_USE_EXP_ID                  = "item_use";
    public static final String POTION_BREW_EXP_ID               = "potion_brew";
    public static final String VILLAGER_TRADE_EXP_ID            = "villager_trade";

    public static final String COBBLEMON_CAPTURE_EXP_ID         = "cobblemon_capture";
    public static final String COBBLEMON_NPC_BATTLE_EXP_ID      = "cobblemon_npc_battle";
    public static final String COBBLEMON_PLAYER_BATTLE_EXP_ID   = "cobblemon_player_battle";
    public static final String COBBLEMON_WILD_BATTLE_EXP_ID     = "cobblemon_wild_battle";
    public static final String COBBLEMON_EVOLUTION_EXP_ID       = "cobblemon_evolution";
    public static final String COBBLEMON_EGG_HATCH_EXP_ID       = "cobblemon_egg_hatch";
    public static final String COBBLEMON_LEVEL_UP_EXP_ID        = "cobblemon_level_up";
    public static final String COBBLEMON_FOSSIL_REVIVE_EXP_ID   = "cobblemon_fossil_revive";
    public static final String COBBLEMON_HARVEST_EXP_ID         = "cobblemon_harvest";

    public static final String COBBLEMON_QUICK_BATTLE_EXP_ID    = "quick_battle";

    // Skill Effect Type IDs
    public static final String BLOCK_DROP_MULTIPLIER_EFFECT_ID  = "block_drop_multiplier";
    public static final String BLOCK_DROP_EXTRA_EFFECT_ID       = "block_drop_extra";
    public static final String DAMAGE_MITIGATION_EFFECT_ID      = "damage_mitigation";
    public static final String LOOT_ROLL_EFFECT_ID              = "loot_roll";
    public static final String MOB_DROP_MULTIPLIER_EFFECT_ID    = "mob_drop_multiplier";
    public static final String STAT_MODIFIER_EFFECT_ID          = "stat_modifier";

    // Ability Effect Type IDs
    public static final String DURATION_ABILITY_EFFECT_ID       = "duration_ability";
    public static final String COOLDOWN_ABILITY_EFFECT_ID       = "cooldown_ability";
    public static final String SHAPE_BREAK_ABILITY_EFFECT_ID    = "shape_break_ability";
    public static final String CHAIN_BREAK_ABILITY_EFFECT_ID    = "chain_break_ability";

    // Codecs
    public static final Codec<Either<RegistryKey<Item>, TagKey<Item>>>                          TARGET_ITEM_CODEC           = Codec.either(RegistryKey.createCodec(RegistryKeys.ITEM), TagKey.codec(RegistryKeys.ITEM));
    public static final Codec<Either<RegistryKey<Block>, TagKey<Block>>>                        TARGET_BLOCK_CODEC          = Codec.either(RegistryKey.createCodec(RegistryKeys.BLOCK), TagKey.codec(RegistryKeys.BLOCK));
    public static final Codec<Either<RegistryKey<Enchantment>, TagKey<Enchantment>>>            TARGET_ENCHANTMENT_CODEC    = Codec.either(RegistryKey.createCodec(RegistryKeys.ENCHANTMENT), TagKey.codec(RegistryKeys.ENCHANTMENT));
    public static final Codec<Either<RegistryKey<DamageType>, TagKey<DamageType>>>              TARGET_DAMAGE_TYPE_CODEC    = Codec.either(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE), TagKey.codec(RegistryKeys.DAMAGE_TYPE));
    public static final Codec<Either<RegistryKey<StatusEffect>, TagKey<StatusEffect>>>          TARGET_STATUS_EFFECT_CODEC  = Codec.either(RegistryKey.createCodec(RegistryKeys.STATUS_EFFECT), TagKey.codec(RegistryKeys.STATUS_EFFECT));
    public static final Codec<Either<RegistryKey<EntityType<?>>, TagKey<EntityType<?>>>>        TARGET_ENTITY_TYPE_CODEC    = Codec.either(RegistryKey.createCodec(RegistryKeys.ENTITY_TYPE), TagKey.codec(RegistryKeys.ENTITY_TYPE));

    public static final Codec<Either<Species, String>>                                          TARGET_SPECIES_CODEC        = Codec.either(Species.getBY_IDENTIFIER_CODEC(), Codec.STRING);

    public static Gson makeGSON() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
    public static Identifier osmcID(String id) { return Identifier.of(MODID, id); }
    public static Text osmcTag() {
        MutableText tag = Text.empty();

        tag.append(Text.literal("[").formatted(Formatting.GRAY));
        tag.append(Text.literal("OSMC").formatted(Formatting.AQUA));
        tag.append(Text.literal("] ").formatted(Formatting.GRAY));

        return tag;
    }

    public static void sendExpMessage(ServerPlayerEntity player, Skill skill, String message) {
        player.sendMessage(Text.empty()
                .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                .append(skill.getTextName())
                .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(" " + message)));
    }

    public static void levelUpNotification(ServerPlayerEntity player, Identifier skillID) {
        boolean global = OSMC.CONFIG.getSendLevelUpToGlobal();
        Skill skill = OSMCSkillRegistry.get(skillID);
        if(skill == null) return;

        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());

        player.playSoundToPlayer(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS, 1.0f, 0.5f);
        Text text = Text.empty()
                .append(osmcTag())
                .append(global ? Text.empty().append(player.getName()).append(" has ") : Text.literal("You have "))
                .append("leveled up the ")
                .append(skill.getTextName())
                .append(" skill to level " + data.getSkillLevel(skillID) + "!");


        if(global) {
            Objects.requireNonNull(player.getServer()).getPlayerManager().broadcast(text, false);
        } else {
            player.sendMessage(text);
        }
    }

    public static boolean hasQuickBattle() { return FabricLoader.getInstance().isModLoaded("cobblemon_quick_battle"); }

}
