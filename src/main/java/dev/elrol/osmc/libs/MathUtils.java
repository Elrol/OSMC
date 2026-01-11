package dev.elrol.osmc.libs;

import net.minecraft.util.Identifier;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MathUtils {

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

}
