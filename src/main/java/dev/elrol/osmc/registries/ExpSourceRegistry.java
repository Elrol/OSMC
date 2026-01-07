package dev.elrol.osmc.registries;

import com.mojang.datafixers.util.Either;
import dev.elrol.osmc.data.BoundSource;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.exp.BlockBreakExpSource;
import dev.elrol.osmc.data.exp.BlockInteractionExpSource;import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpSourceRegistry {

    private static final Map<Block, List<BoundSource<BlockBreakExpSource>>> BLOCK_BREAK_CACHE = new HashMap<>();
    private static final Map<Block, List<BoundSource<BlockInteractionExpSource>>> BLOCK_INTERACT_CACHE = new HashMap<>();

    @NotNull
    public static List<BoundSource<BlockBreakExpSource>> getBreak(Block block) {
        return BLOCK_BREAK_CACHE.getOrDefault(block, new ArrayList<>());
    }

    @NotNull
    public static List<BoundSource<BlockInteractionExpSource>> getInteract(Block block) {
        return BLOCK_INTERACT_CACHE.getOrDefault(block, new ArrayList<>());
    }

    public static void rebuild(Map<Identifier, Skill> skills) {
        BLOCK_BREAK_CACHE.clear();
        BLOCK_INTERACT_CACHE.clear();

        skills.forEach((id, skill) -> {
            for(ExpSource source : skill.getExpSources()) {
                if(source instanceof BlockBreakExpSource breakSource) {
                    for(Either<Block, TagKey<Block>> target : breakSource.getTargets()) {
                        target.ifLeft(block -> addBoundSource(block, breakSource, id))
                                .ifRight(tagKey -> {
                                    for(RegistryEntry<Block> entry : Registries.BLOCK.getOrCreateEntryList(tagKey)) {
                                        addBoundSource(entry.value(), breakSource, id);
                                    }
                                });
                    }
                } else if(source instanceof BlockInteractionExpSource interactSource) {
                    for(Either<Block, TagKey<Block>> target : interactSource.getTargets()) {
                        target.ifLeft(block -> addBoundSource(block, interactSource, id))
                                .ifRight(tagKey -> {
                                    for(RegistryEntry<Block> entry : Registries.BLOCK.getOrCreateEntryList(tagKey)) {
                                        addBoundSource(entry.value(), interactSource, id);
                                    }
                                });
                    }
                }
            }
        });
    }

    private static <T extends ExpSource> void addBoundSource(Block block, BlockInteractionExpSource source, Identifier id) {
        BLOCK_INTERACT_CACHE.computeIfAbsent(block, b -> new ArrayList<>())
                .add(new BoundSource<>(source, id));
    }

    private static <T extends ExpSource> void addBoundSource(Block block, BlockBreakExpSource source, Identifier id) {
        BLOCK_BREAK_CACHE.computeIfAbsent(block, b -> new ArrayList<>())
                .add(new BoundSource<>(source, id));
    }

}
