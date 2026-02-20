package dev.elrol.osmc.registries;

import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.libs.SkillUtils;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class OSMCPlaceholderRegistry {

    public static void init() {
        register("skill-level", (ctx, arg) -> {
            if(arg == null) return PlaceholderResult.invalid("No argument!");
            if(ctx.hasPlayer() && ctx.player() instanceof ServerPlayerEntity player) {
                PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
                Identifier id = Identifier.tryParse(arg);
                return PlaceholderResult.value(String.valueOf(data.getSkillLevel(id)));
            }
            return PlaceholderResult.invalid("No player!");
        });

        register("skill-exp", (ctx, arg) -> {
            if(arg == null) return PlaceholderResult.invalid("No argument!");
            if(ctx.hasPlayer() && ctx.player() instanceof ServerPlayerEntity player) {
                PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
                Identifier id = Identifier.tryParse(arg);
                return PlaceholderResult.value(String.valueOf(data.getSkillExp(id)));
            }
            return PlaceholderResult.invalid("No player!");
        });

        register("skill-name", (ctx, arg) -> {
            if(arg == null) return PlaceholderResult.invalid("No argument!");
            Identifier id = Identifier.tryParse(arg);
            Skill skill = OSMCSkillRegistry.get(id);
            if(skill == null) return PlaceholderResult.invalid("No such skill!");
            return PlaceholderResult.value(skill.getTextName());
        });

        register("trainer-level", (ctx, arg) -> {
            if(ctx.hasPlayer() && ctx.player() instanceof ServerPlayerEntity player) {
                return PlaceholderResult.value(String.valueOf(SkillUtils.getPlayerTrainerLevel(player)));
            }
            return PlaceholderResult.invalid("No player!");
        });
    }

    public static void register(String placeholder, PlaceholderHandler function) {
        register(OSMCConstants.osmcID(placeholder), function);
    }

    public static void register(String key, String placeholder, PlaceholderHandler function) {
        register(Identifier.of(key, placeholder), function);
    }

    public static void register(Identifier id, PlaceholderHandler function) {
        Placeholders.register(id, function);
    }
}
