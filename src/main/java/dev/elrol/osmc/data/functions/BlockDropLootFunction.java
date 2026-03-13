package dev.elrol.osmc.data.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.BoundEffect;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.data.skill_effects.BlockDropExtraSkillEffect;
import dev.elrol.osmc.data.skill_effects.BlockDropMultiplierSkillEffect;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.SkillUtils;
import dev.elrol.osmc.registries.OSMCLootFunctionRegistry;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillEffectRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class BlockDropLootFunction extends ConditionalLootFunction {

    public static final MapCodec<BlockDropLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
        addConditionsField(instance).apply(instance, BlockDropLootFunction::new));

    protected BlockDropLootFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return OSMCLootFunctionRegistry.BLOCK_DROP_MULTIPLIER_FUNCTION_TYPE;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if(context.get(LootContextParameters.THIS_ENTITY) instanceof ServerPlayerEntity player) {
            BlockState state = context.get(LootContextParameters.BLOCK_STATE);
            if(state == null || SkillUtils.hasEnchantment(player.getServerWorld().getRegistryManager(), player.getMainHandStack(), Enchantments.SILK_TOUCH)) return stack;

            List<BoundEffect<?>> boundEffects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.BLOCK_DROP, state.getBlock());

            PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
            boundEffects.forEach(boundEffect -> {
                Identifier skillID = boundEffect.skillID();
                int skillLevel = data.getSkillLevel(skillID);
                int reqLevel = boundEffect.effect().getReqLevel();
                if(skillLevel < reqLevel) return;

                if(boundEffect.effect() instanceof BlockDropMultiplierSkillEffect effect) {
                    if (!effect.isValid(stack)) return;
                    int extra = MathUtils.handleExtraDrops(player.getServerWorld(), context.get(LootContextParameters.ORIGIN), stack, effect.calculateChanceDrop(skillLevel, stack.getCount()));
                    if(extra > 0)
                        OSMCPlayerDataRegistry.bufferExp(player.getUuid(), skillID, (int) effect.calculateExp(skillLevel, extra));
                } else if(boundEffect.effect() instanceof BlockDropExtraSkillEffect effect) {
                    effect.getItems().forEach(itemKey -> {
                        Item item = Registries.ITEM.get(itemKey);
                        if(item == null) return;
                        int extra = MathUtils.handleExtraDrops(player.getServerWorld(), context.get(LootContextParameters.ORIGIN), new ItemStack(item), effect.calculateChanceDrop(skillLevel, stack.getCount()));
                        if(extra > 0)
                            OSMCPlayerDataRegistry.bufferExp(player.getUuid(), skillID, (int) effect.calculateExp(skillLevel, extra));
                    });
                }
            });
        }
        return stack;
    }

    public static Builder<?> builder() {
        return builder(BlockDropLootFunction::new);
    }
}
