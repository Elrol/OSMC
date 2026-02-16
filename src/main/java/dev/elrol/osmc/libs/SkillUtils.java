package dev.elrol.osmc.libs;

import com.mojang.datafixers.util.Either;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.CobblemonTier;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SkillUtils {

    public static boolean isValid(Block block, List<Either<RegistryKey<Block>, TagKey<Block>>> validBlocks) {
        return validBlocks.stream().anyMatch(either ->
                either.map(a -> block.getDefaultState().matchesKey(a), tagKey -> block.getDefaultState().isIn(tagKey)));
    }

    public static int getPlayerTrainerLevel(UUID uuid) {
        return OSMC.CONFIG.getTrainerLevel().calculate(uuid);
    }

    public static int getPlayerTrainerLevel(PlayerEntity player) {
        return getPlayerTrainerLevel(player.getUuid());
    }

    @Nullable
    public static CobblemonTier getPlayerTier(UUID uuid) {
        CobblemonTier tier = null;
        int level = getPlayerTrainerLevel(uuid);

        List<CobblemonTier> tiers = OSMC.CONFIG.getCobblemonTiers().getTiers();
        for (CobblemonTier cobblemonTier : tiers) {
            if(cobblemonTier.getReqLevel() <= level) tier = cobblemonTier;
            else break;
        }
        return tier;
    }

    public static CobblemonTier getPlayerTier(PlayerEntity player) {
        return getPlayerTier(player.getUuid());
    }

    public static int getTotalSkillLevel(PlayerEntity player) {
        return getTotalSkillLevel(player.getUuid());
    }

    public static int getTotalSkillLevel(UUID uuid) {
        PlayerSkillData data = OSMCPlayerDataRegistry.get(uuid);

        int totalLevel = 0;
        for (Identifier id : data.getSkillExpMap().keySet()) {
            totalLevel += data.getSkillLevel(id);
        }

        return totalLevel;
    }
}
