package dev.elrol.osmc.data;

import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import net.minecraft.util.Identifier;

public record BoundSource<T extends ExpSource>(T source, Identifier skillID) {
}
