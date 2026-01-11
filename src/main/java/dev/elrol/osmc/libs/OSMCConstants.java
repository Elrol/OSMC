package dev.elrol.osmc.libs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.PlayerSkillData;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.registries.PlayerDataRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.io.File;

public class OSMCConstants {

    public static final String MODID = "osmc";

    // Identifiers
    public static final Identifier EXP_TYPE_ID = Identifier.of(MODID, "exp_data_type");

    // Registry Keys
    public static final RegistryKey<Registry<ExpSourceType<?>>> EXP_TYPE_KEY = RegistryKey.ofRegistry(EXP_TYPE_ID);

    // Files
    public static final File ROOT_DIR = FabricLoader.getInstance().getGameDir().toFile();
    public static final File CONFIG_DIR = new File(FabricLoader.getInstance().getConfigDir().toFile(), "OSMC");
    public static final File SKILL_CONFIG_DIR = new File(CONFIG_DIR, "Skills");

    public static final File OSMC_DATA_DIR = new File(ROOT_DIR, "OSMC");
    public static final File PLAYER_DATA_DIR = new File(OSMC_DATA_DIR, "player_data");

    // Exp Source Type IDs
    public static final String BLOCK_BREAK_EXP_ID = "block_break";
    public static final String BLOCK_INTERACT_EXP_ID = "block_interact";
    public static final String CONSUME_FOOD_EXP_ID = "consume_food";
    public static final String CONSUME_POTION_EXP_ID = "consume_potion";
    public static final String CRAFT_EXP_ID = "craft";
    public static final String ENCHANT_EXP_ID = "enchant";
    public static final String ENTITY_INTERACTION_EXP_ID = "entity_interact";
    public static final String ENTITY_KILL_EXP_ID = "entity_kill";
    public static final String ITEM_USE_EXP_ID = "item_use";
    public static final String POTION_BREW_EXP_ID = "potion_brew";
    public static final String VILLAGER_TRADE_EXP_ID = "villager_trade";

    public static Gson makeGSON() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    }
    public static Identifier osmcID(String id) { return Identifier.of(MODID, id); }
    public static Text osmcTag() {
        MutableText tag = Text.empty();

        tag.append(Text.literal("[").formatted(Formatting.GRAY));
        tag.append(Text.literal("OSMC").formatted(Formatting.AQUA));
        tag.append(Text.literal("] ").formatted(Formatting.GRAY));

        return tag;
    }

    public static void sendXpGainMsg(ServerPlayerEntity player, String action, String target, int exp, Skill skill) {
        PlayerSkillData data = PlayerDataRegistry.get(player.getUuid());
        PlayerSkillData.SkillExpInfo info = data.getSkillInfo(skill.getID());

        player.sendMessage(Text.empty()
                .append(Text.literal("[").formatted(Formatting.DARK_GRAY))
                .append(skill.getTextName())
                .append(Text.literal("]").formatted(Formatting.DARK_GRAY))
                .append(Text.literal(" " + action + " " + target + " and got " + exp + "xp. " + info.level() + " [" + info.currentExp() + "/" + info.targetExp() + "]")));
    }

}
