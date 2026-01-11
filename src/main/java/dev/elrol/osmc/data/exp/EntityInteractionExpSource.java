package dev.elrol.osmc.data.exp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.exp.abstractexps.ExpSource;
import dev.elrol.osmc.registries.ExpSourceTypeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;

public class EntityInteractionExpSource extends ExpSource {

    public static final MapCodec<EntityInteractionExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(Registries.ENTITY_TYPE.getCodec().listOf().fieldOf("entities").forGetter(EntityInteractionExpSource::getEntities)
    ).apply(instance, (expGain, entities) -> {
        EntityInteractionExpSource data = new EntityInteractionExpSource(expGain);
        data.entities.addAll(entities);
        return data;
    }));

    List<EntityType<?>> entities = new ArrayList<>();

    protected EntityInteractionExpSource(int expGain) {
        super(expGain);
    }

    public List<EntityType<?>> getEntities() { return entities; }

    @Override
    public ExpSourceType<?> getType() {
        return ExpSourceTypeRegistry.ENTITY_INTERACT_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }
}
