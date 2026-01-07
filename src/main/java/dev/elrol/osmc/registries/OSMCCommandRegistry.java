package dev.elrol.osmc.registries;

import com.mojang.brigadier.CommandDispatcher;
import dev.elrol.osmc.commands.OSMCCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class OSMCCommandRegistry {

    public static void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        new OSMCCommand().init(dispatcher, access, environment);
    }
}
