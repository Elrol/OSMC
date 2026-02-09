package dev.elrol.osmc.mixin;

import dev.elrol.osmc.events.LootContextBuildEvent;
import dev.elrol.osmc.libs.OSMCLootTracker;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LootContext.Builder.class)
public class LootContextMixin {

    @Shadow @Final
    private LootContextParameterSet parameters;

    @SuppressWarnings("all")
    @Inject(method = "build", at = @At("HEAD"))
    public void osmc$build(Optional<Identifier> randomId, CallbackInfoReturnable<LootContext> cir) {
        randomId.ifPresent(OSMCLootTracker::set);
    }

}
