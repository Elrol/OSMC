package dev.elrol.osmc.registries;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.*;
import dev.elrol.osmc.data.exp.cobblemon.CaptureExpSource;
import dev.elrol.osmc.data.exp.cobblemon.EggHatchExpSource;
import dev.elrol.osmc.data.exp.cobblemon.EvolutionExpSource;
import dev.elrol.osmc.data.exp.cobblemon.LevelUpExpSource;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryOps;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SkillRegistry {
    // Map of all loaded skills
    private static Map<Identifier, Skill> SKILL_MAP = new HashMap<>();

    public static void init(MinecraftServer server){
        load(server);
    }

    public static void load(MinecraftServer server) {
        SKILL_MAP.clear();
        File[] files = OSMCConstants.SKILL_CONFIG_DIR.listFiles(file -> file.getName().endsWith(".json"));
        if(files == null) return;

        for (File file : files) {
            JsonElement json = JsonUtils.loadFromJson(OSMCConstants.SKILL_CONFIG_DIR, file.getName(), null);

            if(json != null) {
                RegistryOps<JsonElement> registryOps = server.getRegistryManager().getOps(JsonOps.INSTANCE);
                Skill.CODEC.parse(registryOps, json)
                        .resultOrPartial(OSMC.LOGGER::error)
                        .ifPresent(SkillRegistry::register);

            } else {
                OSMC.LOGGER.error("Skill failed to load from: {}", file);
            }
        }

        if(!(new File(OSMCConstants.SKILL_CONFIG_DIR, "example_skill.json").exists()))
            SkillRegistry.registerExampleSkill(server);
    }

    public static void save(Skill skill, MinecraftServer server) {
        Codec<Skill> codec = Skill.CODEC;
        RegistryOps<JsonElement> registryOps = server.getRegistryManager().getOps(JsonOps.INSTANCE);
        codec.encodeStart(registryOps, skill)
                .ifError(error -> OSMC.LOGGER.error(error.message()))
                .ifSuccess(json -> JsonUtils.saveToJson(OSMCConstants.SKILL_CONFIG_DIR, skill.getID().getPath() + ".json", json));
    }

    public static void save(MinecraftServer server) {
        SKILL_MAP.forEach((id, skill) -> save(skill, server));
    }

    private static void register(Skill skill) {
        if(SKILL_MAP == null) SKILL_MAP = new HashMap<>();

        if(skill.getExpSources().isEmpty()) {
            BlockBreakExpSource source = new BlockBreakExpSource(1);
            source.addTarget(Blocks.WHITE_WOOL);
            skill.addExpSource(source);
        }
        if(skill.getGlobalChanceDrops().isEmpty()) skill.addGlobalDrop(Identifier.ofVanilla("string"), 0.1f);

        if(skill.isEnabled()) SKILL_MAP.put(skill.getID(), skill);
    }

    public static void registerExampleSkill(MinecraftServer server) {

        Skill skill = new Skill(OSMCConstants.osmcID("example_skill"));

        // Block Break Exp Source
        BlockBreakExpSource bbSource = new BlockBreakExpSource(1);
        bbSource.addTarget(Blocks.DIRT);
        bbSource.addTarget(Identifier.ofVanilla("logs"));
        bbSource.addRequiredProperty("axis", "y");
        skill.addExpSource(bbSource);

        // Block Interact Exp Source
        BlockInteractionExpSource biSource = new BlockInteractionExpSource(1);
        biSource.addTarget(Identifier.of(Cobblemon.MODID, "apricorns"));
        biSource.addRequiredProperty("age", "3");
        skill.addExpSource(biSource);

        // Consume Food Exp Source
        ConsumeFoodExpSource cfSource = new ConsumeFoodExpSource(1);
        cfSource.addItem(new ItemStack(Items.APPLE));
        skill.addExpSource(cfSource);

        // Consume Potion Exp Source
        ConsumePotionExpSource cpSource = new ConsumePotionExpSource(1);
        cpSource.addEffect(StatusEffects.REGENERATION);
        cpSource.addEffect(StatusEffects.INSTANT_HEALTH);
        cpSource.addEffect(StatusEffects.SPEED);
        skill.addExpSource(cpSource);

        // Craft Exp Source
        CraftExpSource cSource = new CraftExpSource(1);
        cSource.addItem(new ItemStack(Items.STICK));
        skill.addExpSource(cSource);

        EnchantExpSource eSource = new EnchantExpSource(1);
        Optional<RegistryEntry.Reference<Enchantment>> mending = server.getRegistryManager()
                .get(RegistryKeys.ENCHANTMENT)
                .getEntry(Enchantments.MENDING);
        eSource.addTargetEntry(mending.get());
        eSource.addTargetTag(EnchantmentTags.ARMOR_EXCLUSIVE_SET);
        skill.addExpSource(eSource);

        EntityInteractionExpSource eiSource = new EntityInteractionExpSource(1);
        eiSource.addEntity(EntityType.BOAT);
        skill.addExpSource(eiSource);

        EntityKillExpSource ekSource = new EntityKillExpSource(1);
        ekSource.addEntity(EntityType.PIG);
        skill.addExpSource(ekSource);

        ItemUseExpSource iuSource = new ItemUseExpSource(1);
        iuSource.addItem(new ItemStack(Items.STICK));
        skill.addExpSource(iuSource);

        PotionBrewExpSource pbSource = new PotionBrewExpSource(1);
        pbSource.addIngredient(new ItemStack(Items.NETHER_WART));
        skill.addExpSource(pbSource);

        VillagerTradeExpSource vtSource1 = new VillagerTradeExpSource(1);
        vtSource1.addInputItem(new ItemStack(Items.EMERALD));
        skill.addExpSource(vtSource1);

        VillagerTradeExpSource vtSource2 = new VillagerTradeExpSource(2);
        vtSource2.addOutputItem(new ItemStack(Items.EMERALD));
        skill.addExpSource(vtSource2);

        // Cobblemon ExpSources

        CaptureExpSource pcSource = new CaptureExpSource(1);
        pcSource.addSpecies(PokemonSpecies.getByName("magikarp"));
        skill.addExpSource(pcSource);
        skill.addExpSource(new CaptureExpSource(1));

        skill.addExpSource(new EvolutionExpSource(1));

        skill.addExpSource(new EggHatchExpSource(1));

        skill.addExpSource(new LevelUpExpSource(1));

        //TODO finish adding example sources

        register(skill);
        save(skill, server);
    }

    public static Map<Identifier, Skill> getAll() { return SKILL_MAP; }

    @Nullable
    public static Skill get(Identifier id) {
        return SKILL_MAP.get(id);
    }

    public static boolean contains(Identifier id) {
        return SKILL_MAP.containsKey(id);
    }
}
