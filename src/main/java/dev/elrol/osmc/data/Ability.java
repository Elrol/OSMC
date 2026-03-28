package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Ability {

    public static final Codec<Ability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("enabled").forGetter(Ability::isEnabled),
            Codec.BOOL.fieldOf("hasShapeSettings").forGetter(Ability::doesHaveShapeSettings),
            Identifier.CODEC.fieldOf("id").forGetter(Ability::getID),
            TextCodecs.CODEC.fieldOf("displayName").forGetter(Ability::getDisplayName),
            Codec.INT.fieldOf("baseDuration").forGetter(Ability::getBaseDuration),
            Codec.INT.fieldOf("baseCooldown").forGetter(Ability::getBaseCooldown),
            AbilityEffect.CODEC.listOf().fieldOf("effects").forGetter(Ability::getEffects)
    ).apply(instance, (enabled, hasShapeSettings, id, displayName, baseDuration, baseCooldown, effects) -> {
        Ability data = new Ability(id, displayName);
        data.enabled = enabled;
        data.hasShapeSettings = hasShapeSettings;
        data.baseDuration = baseDuration;
        data.baseCooldown = baseCooldown;
        data.effects.addAll(effects);
        return data;
    }));

    protected boolean enabled = true;
    protected boolean hasShapeSettings = false;
    protected final Identifier id;
    protected final Text displayName;
    protected int baseDuration = 10;
    protected int baseCooldown = 60;
    protected List<AbilityEffect> effects = new ArrayList<>();

    public Ability(Identifier id) {
        this.id = id;
        displayName = Text.of("EXAMPLE NAME");
    }

    public Ability(Identifier id, Text displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public void addAbilityEffect(AbilityEffect effect) {
        effects.add(effect);
    }

    public <T extends AbilityEffect> List<T> getEffects(Class<T> effectClass) {
        return effects.stream().filter(effect -> effect.getClass().equals(effectClass)).map(effect -> (T) effect).toList();
    }

    public boolean isEnabled() { return enabled; }
    public boolean doesHaveShapeSettings() { return hasShapeSettings; }
    public Identifier getID() { return id; }
    public Text getDisplayName() { return displayName; }
    public int getBaseDuration() { return baseDuration; }
    public int getBaseCooldown() { return baseCooldown; }
    public List<AbilityEffect> getEffects() { return effects; }
}
