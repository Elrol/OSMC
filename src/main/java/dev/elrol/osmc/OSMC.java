package dev.elrol.osmc;

import de.tomalbrc.filament.api.FilamentLoader;
import dev.elrol.osmc.config.OSMCConfig;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.*;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSMC implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(OSMCConstants.MODID);
    public static OSMCConfig CONFIG = new OSMCConfig();

    @Override
    public void onInitialize() {
        registerMod(OSMCConstants.MODID);

        CONFIG = CONFIG.load();
        OSMCExpSourceTypeRegistry.init();
        OSMCSkillEffectTypeRegistry.init();
        OSMCAbilityEffectTypeRegistry.init();
        OSMCLootFunctionRegistry.init();
        OSMCEventRegistry.init();
        OSMCCobblemonTierRegistry.init();
        OSMCPlaceholderRegistry.init();
    }

    private void registerMod(String modid) {
        FilamentLoader.loadModels(modid, modid);
        FilamentLoader.loadItems(modid);
        FilamentLoader.loadBlocks(modid);
        PolymerResourcePackUtils.addModAssets(modid);
    }
}
