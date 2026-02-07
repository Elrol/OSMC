package dev.elrol.osmc.data;

import net.minecraft.util.Identifier;

public record BoundEffect<T extends SkillEffect>(T effect, Identifier skillID) {
}
