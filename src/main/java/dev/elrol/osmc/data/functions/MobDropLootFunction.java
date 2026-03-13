package dev.elrol.osmc.data.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.BoundEffect;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.data.skill_effects.MobDropMultiplierSkillEffect;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.registries.OSMCLootFunctionRegistry;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillEffectRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class MobDropLootFunction extends ConditionalLootFunction {

    public static final MapCodec<MobDropLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            addConditionsField(instance).apply(instance, MobDropLootFunction::new));

    protected MobDropLootFunction(List<LootCondition> conditions) {
        super(conditions);
    }

    @Override
    public LootFunctionType<? extends ConditionalLootFunction> getType() {
        return OSMCLootFunctionRegistry.MOB_DROP_MULTIPLIER_FUNCTION_TYPE;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if(context.get(LootContextParameters.THIS_ENTITY) instanceof ServerPlayerEntity player) {
            List<BoundEffect<?>> boundEffects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.ENTITY_DROP, stack.getItem());

            boundEffects.forEach(boundEffect -> {
                PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
                Identifier skillID = boundEffect.skillID();
                int skillLevel = data.getSkillLevel(skillID);
                int reqLevel = boundEffect.effect().getReqLevel();
                if(skillLevel < reqLevel) return;

                if(boundEffect.effect() instanceof MobDropMultiplierSkillEffect effect) {
                    int extra = MathUtils.handleExtraDrops(player.getServerWorld(), context.get(LootContextParameters.ORIGIN), stack, effect.calculateChanceDrop(skillLevel, stack.getCount()));
                    if(extra > 0 )
                        OSMCPlayerDataRegistry.bufferExp(player.getUuid(), skillID, (int) effect.calculateExp(skillLevel, extra));
                }
            });
        }
        return stack;
    }

    public static Builder<?> builder() {
        return builder(MobDropLootFunction::new);
    }
}
