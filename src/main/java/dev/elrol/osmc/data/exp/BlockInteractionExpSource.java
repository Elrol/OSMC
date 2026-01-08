package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockInteractionExpSource extends ExpSource {

    public static final Codec<Either<Block, TagKey<Block>>> TARGET_CODEC = Codec.either(
            Registries.BLOCK.getCodec(),
            TagKey.codec(RegistryKeys.BLOCK)
    );

    public static final MapCodec<BlockInteractionExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExpSource.commonCodec(),
            TARGET_CODEC.listOf().fieldOf("targets").forGetter(BlockInteractionExpSource::getTargets),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("requiredProperties").forGetter(BlockInteractionExpSource::getRequiredProperties)
    ).apply(instance, (expGain, targets, requiredProperties) -> {
        BlockInteractionExpSource data = new BlockInteractionExpSource(expGain);
        data.targets.addAll(targets);
        data.requiredProperties.putAll(requiredProperties);
        return data;
    }));

    private final List<Either<Block, TagKey<Block>>> targets = new ArrayList<>();
    private final Map<String, String> requiredProperties = new HashMap<>();

    public BlockInteractionExpSource(int expGain) {
        super(expGain);
    }

    public List<Either<Block, TagKey<Block>>> getTargets() { return targets; }
    public Map<String, String> getRequiredProperties() { return requiredProperties; }

    /**
     * Adds the target block to this source
     * @param target the block
     */
    public void addTarget(Block target) {
        targets.add(Either.left(target));
    }

    public boolean hasProperties(BlockState state) {
        return requiredProperties.entrySet().stream().allMatch(entry -> {
            String key = entry.getKey();
            String expectedValue = entry.getValue();

            return state.getEntries().entrySet().stream().anyMatch(stateEntry ->
                    stateEntry.getKey().getName().equals(key) && stateEntry.getValue().toString().equals(expectedValue));
        });
    }

    /**
     * Adds the target block tag to this source
     * @param id the block tag
     */
    public void addTarget(Identifier id) {
        targets.add(Either.right(TagKey.of(Registries.BLOCK.getKey(), id)));
    }

    public void addRequiredProperty(String key, String value) {
        requiredProperties.put(key, value);
    }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.BLOCK_INTERACT_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
