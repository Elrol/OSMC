package dev.elrol.osmc.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Grid<T> {

    public static <T> Codec<Grid<T>> makeCodec(Codec<T> codec, Supplier<T> defaultCell) {
        Codec<GridEntry<T>> entryCodec = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("x").forGetter(GridEntry::x),
                Codec.INT.fieldOf("y").forGetter(GridEntry::y),
                codec.fieldOf("value").forGetter(GridEntry::value)
        ).apply(instance, GridEntry::new));

        return entryCodec.listOf().xmap(
                entries -> {
                    Grid<T> grid = new Grid<>(defaultCell);
                    for (GridEntry<T> entry : entries) {
                        grid.set(entry.x, entry.y, entry.value);
                    }
                    return grid;
                },
                grid -> {
                    List<GridEntry<T>> entries = new ArrayList<>();
                    grid.grid.forEach((key, value) -> entries.add(new GridEntry<>(key, value)));
                    return entries;
                }
        );
    }

    private final Map<Long, T> grid = new HashMap<>();
    private final Supplier<T> defaultCell;

    public Grid(Supplier<T> defaultCell) {
        this.defaultCell = defaultCell;
    }

    public static long getPackedKey(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }

    public void set(long key, T value) {
        grid.put(key, value);
    }

    public void set(int x, int y, T value) {
        set(getPackedKey(x, y), value);
    }

    public T get(int x, int y) {
        return grid.getOrDefault(getPackedKey(x, y), defaultCell.get());
    }

    public Map<Long, T> get() { return grid; }

    public void remove(long key) { grid.remove(key); }

    public void remove(int x, int y) { remove(getPackedKey(x, y)); }

    public static record GridEntry<T>(int x, int y, T value) {

        public GridEntry(long coords, T value) {
            this((int) (coords >> 32), (int)(coords & 0xffffffffL), value);
        }

    }

    public int count() { return grid.size(); }
    public int count(T value) { return (int) grid.values().stream().filter(state -> state == value).count(); }

}
