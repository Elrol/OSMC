package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.skills.ISkillData;
import dev.elrol.osmc.data.skills.MiningSkillData;
import dev.elrol.osmc.data.skills.SkillDataType;
import dev.elrol.osmc.data.skills.WoodcuttingSkillData;
import dev.elrol.osmc.libs.OSMCConstants;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkillDataTypeRegistry {

    public static final SkillDataType<MiningSkillData> MINING_SKILL = register("mining", new SkillDataType<>(MiningSkillData.CODEC));
    public static final SkillDataType<WoodcuttingSkillData> WOODCUTTING_SKILL = register("woodcutting", new SkillDataType<>(WoodcuttingSkillData.CODEC));

    public static void init(){}

    public static <T extends ISkillData>SkillDataType<T> register(String id, SkillDataType<T> type) {
        return Registry.register(SkillDataType.REGISTRY, Identifier.of(OSMCConstants.MODID, id), type);
    }

}
