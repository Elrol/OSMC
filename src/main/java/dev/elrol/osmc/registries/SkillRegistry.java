package dev.elrol.osmc.registries;

import com.cobblemon.mod.common.Cobblemon;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.BlockBreakExpSource;
import dev.elrol.osmc.data.exp.BlockInteractionExpSource;
import dev.elrol.osmc.data.exp.ConsumeFoodExpSource;
import dev.elrol.osmc.data.exp.ConsumePotionExpSource;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {
    // Map of all loaded skills
    private static Map<Identifier, Skill> SKILL_MAP = new HashMap<>();

    public static void init(){
        load();

        if(SKILL_MAP.isEmpty()) {
            register(new Skill(OSMCConstants.osmcID("farming")));
            register(new Skill(OSMCConstants.osmcID("mining")));
            register(new Skill(OSMCConstants.osmcID("woodcutting")));
        }
    }

    public static void load() {
        SKILL_MAP.clear();
        File[] files = OSMCConstants.SKILL_CONFIG_DIR.listFiles(file -> file.getName().endsWith(".json"));
        if(files == null) return;

        for (File file : files) {
            JsonElement json = JsonUtils.loadFromJson(OSMCConstants.SKILL_CONFIG_DIR, file.getName(), null);

            if(json != null) {
                Skill.CODEC.parse(JsonOps.INSTANCE, json)
                        .resultOrPartial(OSMC.LOGGER::error)
                        .ifPresent(SkillRegistry::register);

            } else {
                OSMC.LOGGER.error("Skill failed to load from: {}", file);
            }
        }
        if(contains(OSMCConstants.osmcID("example_skill"))) register(getExampleSkill());
    }

    public static void save(Skill skill) {
        Codec<Skill> codec = Skill.CODEC;
        JsonElement json = codec.encodeStart(JsonOps.INSTANCE, skill).getOrThrow();
        JsonUtils.saveToJson(OSMCConstants.SKILL_CONFIG_DIR, skill.getID().getPath() + ".json", json);
    }

    public static void save() {
        SKILL_MAP.forEach((id, skill) -> save(skill));
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
        save(skill);
    }

    private static Skill getExampleSkill() {
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
        //cpSource.addEffect(Registries.STATUS_EFFECT.get);
        //TODO finish adding example sources

        return skill;
    }

    public static Map<Identifier, Skill> getAll() { return SKILL_MAP; }

    public static Skill get(Identifier id) {
        if(!SKILL_MAP.containsKey(id)) register(new Skill(id));
        return SKILL_MAP.get(id);
    }

    public static boolean contains(Identifier id) {
        return SKILL_MAP.containsKey(id);
    }
}
