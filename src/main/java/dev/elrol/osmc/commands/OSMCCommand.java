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
import dev.elrol.osmc.libs.SkillUtils;
import dev.elrol.osmc.menus.DevMenu;
import dev.elrol.osmc.menus.OSMCMenu;
import dev.elrol.osmc.registries.*;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class OSMCCommand extends BaseCommand {

    @Override
    public void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("osmc")
                .executes(this::osmcInfo)
                .then(literal("reload")
                        .requires(source -> {
                            //TODO change this to only allow ops / luckperms
                            return source.hasPermissionLevel(4);
                        })
                        .executes(this::reload))
                .then(literal("skill")
                        .then(argument("skill", IdentifierArgumentType.identifier())
                                .suggests(this::SkillSuggestions)
                                .executes(this::showSingleSkill)
                                .then(argument("target", EntityArgumentType.player())
                                        .executes(this::showOtherSingleSkill))))
                .then(literal("skills")
                        .executes(this::showSkills)
                        .then(argument("target", EntityArgumentType.player())
                                .executes(this::showOtherSkills)))
                .then(literal("set")
                        .requires((source) -> {
                            //TODO change this to only allow ops / luckperms
                            return source.hasPermissionLevel(4);
                        })
                        .then(argument("skill", IdentifierArgumentType.identifier())
                                .suggests(this::SkillSuggestions)
                                .then(literal("level")
                                        .then(argument("level", IntegerArgumentType.integer(1))
                                                .executes(this::setSkillLevel)
                                                .then(argument("target", EntityArgumentType.player())
                                                        .executes(this::setPlayerSkillLevel))))
                                .then(literal("exp")
                                        .then(argument("exp", LongArgumentType.longArg(0))
                                                .executes(this::setSkillExp)
                                                .then(argument("target", EntityArgumentType.player())
                                                        .executes(this::setPlayerSkillExp))))
                        ))
                .then(literal("leaderboard")
                        .then(argument("skill", IdentifierArgumentType.identifier())
                                .suggests(this::SkillSuggestions)
                                .executes(this::displayLeaderboard)))
                .then(literal("menu")
                        .executes(this::openMenu))
        );
    }

    private CompletableFuture<Suggestions> SkillSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(OSMCSkillRegistry.getAll().keySet(), builder);
    }

    private int osmcInfo(CommandContext<ServerCommandSource> context) {
        List<MutableText> texts = new ArrayList<>();

        if(context.getSource().getPlayer() instanceof ServerPlayerEntity player) {
            int level = SkillUtils.getPlayerTrainerLevel(player);
            int totalLevel = SkillUtils.getTotalSkillLevel(player);
            List<Identifier> subskills = OSMC.CONFIG.getTrainerLevel().getSubskills();
            PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());

            texts.add(Text.empty()
                    .append(player.getDisplayName())
                    .append(Text.literal("'s Levels:"))
                    .formatted(Formatting.BOLD));

            texts.add(Text.empty()
                    .append(Text.literal("   Trainer Level").formatted(Formatting.GRAY))
                    .append(" : " + level));

            subskills.stream().sorted().forEach(id -> {
                Skill skill = OSMCSkillRegistry.get(id);
                if(skill == null) return;
                texts.add(Text.literal("      - ").append(skill.getTextName()).append(Text.literal(" : " + data.getSkillLevel(id))));
            });

            texts.add(Text.empty()
                    .append(Text.literal("   Total Skill Levels").formatted(Formatting.GRAY))
                    .append(" : " + totalLevel));
        }

        packageTexts(context.getSource(), texts);
        return 1;
    }

    private int setPlayerSkillLevel(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        if(player != null) {
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = OSMCSkillRegistry.get(skillID);
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

    private int setPlayerSkillExp(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        if(player != null) {
            long exp = LongArgumentType.getLong(context, "exp");
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = OSMCSkillRegistry.get(skillID);

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

    private int setSkillLevel(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(source.isExecutedByPlayer()) {
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = OSMCSkillRegistry.get(skillID);
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

    private int setSkillExp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        if(source.isExecutedByPlayer()) {
            Identifier skillID = IdentifierArgumentType.getIdentifier(context, "skill");
            Skill skill = OSMCSkillRegistry.get(skillID);
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

    private void setSkillExp(Identifier skillID, ServerPlayerEntity player, long exp) {
        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
        data.setSkillExp(skillID, exp);
        OSMCPlayerDataRegistry.updatePlayerData(data);
    }

    private void changeSkillExp(Identifier skillID, ServerPlayerEntity player, long exp) {
        PlayerSkillData data = OSMCPlayerDataRegistry.get(player.getUuid());
        data.addSkillExp(skillID, exp);
        OSMCPlayerDataRegistry.updatePlayerData(data);
    }

    private int showSkills(CommandContext<ServerCommandSource> context) {
        if(context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            displayAllSkills(context.getSource(), player);
            return 1;
        }
        context.getSource().sendMessage(Text.literal("Only Players Have Skills").formatted(Formatting.RED));
        return 0;
    }

    private int showOtherSkills(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        displayAllSkills(context.getSource(), player);

        return 1;
    }

    private int showSingleSkill(CommandContext<ServerCommandSource> context) {
        if(context.getSource().isExecutedByPlayer()) {
            ServerPlayerEntity player = context.getSource().getPlayer();
            displayOneSkill(context.getSource(), player, context.getArgument("skill", Identifier.class));
            return 1;
        }
        context.getSource().sendMessage(Text.literal("Only Players Have Skills").formatted(Formatting.RED));
        return 0;
    }

    private int showOtherSingleSkill(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "target");
        displayOneSkill(context.getSource(), player, context.getArgument("skill", Identifier.class));

        return 0;
    }

    private int reload(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Reloading OSMC configs and data").formatted(Formatting.GREEN));

        OSMC.CONFIG = OSMC.CONFIG.load();

        OSMCAbilityRegistry.load(context.getSource().getServer());
        OSMCSkillRegistry.load(context.getSource().getServer());

        OSMCExpSourceRegistry.rebuild(OSMCSkillRegistry.getAll(), context.getSource().getRegistryManager());
        OSMCSkillEffectRegistry.rebuild(OSMCSkillRegistry.getAll(), context.getSource().getRegistryManager());
        OSMCCobblemonTierRegistry.init();

        MathUtils.load();

        OSMC.LOGGER.info("Loading all player skill data");
        OSMCPlayerDataRegistry.init();

        OSMCLeaderboard.populate();
        return 1;
    }

    private void displayOneSkill(ServerCommandSource source, ServerPlayerEntity target, Identifier id) {
        List<MutableText> texts = getSkillHeader(target);
        PlayerSkillData data = OSMCPlayerDataRegistry.get(target.getUuid());
        texts.add(getSkill(data.getSkillInfo(id), Objects.requireNonNull(OSMCSkillRegistry.get(id))));
        packageTexts(source, texts);
    }

    private void displayAllSkills(ServerCommandSource source, ServerPlayerEntity target) {
        List<MutableText> texts = getSkillHeader(target);
        PlayerSkillData data = OSMCPlayerDataRegistry.get(target.getUuid());
        OSMCSkillRegistry.getAll().keySet().forEach(id -> texts.add(getSkill(data.getSkillInfo(id), Objects.requireNonNull(OSMCSkillRegistry.get(id)))));
        packageTexts(source, texts);
    }

    private List<MutableText> getSkillHeader(ServerPlayerEntity target) {
        return new ArrayList<>(List.of(Text.empty().append(target.getDisplayName()).append(Text.literal("'s Stats:")).formatted(Formatting.BOLD)));
    }

    private MutableText getSkill(PlayerSkillData.SkillExpInfo info, Skill skill) {
        return Text.literal("   ")
                .append(skill.getTextName())
                .append(Text.literal(" : " + info.level() + " [ "))
                .append(Text.literal(String.valueOf(info.currentExp())).formatted(Formatting.YELLOW))
                .append(Text.literal(" / "))
                .append(Text.literal(String.valueOf(info.targetExp())).formatted(Formatting.GREEN))
                .append(Text.literal(" ]"));
    }

    private int displayLeaderboard(CommandContext<ServerCommandSource> context) {
        Identifier id = context.getArgument("skill", Identifier.class);
        List<OSMCLeaderboard.Entry> entries = OSMCLeaderboard.get(id);

        ServerCommandSource source = context.getSource();
        List<MutableText> texts = getLeaderboardHeader(id);

        int rank = 1;
        for (OSMCLeaderboard.Entry entry : entries) {
            texts.add(getEntry(id, entry, rank));
            rank++;
        }
        packageTexts(source, texts);
        return 1;
    }

    private List<MutableText> getLeaderboardHeader(Identifier skillID) {
        List<MutableText> texts = new ArrayList<>();
        Skill skill = OSMCSkillRegistry.get(skillID);
        if(skill == null) {
            texts.add(Text.literal("Skill not found").formatted(Formatting.RED));
            return texts;
        }
        texts.add(Text.empty()
                .append(skill.getTextName())
                .append(" Leaderboard:"));
        return texts;
    }

    private MutableText getEntry(Identifier skillID, OSMCLeaderboard.Entry entry, int rank) {
        Formatting color = Formatting.GRAY;
        PlayerSkillData data = OSMCPlayerDataRegistry.get(entry.uuid());
        return Text.literal(" ").append(Text.literal(rank > 9 ? "│" + rank + "│ " : "│ " + rank + "│ ").formatted(color))
                .append(data.getUsername())
                .styled(s -> s.withHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        Text.literal("Level " + data.getSkillLevel(skillID) + " │ " + entry.exp() + " exp").formatted(color)))
                );
    }

    private void packageTexts(ServerCommandSource source, List<MutableText> texts) {
        if(texts.isEmpty()) return;

        MutableText output = texts.stream()
                .reduce((first, second) -> first.append("\n").append(second))
                .orElse(Text.empty());

        if(!output.equals(Text.empty())) {
            source.sendMessage(output);
        }
    }

    private int openMenu(CommandContext<ServerCommandSource> context) {
        if(context.getSource().getPlayer() instanceof ServerPlayerEntity player) {
            OSMCMenu osmcMenu = new OSMCMenu(player);
            osmcMenu.open();
            return 1;
        }
        return 0;
    }
}
