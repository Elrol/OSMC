package dev.elrol.osmc.data.skills;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public abstract class AbstractSkillData implements ISkillData {
    protected boolean enabled = true;
    protected final String id;

    public AbstractSkillData(String id) {
        this.id = id;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String getID() {
        return id;
    }

    public static <T extends AbstractSkillData> Products.P2<RecordCodecBuilder.Mu<T>, Boolean, String> baseCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Codec.BOOL.fieldOf("enabled").forGetter(AbstractSkillData::isEnabled),
                Codec.STRING.fieldOf("id").forGetter(AbstractSkillData::getID));
    }

    public abstract MapCodec<? extends AbstractSkillData> getCodec();
}
