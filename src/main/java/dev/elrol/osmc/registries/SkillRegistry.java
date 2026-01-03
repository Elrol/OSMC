package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import dev.elrol.osmc.Osmc;
import dev.elrol.osmc.data.skills.*;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SkillRegistry {

    public static final MiningSkillData MINING_SKILL = register(new MiningSkillData());
    public static final WoodcuttingSkillData WOODCUTTING_SKILL = register(new WoodcuttingSkillData());

    public static Map<String, AbstractSkillData> SKILL_MAP = new HashMap<>();

    public static void init(){}

    @SuppressWarnings("unchecked")
    private static <T extends AbstractSkillData> T load(T skillData) {
        JsonElement json = JsonUtils.loadFromJson(OSMCConstants.SKILL_CONFIG_DIR, skillData.getID() + ".json", JsonParser.parseString("{}"));
        MapCodec<T> codec = (MapCodec<T>) skillData.getCodec();
        return codec.codec().parse(JsonOps.INSTANCE, json)
                .resultOrPartial(error -> {
                    // TODO
                }).orElse(skillData);
    }

    private static <T extends AbstractSkillData> T register(T skillData) {
        if(SKILL_MAP == null) SKILL_MAP = new HashMap<>();
        T skill = load(skillData);
        SKILL_MAP.put(skill.getID(), skill);
        return skill;
    }
}
