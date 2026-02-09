package dev.elrol.osmc.libs;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import dev.elrol.osmc.data.BoundEffect;
import dev.elrol.osmc.data.SkillEffect;
import dev.elrol.osmc.data.effects.BlockDropMultiplierSkillEffect;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class MathUtils {

    private static final Random random = new Random();
    private static final Map<String, Expression> EXPRESSION_CACHE = new HashMap<>();
    private static final Map<Identifier, Map<Integer, Double>> EXP_TABLE_CACHE = new ConcurrentHashMap<>();

    private static void clearCache() {
        EXPRESSION_CACHE.clear();
        EXP_TABLE_CACHE.clear();
    }

    public static void load() {
        clearCache();
    }

    // Example equation: "floor(level + 300 * 2^(level/7)) / 4"

    public static double calculate(String formula, Map<String, Double> variables) {
        Expression expression = EXPRESSION_CACHE.computeIfAbsent(formula, f -> new ExpressionBuilder(f)
                .variables(variables.keySet()).build());
        expression.setVariables(variables);
        return expression.evaluate();
    }

    public static double getTotalXPForLevel(Identifier id, String formula, int level) {
        Map<Integer, Double> table = EXP_TABLE_CACHE.computeIfAbsent(id, i -> new HashMap<>());

        if(table.containsKey(level)) return table.get(level);

        double total = 0;
        Map<String, Double> vars = new HashMap<>();
        for (int i = 1; i < level; i++) {
            if(table.containsKey(i + 1)) {
                total = table.get(i + 1);
            } else {
                vars.put("level", (double) i);
                total += calculate(formula, vars);
                table.put(i + 1, total);
            }
        }
        return total;
    }

    public static double calculate(String formula, Map<String, Double> variables, Pokemon pokemon) {
        IVs ivs = pokemon.getIvs();
        EVs evs = pokemon.getEvs();

        Map<String, Double> vars = new HashMap<>(variables);

        vars.put("p_level",      (double) pokemon.getLevel());

        vars.put("p_iv_hp",      (double) ivs.getOrDefault(Stats.HP));
        vars.put("p_iv_atk",     (double) ivs.getOrDefault(Stats.ATTACK));
        vars.put("p_iv_def",     (double) ivs.getOrDefault(Stats.DEFENCE));
        vars.put("p_iv_spatk",   (double) ivs.getOrDefault(Stats.SPECIAL_ATTACK));
        vars.put("p_iv_spdef",   (double) ivs.getOrDefault(Stats.SPECIAL_DEFENCE));
        vars.put("p_iv_spd",     (double) ivs.getOrDefault(Stats.SPEED));
        vars.put("p_ivs",        (double) ivs.total());

        vars.put("p_ev_hp",      (double) evs.getOrDefault(Stats.HP));
        vars.put("p_ev_atk",     (double) evs.getOrDefault(Stats.ATTACK));
        vars.put("p_ev_def",     (double) evs.getOrDefault(Stats.DEFENCE));
        vars.put("p_ev_spatk",   (double) evs.getOrDefault(Stats.SPECIAL_ATTACK));
        vars.put("p_ev_spdef",   (double) evs.getOrDefault(Stats.SPECIAL_DEFENCE));
        vars.put("p_evs_pd",     (double) evs.getOrDefault(Stats.SPEED));
        vars.put("p_evs",        (double) evs.total());

        vars.put("p_shiny",      pokemon.getShiny() ? 1.0 : 0.0);
        vars.put("p_legend",     pokemon.isLegendary() ? 1.0 : 0.0);

        vars.put("p_stage",      (double) CobblemonUtils.getPokemonStage(pokemon));

        return calculate(formula, vars);
    }

    /**
     * @param chance Percent chance of success
     * @return If the chance/100 was successful
     */
    public static boolean percentChance(float chance) {
        return random.nextFloat(1.0f) <= (chance/100.0f);
    }

    public static int handleExtraDrops(ServerWorld world, Vec3d origin, ItemStack stack, float chance) {
        int count = (int) chance;
        float remainder = chance - count;

        if(percentChance(remainder)) count++;

        int finalCount = count;

        int max = stack.getItem().getMaxCount();

        ItemStack newStack = stack.copy();

        while(count > max) {
            world.spawnEntity(new ItemEntity(world, origin.getX(), origin.getY(), origin.getZ(), newStack.copyWithCount(max)));
            count -= max;
        }

        if(count > 0)
            world.spawnEntity(new ItemEntity(world, origin.getX(), origin.getY(), origin.getZ(), newStack.copyWithCount(count)));
        return finalCount;
    }
}
