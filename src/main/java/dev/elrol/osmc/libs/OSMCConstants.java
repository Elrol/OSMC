package dev.elrol.osmc.libs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.elrol.osmc.data.skills.SkillDataType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.io.File;

public class OSMCConstants {

    public static final String MODID = "osmc";
    public static final Identifier SKILL_TYPE_ID = Identifier.of(MODID, "skill_data_type");
    public static final RegistryKey<Registry<SkillDataType<?>>> SKILL_TYPE_KEY = RegistryKey.ofRegistry(SKILL_TYPE_ID);

    public static final File ROOT_DIR = FabricLoader.getInstance().getGameDir().toFile();
    public static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "OSMC");
    public static final File SKILL_CONFIG_DIR = new File(CONFIG_DIR, "Skills");

    public static final File OSMC_DATA_DIR = new File(ROOT_DIR, "OSMC");
    public static final File PLAYER_DATA_DIR = new File(OSMC_DATA_DIR, "player_data");

    public static final String MINING_ID = "mining";
    public static final String WOODCUTTING_ID = "woodcutting";

    public static Gson makeGSON() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }

}
