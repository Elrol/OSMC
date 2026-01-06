package dev.elrol.osmc.libs;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.HashMap;
import java.util.Map;

public class MathUtils {

    // Example equation: "floor(level + 300 * 2^(level/7)) / 4"

    public static double calculate(String formula, Map<String, Double> variables) {
        Expression expression = new ExpressionBuilder(formula)
                .variables(variables.keySet()).build();
        expression.setVariables(variables);
        return expression.evaluate();
    }

    public static double getTotalXPForLevel(String formula, int level) {
        double total = 0;
        Map<String, Double> vars = new HashMap<>();
        for (int i = 1; i < level; i++) {
            vars.put("level", (double) i);
            total += calculate(formula, vars);
        }
        return total;
    }

}
