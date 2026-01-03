package dev.elrol.osmc.data.skills;

import com.mojang.serialization.Codec;

public interface ISkillData {

    Codec<SkillDataType<?>> skillDataTypeCodec = SkillDataType.REGISTRY.getCodec();
    Codec<ISkillData> CODEC = skillDataTypeCodec.dispatch("type", ISkillData::getType, SkillDataType::codec);

    SkillDataType<?> getType();
    boolean isEnabled();
    String getID();

}
