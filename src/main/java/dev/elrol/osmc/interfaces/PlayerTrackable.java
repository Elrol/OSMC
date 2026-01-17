package dev.elrol.osmc.interfaces;

import java.util.UUID;

public interface PlayerTrackable {

    void osmc$setLastPlayer(UUID uuid);
    UUID osmc$getLastPlayer();
    void osmc$clearLastPlayer();

}
