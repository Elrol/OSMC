package dev.elrol.osmc.data;

import net.minecraft.util.Identifier;

public record BoundSource<T extends ExpSource>(T source, Identifier skillID) {
}
