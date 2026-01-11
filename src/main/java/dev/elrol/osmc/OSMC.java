package dev.elrol.osmc;

import dev.elrol.osmc.config.OSMCConfig;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.*;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSMC implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(OSMCConstants.MODID);
    public static OSMCConfig CONFIG = new OSMCConfig();

    @Override
    public void onInitialize() {
        CONFIG = CONFIG.load();
        ExpSourceTypeRegistry.init();
        SkillRegistry.init();

        PlayerDataRegistry.init();
        OSMCEventRegistry.init();


    }
}
