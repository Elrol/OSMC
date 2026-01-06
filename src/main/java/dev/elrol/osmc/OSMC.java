package dev.elrol.osmc;

import dev.elrol.osmc.config.OSMCConfig;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCEventRegistry;
import dev.elrol.osmc.registries.PlayerDataRegistry;
import dev.elrol.osmc.registries.SkillDataTypeRegistry;
import dev.elrol.osmc.registries.SkillRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSMC implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(OSMCConstants.MODID);
    public static OSMCConfig CONFIG = new OSMCConfig();

    @Override
    public void onInitialize() {
        CONFIG = CONFIG.load();
        SkillDataTypeRegistry.init();
        SkillRegistry.init();

        PlayerDataRegistry.init();
        OSMCEventRegistry.init();
    }
}
