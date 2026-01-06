package dev.elrol.osmc.data.skills;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public abstract class AbstractSkill {

    Codec<SkillDataType<?>> skillDataTypeCodec = SkillDataType.REGISTRY.getCodec();
    Codec<AbstractSkill> CODEC = skillDataTypeCodec.dispatch("type", AbstractSkill::getType, SkillDataType::codec);

    protected boolean enabled = true;
    protected final String id;
    public String levelFormula = "floor(level + 300 * 2^(level/7)) / 4";

    public AbstractSkill(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getID() {
        return id;
    }

    public String getLevelFormula() { return levelFormula; }
    public void setLevelFormula(String levelFormula) { this.levelFormula = levelFormula; }

    public static <T extends AbstractSkill> Products.P3<RecordCodecBuilder.Mu<T>, Boolean, String, String> baseCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.BOOL.fieldOf("enabled").forGetter(AbstractSkill::isEnabled),
                Codec.STRING.fieldOf("id").forGetter(AbstractSkill::getID),
                Codec.STRING.fieldOf("levelFormula").forGetter(AbstractSkill::getLevelFormula));
    }
    public abstract SkillDataType<?> getType();
    public abstract void update();
    public abstract MapCodec<? extends AbstractSkill> getCodec();
}
