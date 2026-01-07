package dev.elrol.osmc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.registries.ExpSourceRegistry;
import dev.elrol.osmc.registries.PlayerDataRegistry;
import dev.elrol.osmc.registries.SkillRegistry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class OSMCCommand extends BaseCommand {

    @Override
    public void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("osmc")
                .then(literal("reload")
                        .requires(source -> {
                            //TODO change this to only allow ops / luckperms
                            return true;
                        })
                        .executes(OSMCCommand::reload)));
    }

    private static int reload(CommandContext<ServerCommandSource> context) {
        context.getSource().sendMessage(Text.literal("Reloading OSMC configs and data").formatted(Formatting.GREEN));

        OSMC.CONFIG = OSMC.CONFIG.load();

        SkillRegistry.load();
        ExpSourceRegistry.rebuild(SkillRegistry.getAll());

        OSMC.LOGGER.info("Loading all player skill data");
        PlayerDataRegistry.init();
        return 1;
    }
}
