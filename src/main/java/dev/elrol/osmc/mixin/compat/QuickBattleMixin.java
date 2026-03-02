package dev.elrol.osmc.mixin.compat;

import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.events.QuickBattleEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quick.battle.battle.QuickBattleCalculator;
import quick.battle.battle.QuickBattleResult;

@Mixin(QuickBattleCalculator.class)
public class QuickBattleMixin {

    @Inject(
            method = "calculateBattle",
            at = @At("RETURN"),
            remap = false
    )
    private static void calculateBattle(Object attackerPokemon, Object defenderPokemon, CallbackInfoReturnable<QuickBattleResult> cir) {
        if(attackerPokemon instanceof Pokemon attacker && defenderPokemon instanceof Pokemon defender) {
            QuickBattleEvent.EVENT.invoker().battleEnd(attacker, defender, cir.getReturnValue());
        } else {
            OSMC.LOGGER.error("Not pokemon. Attacker {}. Defender {}", attackerPokemon.getClass().getName(), defenderPokemon.getClass().getName());
        }
    }

}
