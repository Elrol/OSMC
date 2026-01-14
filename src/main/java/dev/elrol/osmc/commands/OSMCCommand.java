package dev.elrol.osmc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.libs.MathUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.ExpSourceRegistry;
import dev.elrol.osmc.registries.PlayerDataRegistry;
import dev.elrol.osmc.registries.SkillRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class OSMCCommand extends BaseCommand {

    @Override
    public void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("osmc")
                .then(literal("reload")
                        .requires(source -> {
                            //TODO change this to only allow ops / luckperms
                            return source.hasPermissionLevel(4);
                        })
                        .executes(OSMCCommand::reload))
                .then(literal("skill")
                        .then(argument("skill", IdentifierArgumentType.identifier())
                                .suggests(OSMCCommand::SkillSuggestions)
                                .executes(OSMCCommand::showSingleSkill)
                                .then(argument("target", EntityArgumentType.player())
                                        .executes(OSMCCommand::showOtherSingleSkill))))
                .then(literal("skills")
                        .executes(OSMCCommand::showSkills)
                        .then(argument("target", EntityArgumentType.player())
                                .executes(OSMCCommand::showOtherSkills)))
                .then(literal("set")
                        .requires((source) -> {
                            //TODO change this to only allow ops / luckperms
                            return source.hasPermissionLevel(4);
                        })
                        .then(argument("skill", IdentifierArgumentType.identifier())
                                .suggests(OSMCCommand::SkillSuggestions)
                                .then(literal("level")
                                        .then(argument("level", IntegerArgumentType.integer(1))
                                                .executes(OSMCCommand::setSkillLevel)
                                                .then(argument("target", EntityArgumentType.player())
                                                        .executes(OSMCCommand::setPlayerSkillLevel))))
                                .then(literal("exp")
                                        .then(argument("exp", LongArgumentType.longArg(0))
                                                .executes(OSMCCommand::setSkillExp)
                                                .then(argument("target", EntityArgumentType.player())
                                                        .executes(OSMCCommand::setPlayerSkillExp))))
                        ))
        );
    }

    private static CompletableFuture<Suggestions> SkillSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(SkillRegistry.getAll().keySet(), builder);
    }

    private static int setPlayerSkillLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        if(player != null) {
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = SkillRegistry.get(skillID);
            if(skill == null) return 0;

            int level = IntegerArgumentType.getInteger(context, "level");
            long exp = (long) MathUtils.getTotalXPForLevel(skillID, skill.getLevelFormula(), level);

            setSkillExp(IdentifierArgumentType.getIdentifier(context, "skill"), player, exp);

            context.getSource().sendMessage(Text.literal("Set ")
                    .append(player.getDisplayName())
                    .append("'s ")
                    .append(skill.getTextName())
                    .append(" level to " + level));
        } else {
            context.getSource().sendMessage(Text.literal("That player was invalid"));
        }
        return 1;

    }

    private static int setPlayerSkillExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        if(player != null) {
            long exp = LongArgumentType.getLong(context, "exp");
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = SkillRegistry.get(skillID);

            if(skill == null) return 0;

            setSkillExp(skillID, player, exp);
            context.getSource().sendMessage(Text.literal("Set ")
                    .append(player.getDisplayName())
                    .append("'s ")
                    .append(skill.getTextName())
                    .append(" exp to " + exp));
        } else {
            context.getSource().sendMessage(Text.literal("That player was invalid"));
        }
        return 1;
    }

    private static int setSkillLevel(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(source.isExecutedByPlayer()) {
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = SkillRegistry.get(skillID);
            ServerPlayerEntity player = source.getPlayer();
            if(player == null || skill == null) return 0;

            int level = IntegerArgumentType.getInteger(context, "level");
            long exp = (long) MathUtils.getTotalXPForLevel(skillID, skill.getLevelFormula(), level);

            setSkillExp(IdentifierArgumentType.getIdentifier(context, "skill"), player, exp);

            context.getSource().sendMessage(Text.literal("Set ")
                    .append(player.getDisplayName())
                    .append("'s ")
                    .append(skill.getTextName())
                    .append(" level to " + level));
        } else {
            source.sendMessage(Text.literal("You need to be a player to change your own skill level"));
        }
        return 1;
    }

    private static int setSkillExp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(source.isExecutedByPlayer()) {
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = SkillRegistry.get(skillID);
            ServerPlayerEntity player = source.getPlayer();
            if(player == null || skill == null) return 0;

            long exp = LongArgumentType.getLong(context, "exp");
            setSkillExp(IdentifierArgumentType.getIdentifier(context, "skill"), player, exp);
            context.getSource().sendMessage(Text.literal("Set ")
                    .append(player.getDisplayName())
                    .append("'s ")
                    .append(skill.getTextName())
                    .append(" exp to " + exp));
        } else {
            source.sendMessage(Text.literal("You need to be a player to change your own skill exp"));
        }
        return 1;
    }

    private static void setSkillExp(Identifier skillID, ServerPlayerEntity player, long exp) {
        PlayerSkillData data = PlayerDataRegistry.get(player.getUuid());
        data.setSkillExp(skillID, exp);
        PlayerDataRegistry.updatePlayerData(data);
    }

    private static void changeSkillExp(Identifier skillID, ServerPlayerEntity player, long exp) {
        PlayerSkillData data = PlayerDataRegistry.get(player.getUuid());
        data.addSkillExp(skillID, exp);
        PlayerDataRegistry.updatePlayerData(data);
    }

    private static int showSkills(CommandContext<ServerCommandSource> context) {
        if(context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            displayAllSkills(context.getSource(), player);
            return 1;
        }
        context.getSource().sendMessage(Text.literal("Only Players Have Skills").formatted(Formatting.RED));
        return 0;
    }

    private static int showOtherSkills(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        displayAllSkills(context.getSource(), player);

        return 1;
    }

    private static int showSingleSkill(CommandContext<ServerCommandSource> context) {
        if(context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            displayOneSkill(context.getSource(), player, context.getArgument("skill", Identifier.class));
            return 1;
        }
        context.getSource().sendMessage(Text.literal("Only Players Have Skills").formatted(Formatting.RED));
        return 0;
    }

    private static int showOtherSingleSkill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        displayOneSkill(context.getSource(), player, context.getArgument("skill", Identifier.class));

        return 0;
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Reloading OSMC configs and data").formatted(Formatting.GREEN));

        OSMC.CONFIG = OSMC.CONFIG.load();

        SkillRegistry.load(context.getSource().getServer());
        ExpSourceRegistry.rebuild(SkillRegistry.getAll(), context.getSource().getRegistryManager());

        MathUtils.load();

        OSMC.LOGGER.info("Loading all player skill data");
        PlayerDataRegistry.init();
        return 1;
    }

    private static void displayOneSkill(ServerCommandSource source, ServerPlayerEntity target, Identifier id) {
        sendSkillHeader(source, target);
        PlayerSkillData data = PlayerDataRegistry.get(target.getUuid());
        displaySkill(source, data.getSkillInfo(id), Objects.requireNonNull(SkillRegistry.get(id)));
    }

    private static void displayAllSkills(ServerCommandSource source, ServerPlayerEntity target) {
        sendSkillHeader(source, target);
        PlayerSkillData data = PlayerDataRegistry.get(target.getUuid());
        data.getSkillExpMap().keySet().forEach(id -> displaySkill(source, data.getSkillInfo(id), Objects.requireNonNull(SkillRegistry.get(id))));
    }

    private static void sendSkillHeader(ServerCommandSource source, ServerPlayerEntity target) {
        source.sendMessage(Text.empty().append(target.getDisplayName()).append(Text.literal("'s Stats:").formatted(Formatting.BOLD, Formatting.UNDERLINE)));
    }

    private static void displaySkill(ServerCommandSource source, PlayerSkillData.SkillExpInfo info, Skill skill) {
        source.sendMessage(Text.empty()
                .append(OSMCConstants.osmcTag())
                .append(skill.getTextName())
                .append(Text.literal(" " + info.level() + " [ "))
                .append(Text.literal(String.valueOf(info.currentExp())).formatted(Formatting.YELLOW))
                .append(Text.literal(" / "))
                .append(Text.literal(String.valueOf(info.targetExp())).formatted(Formatting.GREEN))
                .append(Text.literal(" ]")));
    }
}
