package dev.elrol.osmc.mixin;

import dev.elrol.osmc.interfaces.IPlacedTracker;
import net.minecraft.world.chunk.ChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.BitSet;

@Mixin(ChunkSection.class)
public class ChunkSectionMixin implements IPlacedTracker {

    @Unique
    private final BitSet playerPlaced = new BitSet(4096);

    @Unique
    private int index(int localX, int localY, int localZ) {
        return (localY << 8) | (localZ << 4) | localX;
    }

    @Override
    public void osmc$setPlaced(int localX, int localY, int localZ) {
        playerPlaced.set(index(localX, localY, localZ), true);
    }

    @Override
    public boolean osmc$getPlaced(int localX, int localY, int localZ) {
        return playerPlaced.get(index(localX, localY, localZ));
    }

    @Override
    public void osmc$break(int localX, int localY, int localZ) {
        playerPlaced.set(index(localX, localY, localZ), false);
    }

}
