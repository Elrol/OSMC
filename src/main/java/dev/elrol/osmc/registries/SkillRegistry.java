package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.ExpGain;
import dev.elrol.osmc.data.skills.AbstractSkill;
import dev.elrol.osmc.data.skills.BlockBreakSkill;
import dev.elrol.osmc.data.skills.MiningSkill;
import dev.elrol.osmc.data.skills.WoodcuttingSkill;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.block.Blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillRegistry {

    private static Map<String, AbstractSkill> SKILL_MAP = new HashMap<>();

    public static void init(){
        register(new MiningSkill());
        register(new WoodcuttingSkill());
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractSkill> T load(T skillData) {
        MapCodec<T> codec = (MapCodec<T>) skillData.getCodec();
        JsonElement defaultJson = codec.codec().encodeStart(JsonOps.INSTANCE, skillData).getOrThrow();
        JsonElement json = JsonUtils.loadFromJson(OSMCConstants.SKILL_CONFIG_DIR, skillData.getID() + ".json", defaultJson);
        return codec.codec().parse(JsonOps.INSTANCE, json)
                .resultOrPartial(OSMC.LOGGER::error).orElse(skillData);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractSkill> void save(T skill) {
        MapCodec<T> codec = (MapCodec<T>) skill.getCodec();
        JsonElement json = codec.codec().encodeStart(JsonOps.INSTANCE, skill).getOrThrow();
        JsonUtils.saveToJson(OSMCConstants.SKILL_CONFIG_DIR, skill.getID() + ".json", json);
    }

    public static void save() {
        SKILL_MAP.forEach((id, skill) -> save(skill));
    }

    private static <T extends AbstractSkill> void register(T skillData) {
        if(SKILL_MAP == null) SKILL_MAP = new HashMap<>();
        T skill = load(skillData);
        if(skill instanceof BlockBreakSkill breakSkill) {
            Map<String, ExpGain> expMap = breakSkill.getExpGainMap();
            if(expMap.isEmpty()) {
                breakSkill.addExpGain(Blocks.WHITE_WOOL, 1, 1);
                save(breakSkill);
            }
        }
        SKILL_MAP.put(skill.getID(), skill);
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractSkill> T get(String id) {
        return (T) SKILL_MAP.get(id);
    }

    public static MiningSkill mining() {
        return (MiningSkill) SKILL_MAP.get(OSMCConstants.MINING_ID);
    }

    public static WoodcuttingSkill woodcutting() {
        return (WoodcuttingSkill) SKILL_MAP.get(OSMCConstants.WOODCUTTING_ID);
    }

    public static <T extends AbstractSkill> Map<String, T> getSkillsOfType(Class<T> clazz) {
        return SKILL_MAP.entrySet().stream()
                .filter(entry -> clazz.isInstance(entry.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> clazz.cast(entry.getValue())
                ));
    }
}
