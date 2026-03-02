package dev.elrol.osmc.interfaces;

public interface IPlacedTracker {

    void osmc$setPlaced(int localX, int localY, int localZ);

    boolean osmc$getPlaced(int localX, int localY, int localZ);

    void osmc$break(int localX, int localY, int localZ);

}
