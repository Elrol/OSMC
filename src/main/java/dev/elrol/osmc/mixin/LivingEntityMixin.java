package dev.elrol.osmc.mixin;

import dev.elrol.osmc.data.BoundEffect;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.data.skill_effects.DamageMitigationSkillEffect;
import dev.elrol.osmc.events.LivingConsumeEvent;
import dev.elrol.osmc.libs.OSMCLootTracker;
import dev.elrol.osmc.registries.OSMCPlayerDataRegistry;
import dev.elrol.osmc.registries.OSMCSkillEffectRegistry;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Unique
    private LivingEntity self() { return (LivingEntity)(Object)this; }

    @Inject(method = "eatFood", at = @At(value = "HEAD"))
    public void osmc$eatFood(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
        LivingConsumeEvent.FOOD.invoker().consumed(self(), stack.copyWithCount(1));
    }

    @ModifyVariable(method = "modifyAppliedDamage", at = @At("HEAD"), argsOnly = true)
    public float osmc$modifyAppliedDamage(float amount, DamageSource source) {
        if(self() instanceof ServerPlayerEntity player) {
            List<BoundEffect<?>> effects = OSMCSkillEffectRegistry.getEffects(SkillTrigger.DAMAGE_RECEIVED, source.getTypeRegistryEntry());
            PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());

            float newAmount = amount;
            for (BoundEffect<?> effect : effects) {
                Identifier skillID = effect.skillID();
                int level = data.getSkillLevel(skillID);
                if (effect.effect() instanceof DamageMitigationSkillEffect damageMitigationEffect) {
                    if(damageMitigationEffect.getReqLevel() > level) continue;
                    newAmount = damageMitigationEffect.calculateDamage(level, newAmount);
                    OSMCPlayerDataRegistry.bufferExp(player, skillID, (int) damageMitigationEffect.calculateExp(level, (int) (amount - newAmount)));
                }
            }
            return newAmount;
        }

        return amount;
    }

    @Inject(method = "getLootTable", at = @At("RETURN"))
    public void osmc$getLootTable(CallbackInfoReturnable<RegistryKey<LootTable>> cir) {
        Identifier id = cir.getReturnValue().getValue();
        if(id != null) OSMCLootTracker.set(id);
    }
}
