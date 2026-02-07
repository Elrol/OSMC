package dev.elrol.osmc.libs;

import com.mojang.datafixers.util.Either;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.List;

public class SkillUtils {

    public static boolean isValid(Block block, List<Either<RegistryKey<Block>, TagKey<Block>>> validBlocks) {
        return validBlocks.stream().anyMatch(either ->
                either.map(a -> block.getDefaultState().matchesKey(a), tagKey -> block.getDefaultState().isIn(tagKey)));
    }

}
