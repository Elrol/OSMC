package dev.elrol.osmc.libs;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.Species;

public class CobblemonUtils {

    public static int getPokemonStage(Pokemon pokemon) {
        int stage = 0;
        Species species = pokemon.getSpecies();

        while(species.getPreEvolution() != null) {
            stage++;
            species = species.getPreEvolution().getSpecies();
        }
        return stage;
    }

}
