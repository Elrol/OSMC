package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.BlockBreakExpSource;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.block.Blocks;
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

        SKILL_MAP.put(skill.getID(), skill);
        save(skill);
    }

    public static Map<Identifier, Skill> getAll() { return SKILL_MAP; }

    public static Skill get(Identifier id) {
        if(!SKILL_MAP.containsKey(id)) register(new Skill(id));
        return SKILL_MAP.get(id);
    }
}
