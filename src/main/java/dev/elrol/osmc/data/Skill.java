package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;import net.minecraft.util.Identifier;

import java.util.ArrayList;import java.util.HashMap;
import java.util.List;import java.util.Map;

public class Skill {

    public static final Codec<Skill> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("enabled").forGetter(Skill::isEnabled),
            Identifier.CODEC.fieldOf("id").forGetter(Skill::getID),
            TextCodecs.CODEC.fieldOf("displayName").forGetter(Skill::getDisplayName),
            TextColor.CODEC.fieldOf("color").forGetter(Skill::getColor),
            Codec.STRING.fieldOf("levelFormula").forGetter(Skill::getLevelFormula),
            ExpSource.CODEC.listOf().fieldOf("expSources").forGetter(Skill::getExpSources),
            Codec.unboundedMap(Identifier.CODEC, Codec.FLOAT).fieldOf("globalChanceDrops").forGetter(Skill::getGlobalChanceDrops)
    ).apply(instance, (enabled, id, displayName, color, levelFormula, expSources, globalChanceDrops) -> {
        Skill data = new Skill(id);
        data.enabled = enabled;
        data.displayName = displayName;
        data.color = color;
        data.levelFormula = levelFormula;
        data.expSources.addAll(expSources);
        data.globalChanceDrops.putAll(globalChanceDrops);
        return data;
    }));

    protected List<ExpSource> expSources = new ArrayList<>();
    protected Map<Identifier, Float> globalChanceDrops = new HashMap<>();

    protected final Identifier id;
    protected boolean enabled = true;
    protected TextColor color = TextColor.fromRgb(0xFF55FF);
    protected Text displayName = Text.literal("SKILL NAME HERE").formatted(Formatting.BOLD, Formatting.RED, Formatting.ITALIC);
    protected String levelFormula = "floor(level + 300 * 2^(level/7)) / 4";

    public Skill(Identifier id) {
        this.id = id;
    }

    public void addExpSource(ExpSource source) {
        expSources.add(source);
    }

    public void addGlobalDrop(Identifier itemID, float chance) {
        globalChanceDrops.put(itemID, chance);
    }

    public List<ExpSource> getExpSources() { return expSources; }
    public Map<Identifier, Float> getGlobalChanceDrops() { return globalChanceDrops; }
    public Identifier getID() { return id; }
    public Text getDisplayName() { return displayName; }
    public String getLevelFormula() { return levelFormula; }
    public TextColor getColor() { return color; }
    public boolean isEnabled() { return enabled; }
    public MutableText getTextName() { return getDisplayName().copy().styled(style -> style.withColor(getColor())); }
}
