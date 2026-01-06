package dev.elrol.osmc.data.skills;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpGain;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BlockBreakSkill extends AbstractSkill {

    Map<String, ExpGain> expGainMap = new LinkedHashMap<>();
    Map<String, Float> chanceDropMap = new LinkedHashMap<>();

    public BlockBreakSkill(String id) {
        super(id);
    }

    public Map<String, ExpGain> getExpGainMap() { return expGainMap; }
    public Map<String, Float> getChanceDropMap() { return chanceDropMap; }

    public void addExpGain(Block block, int level, int exp) {
        addExpGain(block, new ExpGain(level, exp));
    }

    public void addExpGain(Block block, ExpGain expGain) {
        addExpGain(Registries.BLOCK.getId(block), expGain);
    }

    public void addExpGain(Identifier id, ExpGain expGain) {
        expGainMap.put(id.toString(), expGain);
    }

    public static <T extends BlockBreakSkill> Products.P5<RecordCodecBuilder.Mu<T>, Boolean, String, String, Map<String, ExpGain>, Map<String, Float>> blockBreakCodec(RecordCodecBuilder.Instance<T> instance) {
        return baseCodec(instance).and(instance.group(
                Codec.unboundedMap(Codec.STRING, ExpGain.CODEC).fieldOf("expGainMap").forGetter(BlockBreakSkill::getExpGainMap),
                Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("chanceDropMap").forGetter(BlockBreakSkill::getChanceDropMap)
        ));
    }
}
