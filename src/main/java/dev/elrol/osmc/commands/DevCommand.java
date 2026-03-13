package dev.elrol.osmc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import dev.elrol.osmc.menus.DevMenu;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class DevCommand extends BaseCommand {

    @Override
    public void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("dev")
                .executes(this::noArgs));
    }

    private int noArgs(CommandContext<ServerCommandSource> context) {
        if(context.getSource().getPlayer() instanceof ServerPlayerEntity player) {
            DevMenu devMenu = new DevMenu(player);
            devMenu.open();
            return 1;
        }
        return 0;
    }
}
