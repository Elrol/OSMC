package dev.elrol.osmc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

public class OSMCCommand extends BaseCommand {

    @Override
    public void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("osmc")
                .then(literal("reload")
                        .requires(source -> {
                            //TODO change this to only allow ops / luckperms
                            return true;
                        })
                        .executes(OSMCCommand::reload))
                .then(literal("skill")
                        .then(argument("skill", IdentifierArgumentType.identifier())
                                .suggests((context, builder) -> CommandSource.suggestIdentifiers(SkillRegistry.getAll().keySet(), builder))
                                .executes(OSMCCommand::showSingleSkill)
                                .then(argument("target", EntityArgumentType.player())
                                        .executes(OSMCCommand::showOtherSingleSkill))))
                .then(literal("skills")
                        .executes(OSMCCommand::showSkills)
                        .then(argument("target", EntityArgumentType.player())
                                .executes(OSMCCommand::showOtherSkills)))
        );
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

        return 0;
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

        SkillRegistry.load();
        ExpSourceRegistry.rebuild(SkillRegistry.getAll(), context.getSource().getRegistryManager());

        MathUtils.load();

        OSMC.LOGGER.info("Loading all player skill data");
        PlayerDataRegistry.init();
        return 1;
    }

    private static void displayOneSkill(ServerCommandSource source, ServerPlayerEntity target, Identifier id) {
        sendSkillHeader(source, target);
        PlayerSkillData data = PlayerDataRegistry.get(target.getUuid());
        displaySkill(source, data.getSkillInfo(id), SkillRegistry.get(id));
    }

    private static void displayAllSkills(ServerCommandSource source, ServerPlayerEntity target) {
        sendSkillHeader(source, target);
        PlayerSkillData data = PlayerDataRegistry.get(target.getUuid());
        data.getSkillExpMap().keySet().forEach(id -> displaySkill(source, data.getSkillInfo(id), SkillRegistry.get(id)));
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
