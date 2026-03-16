package dev.elrol.osmc.data;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public abstract class AbilityEffect {

    public static final Codec<AbilityEffect> CODEC = AbilityEffectType.REGISTRY.getCodec()
            .dispatch("type", AbilityEffect::getType, AbilityEffectType::codec);

    private final Identifier abilityEffectID;
    private final int reqLevel;
    private final boolean togglable;
    protected Text displayName = Text.literal("ABILITY EFFECT NAME HERE").formatted(Formatting.BOLD, Formatting.ITALIC, Formatting.UNDERLINE);
    public Text description = Text.empty();

    protected AbilityEffect(Identifier abilityEffectID, int reqLevel) {
        this.abilityEffectID = abilityEffectID;
        this.reqLevel = reqLevel;
        this.togglable = false;
    }

    protected AbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable) {
        this.abilityEffectID = abilityEffectID;
        this.reqLevel = reqLevel;
        this.togglable = togglable;
    }

    public void setDescription(Text description) { this.description = description; }
    public void setDisplayName(Text displayName) { this.displayName = displayName; }

    public Identifier getAbilityEffectID() { return abilityEffectID; }
    public int getReqLevel() { return reqLevel; }
    public boolean isTogglable() { return togglable; }
    public Text getDisplayName() { return displayName; }
    public Text getDescription() { return description; }
    public abstract AbilityEffectType<?> getType();

    public static <T extends AbilityEffect> Products.P5<RecordCodecBuilder.Mu<T>, Identifier, Integer, Boolean, Text, Text> getCommonCodec(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                Identifier.CODEC.fieldOf("abilityEffectID").forGetter(AbilityEffect::getAbilityEffectID),
                Codec.INT.fieldOf("reqLevel").forGetter(AbilityEffect::getReqLevel),
                Codec.BOOL.fieldOf("togglable").forGetter(AbilityEffect::isTogglable),
                TextCodecs.CODEC.fieldOf("displayName").forGetter(AbilityEffect::getDisplayName),
                TextCodecs.CODEC.fieldOf("description").forGetter(AbilityEffect::getDescription)
        );
    }
}
