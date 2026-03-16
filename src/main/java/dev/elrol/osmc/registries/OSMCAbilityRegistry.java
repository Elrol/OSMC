package dev.elrol.osmc.registries;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.Ability;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.Skill;
import dev.elrol.osmc.data.ability_effects.CooldownAbilityEffect;
import dev.elrol.osmc.data.ability_effects.DurationAbilityEffect;
import dev.elrol.osmc.data.ability_effects.ShapeBreakAbilityEffect;
import dev.elrol.osmc.libs.JsonUtils;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.libs.SkillUtils;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OSMCAbilityRegistry {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    private static final Map<Identifier, Ability> ABILITY_MAP = new HashMap<>();

    public static final Map<UUID, Long> LAST_CLICK_CACHE = new HashMap<>();
    public static final Map<UUID, Long> ABILITY_COOLDOWN_CACHE = new HashMap<>();
    public static final Map<UUID, Skill> ACTIVE_ABILITY_CACHE = new HashMap<>();

    public static void init(MinecraftServer server){
        load(server);
    }

    public static void register(Ability ability) {
        if(ability.isEnabled())
            ABILITY_MAP.put(ability.getID(), ability);
    }

    public static void activateAbility(ServerPlayerEntity player, Skill skill) {
        Ability ability = get(skill.getAbilityID());
        MinecraftServer server = player.getServer();
        if(server == null || hasActiveAbility(player) || ability == null) return;

        if(hasCooldown(player)) {
            long start = ABILITY_COOLDOWN_CACHE.getOrDefault(player.getUuid(), 0L);
            long current = System.currentTimeMillis();
            long diff = ((current - start) / 1000);
            int cooldown = SkillUtils.getPlayerAbilityCooldown(player, skill.getID());
            if(cooldown > diff) {
                player.sendMessage(Text.empty()
                        .append(Text.literal("Ability ").formatted(Formatting.RED))
                        .append(ability.getDisplayName())
                        .append(Text.literal(" is on cooldown for " + (cooldown - diff) + " seconds").formatted(Formatting.RED)));
                return;
            }
        }

        ACTIVE_ABILITY_CACHE.put(player.getUuid(), skill);
        player.sendMessage(Text.empty().append(Text.literal("Activated ability: ").formatted(Formatting.GREEN)).append(ability.getDisplayName()));
        player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH);

        int durationSeconds = ability.getBaseDuration();
        for (AbilityEffect effect : ability.getEffects()) {
            if(effect instanceof DurationAbilityEffect durationEffect) {
                durationSeconds += durationEffect.getExtraSeconds();
            }
        }

        if(durationSeconds > 0) {
            SCHEDULER.schedule(() ->
                server.execute(() -> deactivateAbility(player))
            , durationSeconds, TimeUnit.SECONDS);
        }
    }

    public static void deactivateAbility(ServerPlayerEntity player) {
        Skill skill = ACTIVE_ABILITY_CACHE.remove(player.getUuid());
        if(skill != null) {
            Ability ability = OSMCAbilityRegistry.ABILITY_MAP.get(skill.getAbilityID());
            Text name = Text.of(skill.getAbilityID());
            if(ability != null) name = ability.getDisplayName();

            player.sendMessage(Text.empty().append(Text.literal("Ability wore off ").formatted(Formatting.RED)).append(name));
            player.playSound(SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST);
            ABILITY_COOLDOWN_CACHE.put(player.getUuid(), System.currentTimeMillis());
        }
    }

    public static boolean hasActiveAbility(UUID uuid) {
        return ACTIVE_ABILITY_CACHE.containsKey(uuid);
    }

    public static boolean hasActiveAbility(ServerPlayerEntity player) {
        return hasActiveAbility(player.getUuid());
    }

    public static boolean hasCooldown(UUID uuid) {
        return ABILITY_COOLDOWN_CACHE.containsKey(uuid);
    }

    public static boolean hasCooldown(ServerPlayerEntity player) {
        return hasCooldown(player.getUuid());
    }

    @Nullable
    public static Ability get(Identifier id) {
        return ABILITY_MAP.get(id);
    }

    public static void save(Ability ability, MinecraftServer server) {
        Codec<Ability> codec = Ability.CODEC;
        RegistryOps<JsonElement> registryOps = server.getRegistryManager().getOps(JsonOps.INSTANCE);
        codec.encodeStart(registryOps, ability)
                .ifError(error -> OSMC.LOGGER.error(error.message()))
                .ifSuccess(json -> JsonUtils.saveToJson(OSMCConstants.ABILITY_CONFIG_DIR, ability.getID().getPath() + ".json", json));
    }

    public static void save(MinecraftServer server) {
        ABILITY_MAP.forEach((id, ability) -> save(ability, server));
    }

    public static void load(MinecraftServer server) {
        ABILITY_MAP.clear();
        File[] files = OSMCConstants.ABILITY_CONFIG_DIR.listFiles(file -> file.getName().endsWith(".json"));
        if(files == null || files.length == 0) {
            registerExampleAbility(server);
            return;
        }

        for(File file : files) {
            JsonElement json = JsonUtils.loadFromJson(OSMCConstants.ABILITY_CONFIG_DIR, file.getName(), null);

            if(json != null) {
                RegistryOps<JsonElement> registryOps = server.getRegistryManager().getOps(JsonOps.INSTANCE);
                Ability.CODEC.parse(registryOps, json)
                        .resultOrPartial(OSMC.LOGGER::error)
                        .ifPresent(OSMCAbilityRegistry::register);
            } else {
                OSMC.LOGGER.error("Ability failed to load from: {}", file);
            }
        }
    }

    private static void registerExampleAbility(MinecraftServer server) {
        Ability ability = new Ability(OSMCConstants.osmcID("example_ability"));

        DurationAbilityEffect daEffect = new DurationAbilityEffect(OSMCConstants.osmcID("example_ability_effect"), 0, 5);
        daEffect.setDisplayName(Text.literal("Example Duration Effect"));
        daEffect.setDescription(Text.literal("This increases your ability by 5 seconds").formatted(Formatting.GRAY));
        ability.addAbilityEffect(daEffect);

        CooldownAbilityEffect caEffect = new CooldownAbilityEffect(OSMCConstants.osmcID("cooldown_ability_effect"), 0, 5);
        caEffect.setDisplayName(Text.literal("Example Cooldown Effect").formatted(Formatting.BLUE));
        caEffect.setDescription(Text.literal("This increases your ability by 5 seconds").formatted(Formatting.GRAY));
        ability.addAbilityEffect(caEffect);

        ShapeBreakAbilityEffect sbEffect = new ShapeBreakAbilityEffect(OSMCConstants.osmcID("shape_break_ability_effect"), 0, 1);
        sbEffect.setDisplayName(Text.literal("Example Shape Break Ability").formatted(Formatting.GREEN));
        sbEffect.setDescription(Text.literal("Increases the number of blocks you can break").formatted(Formatting.GRAY));

        save(ability, server);
    }

}
