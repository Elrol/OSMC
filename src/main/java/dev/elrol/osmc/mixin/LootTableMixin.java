package dev.elrol.osmc.mixin;

import dev.elrol.osmc.data.BoundEffect;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.data.effects.LootRollSkillEffect;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCLootTracker;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillEffectRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ReloadableRegistries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin {

    @Unique
    private static final ThreadLocal<Boolean> IS_PROCESSING = ThreadLocal.withInitial(() -> false);

    @Unique
    private LootTable self() {
        return (LootTable) (Object) this;
    }

    @Unique
    private Identifier getId(ReloadableRegistries.Lookup lookup) {
        return lookup.getIds(RegistryKeys.LOOT_TABLE)
                .stream()
                .filter(id -> lookup.getLootTable(RegistryKey.of(RegistryKeys.LOOT_TABLE, id)) == self())
                .findFirst()
                .orElse(OSMCLootTracker.get());
    }

    @Inject(method = "generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;JLjava/util/function/Consumer;)V", at = @At("HEAD"))
    public void osmc$generateLootParameters(LootContextParameterSet parameters, long seed, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        if(IS_PROCESSING.get()) return;

        ServerPlayerEntity player;
        if(parameters.contains(LootContextParameters.THIS_ENTITY) && parameters.get(LootContextParameters.THIS_ENTITY) instanceof ServerPlayerEntity p)
            player = p;
        else if(parameters.contains(LootContextParameters.LAST_DAMAGE_PLAYER) && parameters.get(LootContextParameters.LAST_DAMAGE_PLAYER) instanceof ServerPlayerEntity p)
            player = p;
        else { player = null; }

        if(player == null) return;

        if(player.getServer() != null) {
            Identifier tableID = getId(player.getServer().getReloadableRegistries());
            if (tableID == null) return;

            try {
                IS_PROCESSING.set(true);
                List<BoundEffect<?>> boundEffects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.LOOT_ROLL, tableID);

                boundEffects.forEach(boundEffect -> {
                    if (boundEffect.effect() instanceof LootRollSkillEffect effect) {
                        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
                        Identifier skillID = boundEffect.skillID();
                        int skillLevel = data.getSkillLevel(skillID);
                        double chance = effect.calculateChanceDrop(skillLevel);
                        int extraRolls = (int) chance;
                        if (MathUtils.percentChance((float) (chance - extraRolls))) extraRolls++;

                        if (extraRolls > 0) {


                            for (int i = 0; i < extraRolls; i++) {
                                self().generateUnprocessedLoot(parameters, lootConsumer);
                            }
                        }
                        OSMCPlayerDataRegistry.bufferExp(player.getUuid(), skillID, (int) effect.calculateExp(skillLevel, extraRolls));
                    }
                });
            } finally {
                IS_PROCESSING.set(false);
                OSMCLootTracker.clear();
            }
        }
    }

    @Inject(method = "generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
    public void osmc$generateLoot(LootContext context, Consumer<ItemStack> lootConsumer, CallbackInfo ci) {
        if(IS_PROCESSING.get()) return;



        ServerPlayerEntity player;
        if(context.get(LootContextParameters.THIS_ENTITY) instanceof ServerPlayerEntity p)
            player = p;
        else if(context.get(LootContextParameters.LAST_DAMAGE_PLAYER) instanceof ServerPlayerEntity p)
            player = p;
        else { player = null; }

        if(player == null) return;

        if(player.getServer() != null) {
            Identifier tableID = getId(player.getServer().getReloadableRegistries());
            if(tableID == null) return;

            try {
                IS_PROCESSING.set(true);
                List<BoundEffect<?>> boundEffects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.LOOT_ROLL, tableID);

                boundEffects.forEach(boundEffect -> {
                    if (boundEffect.effect() instanceof LootRollSkillEffect effect) {
                        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
                        Identifier skillID = boundEffect.skillID();
                        int skillLevel = data.getSkillLevel(skillID);
                        double chance = effect.calculateChanceDrop(skillLevel);
                        int extraRolls = (int) chance;
                        if(MathUtils.percentChance((float)(chance - extraRolls))) extraRolls++;

                        if(extraRolls > 0) {


                            for(int i = 0; i < extraRolls; i++) {
                                self().generateUnprocessedLoot(context, lootConsumer);
                            }
                        }
                        OSMCPlayerDataRegistry.bufferExp(player.getUuid(), skillID, (int) effect.calculateExp(skillLevel, extraRolls));
                    }
                });
            } finally {
                IS_PROCESSING.set(false);
                OSMCLootTracker.clear();
            }
        }
    }

}
