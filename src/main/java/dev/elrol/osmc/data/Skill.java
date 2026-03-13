package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCAbilityRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("enabled").forGetter(Skill::isEnabled),
            Identifier.CODEC.fieldOf("id").forGetter(Skill::getID),
            Identifier.CODEC.optionalFieldOf("icon", Identifier.ofVanilla("bedrock")).forGetter(Skill::getIcon),
            TextCodecs.CODEC.fieldOf("displayName").forGetter(Skill::getDisplayName),
            TextColor.CODEC.fieldOf("color").forGetter(Skill::getColor),
            Codec.STRING.fieldOf("levelFormula").forGetter(Skill::getLevelFormula),
            ExpSource.CODEC.listOf().fieldOf("expSources").forGetter(Skill::getExpSources),
            SkillEffect.CODEC.listOf().optionalFieldOf("skillEffects", new ArrayList<>()).forGetter(Skill::getSkillEffects),
            Codec.unboundedMap(Identifier.CODEC, Codec.FLOAT).fieldOf("globalChanceDrops").forGetter(Skill::getGlobalChanceDrops),
            Identifier.CODEC.optionalFieldOf("ability", OSMCConstants.osmcID("example_ability")).forGetter(Skill::getAbilityID),
            Identifier.CODEC.optionalFieldOf("toolTag", Identifier.ofVanilla("tools")).forGetter(Skill::getToolTag)
    ).apply(instance, (enabled, id, icon, displayName, color, levelFormula, expSources, skillEffects, globalChanceDrops, ability, toolTag) -> {
        Skill data = new Skill(id);
        data.icon = icon;
        data.enabled = enabled;
        data.displayName = displayName;
        data.color = color;
        data.levelFormula = levelFormula;
        data.expSources.addAll(expSources);
        data.skillEffects.addAll(skillEffects);
        data.globalChanceDrops.putAll(globalChanceDrops);
        data.ability = ability;
        data.toolTag = toolTag;
        return data;
    }));

    protected List<ExpSource> expSources = new ArrayList<>();
    protected List<SkillEffect> skillEffects = new ArrayList<>();
    protected Map<Identifier, Float> globalChanceDrops = new HashMap<>();

    protected final Identifier id;
    protected Identifier icon;
    protected boolean enabled = true;
    protected TextColor color = TextColor.fromRgb(0xFF55FF);
    protected Text displayName = Text.literal("SKILL NAME HERE").formatted(Formatting.BOLD, Formatting.RED, Formatting.ITALIC);
    protected String levelFormula = "floor(level + 300 * 2^(level/7)) / 4";
    protected Identifier ability;
    protected Identifier toolTag;

    public Skill(Identifier id) {
        this.id = id;
    }

    public void addExpSource(ExpSource source) {
        expSources.add(source);
    }

    public void addSkillEffect(SkillEffect effect) {
        skillEffects.add(effect);
    }

    public void addGlobalDrop(Identifier itemID, float chance) {
        globalChanceDrops.put(itemID, chance);
    }

    public List<ExpSource> getExpSources() { return expSources; }
    public List<SkillEffect> getSkillEffects() { return skillEffects; }
    public Map<Identifier, Float> getGlobalChanceDrops() { return globalChanceDrops; }
    public Identifier getID() { return id; }
    public Identifier getIcon() { return icon; }
    public Item getIconItem() { return Registries.ITEM.get(icon); }
    public Identifier getToolTag() { return toolTag; }
    public TagKey<Item> getToolItemTag() { return TagKey.of(RegistryKeys.ITEM, toolTag); }
    public Identifier getAbilityID() { return ability; }
    public Ability getAbility() { return OSMCAbilityRegistry.get(ability); }
    public Text getDisplayName() { return displayName; }
    public String getLevelFormula() { return levelFormula; }
    public TextColor getColor() { return color; }
    public boolean isEnabled() { return enabled; }
    public MutableText getTextName() { return getDisplayName().copy().styled(style -> style.withColor(getColor())); }

    public boolean isValidTool(ItemStack stack) {
        return stack.isIn(getToolItemTag());
    }
}
