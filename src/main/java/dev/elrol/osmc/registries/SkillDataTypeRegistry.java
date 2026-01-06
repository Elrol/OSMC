package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.skills.AbstractSkill;
import dev.elrol.osmc.data.skills.MiningSkill;
import dev.elrol.osmc.data.skills.SkillDataType;
import dev.elrol.osmc.data.skills.WoodcuttingSkill;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillDataTypeRegistry {

    public static final SkillDataType<MiningSkill> MINING_SKILL = register("mining", new SkillDataType<>(MiningSkill.CODEC));
    public static final SkillDataType<WoodcuttingSkill> WOODCUTTING_SKILL = register("woodcutting", new SkillDataType<>(WoodcuttingSkill.CODEC));

    public static void init(){}

    public static <T extends AbstractSkill>SkillDataType<T> register(String id, SkillDataType<T> type) {
        return Registry.register(SkillDataType.REGISTRY, Identifier.of(OSMCConstants.MODID, id), type);
    }

}
