package dev.elrol.osmc.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public abstract class BaseCommand {

    public abstract void init(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment);

    public LiteralArgumentBuilder<ServerCommandSource> literal(String text) {
        return CommandManager.literal(text);
    }

    public RequiredArgumentBuilder<ServerCommandSource, ?> argument(String argument, ArgumentType<?> type) {
        return CommandManager.argument(argument, type);
    }

}
