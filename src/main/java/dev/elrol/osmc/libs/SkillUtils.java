package dev.elrol.osmc.libs;

import com.mojang.datafixers.util.Either;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.*;
import dev.elrol.osmc.data.ability_effects.CooldownAbilityEffect;
import dev.elrol.osmc.data.ability_effects.DurationAbilityEffect;
import dev.elrol.osmc.data.ability_effects.ShapeBreakAbilityEffect;
import dev.elrol.osmc.registries.OSMCAbilityRegistry;
import dev.elrol.osmc.registries.OSMCCobblemonTierRegistry;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Executable;
import java.util.*;
import java.util.concurrent.Callable;

public class SkillUtils {

    public static int calcAbilityInteger(ServerPlayerEntity player, Identifier skillID, AbilityAction action) {
        Skill skill = OSMCSkillRegistry.get(skillID);
        if (skill == null || skill.getAbility() == null) return 0;
        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());

        return action.execute(
                skill,
                skill.getAbility(),
                data.getSkillSettings(skillID),
                data.getSkillLevel(skillID)
        );
    }

    public static int getPlayerAbilityCooldown(ServerPlayerEntity player, Identifier skillID) {
        return calcAbilityInteger(player, skillID, ((skill, ability, settings, level) -> {
            int cooldown = ability.getBaseCooldown();

            for (AbilityEffect effect : ability.getEffects()) {
                if(effect instanceof CooldownAbilityEffect coolEff && coolEff.getReqLevel() <= level) {
                    cooldown -= coolEff.getReduceSeconds();
                }
            }

            return Math.max(cooldown, 0);
        }));
    }

    public static void updateAbilityShapePoints(ServerPlayerEntity player, Skill skill, int level) {
        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
        SkillSettingsData skillSettings = data.getSkillSettings(skill.getID());

        Ability ability = skill.getAbility();
        if(ability != null && ability.doesHaveShapeSettings()) {
            int points = 0;
            for (ShapeBreakAbilityEffect effect : ability.getEffects(ShapeBreakAbilityEffect.class)) {
                if(effect.getReqLevel() <= level)
                    points += effect.getExtraBlocks();
            }

            skillSettings.settShapePoints(points);
        }
    }

    public static int getPlayerAbilityDuration(ServerPlayerEntity player, Identifier skillID) {
        return calcAbilityInteger(player, skillID, ((skill, ability, settings, level) -> {
            int duration = ability.getBaseDuration();

            for (AbilityEffect effect : ability.getEffects()) {
                if(effect instanceof DurationAbilityEffect durEff && durEff.getReqLevel() <= level) {
                    duration += durEff.getExtraSeconds();
                }
            }

            return duration;
        }));
    }

    public static int getPlayerAbilityBlockConfigPoint(ServerPlayerEntity player, Identifier skillID) {
        return calcAbilityInteger(player, skillID, ((skill, ability, settings, level) -> {
            int points = 0;

            for (AbilityEffect effect : ability.getEffects()) {
                if(effect instanceof ShapeBreakAbilityEffect sbEff && sbEff.getReqLevel() <= level) {
                    points += sbEff.getExtraBlocks();
                }
            }

            return points;
        }));
    }

    public static boolean isValid(Block block, List<Either<RegistryKey<Block>, TagKey<Block>>> validBlocks) {
        return validBlocks.stream().anyMatch(either ->
                either.map(a -> block.getDefaultState().matchesKey(a), tagKey -> block.getDefaultState().isIn(tagKey)));
    }

    public static boolean isValid(ItemStack stack,List<Either<RegistryKey<Item>, TagKey<Item>>> validItems) {
        RegistryEntry<Item> entry = stack.getRegistryEntry();
        return validItems.stream().anyMatch(either ->
                either.map(entry::matchesKey, stack::isIn));
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

        List<CobblemonTier> tiers = OSMCCobblemonTierRegistry.get();
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

    public static List<BlockPos> findBlocks(World world, BlockPos startPos, int maxBlocks) {
        List<BlockPos> foundBlocks = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toExplore = new LinkedList<>();

        // Use the Block itself for more flexible matching (Log == Log)
        Block targetBlock = world.getBlockState(startPos).getBlock();

        toExplore.add(startPos);
        visited.add(startPos);

        // Standard safety cap to prevent infinite loops/crashes
        int safetyCap = Math.min(maxBlocks, 500);

        while (!toExplore.isEmpty() && foundBlocks.size() < safetyCap) {
            BlockPos currentPos = toExplore.poll();
            foundBlocks.add(currentPos);

            BlockPos.Mutable mutableNeighbor = new BlockPos.Mutable();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;

                        mutableNeighbor.set(currentPos.getX() + x, currentPos.getY() + y, currentPos.getZ() + z);

                        BlockPos neighborImmutable = mutableNeighbor.toImmutable();

                        if (!visited.contains(neighborImmutable)) {
                            visited.add(neighborImmutable);

                            if (world.getBlockState(neighborImmutable).isOf(targetBlock)) {
                                toExplore.add(neighborImmutable);
                            }
                        }
                    }
                }
            }
        }
        return foundBlocks;
    }

    @Nullable
    public static RegistryEntry<Enchantment> getEnchantmentEntry(DynamicRegistryManager registryManager, RegistryKey<Enchantment> enchantment) {
        return registryManager.get(RegistryKeys.ENCHANTMENT).getEntry(enchantment).orElse(null);
    }

    public static int getEnchantmentLevel(DynamicRegistryManager registryManager, ItemStack stack, RegistryKey<Enchantment> enchantment) {
        RegistryEntry<Enchantment> entry = getEnchantmentEntry(registryManager, enchantment);
        return entry == null ? -1 : stack.getEnchantments().getLevel(entry);
    }

    public static boolean hasEnchantment(DynamicRegistryManager registryManager, ItemStack stack, RegistryKey<Enchantment> enchantment) {
        return getEnchantmentLevel(registryManager, stack, enchantment) > 0;
    }

    @FunctionalInterface
    public interface AbilityAction {
        int execute(Skill skill, Ability ability, SkillSettingsData settings, int level);
    }
}
