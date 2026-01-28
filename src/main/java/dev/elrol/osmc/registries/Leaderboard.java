package dev.elrol.osmc.registries;

import dev.elrol.osmc.OSMC;
import dev.elrol.osmc.data.PlayerSkillData;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Leaderboard {

    private static final Map<Identifier, TreeSet<Entry>> CACHE = new HashMap<>();

    public static void populate() {
        CACHE.clear();
        Map<UUID, PlayerSkillData> playerDataMap = PlayerDataRegistry.get();

        SkillRegistry.getAll().forEach((id, skill) -> {
            TreeSet<Entry> skillSet = new TreeSet<>();
            playerDataMap.forEach((uuid, data) -> {
                long exp = data.getSkillXp(id);
                if(exp > 0) skillSet.add(new Entry(uuid, exp));
            });

            while(skillSet.size() > OSMC.CONFIG.getLeaderboardCount()) skillSet.pollLast();
            CACHE.put(id, skillSet);
        });
    }

    public static List<Entry> get(Identifier id) {
        TreeSet<Entry> set = CACHE.get(id);
        return set == null ? new ArrayList<>() : new ArrayList<>(set);
    }

    public static void updateEntry(Identifier id, UUID uuid, long exp) { updateEntry(id, new Entry(uuid, exp)); }

    public static void updateEntry(Identifier id, Entry entry) {
        if(entry.exp() <= 0) return;

        TreeSet<Entry> entries = CACHE.computeIfAbsent(id, k -> new TreeSet<>());
        int limit = OSMC.CONFIG.getLeaderboardCount();

        entries.removeIf(e -> e.uuid().equals(entry.uuid()));

        if(entries.size() >= limit && !entries.isEmpty() && entry.exp() <= entries.last().exp()) return;

        entries.add(entry);
        if(entries.size() > limit) entries.pollLast();
    }

    public record Entry(UUID uuid, long exp) implements Comparable<Entry> {
        @Override
        public int compareTo(@NotNull Leaderboard.Entry entry) {
            int c = Long.compare(entry.exp, exp);
            return (c == 0) ? uuid.compareTo(entry.uuid) : c;
        }
    }

}
