package dev.elrol.osmc.data.exp;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.ExpSourceType;
import dev.elrol.osmc.data.ExpSource;
import dev.elrol.osmc.data.SkillTrigger;
import dev.elrol.osmc.libs.OSMCConstants;
import dev.elrol.osmc.registries.OSMCExpSourceTypeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;

import java.util.ArrayList;
import java.util.List;

public class EntityKillExpSource extends ExpSource {

    public static final MapCodec<EntityKillExpSource> CODEC = RecordCodecBuilder.mapCodec(instance -> ExpSource.getCommonCodec(instance)
            .and(OSMCConstants.TARGET_ENTITY_TYPE_CODEC.listOf().fieldOf("entities").forGetter(EntityKillExpSource::getEntities)
    ).apply(instance, (expGain, entities) -> {
        EntityKillExpSource data = new EntityKillExpSource(expGain);
        data.entities.addAll(entities);
        return data;
    }));

    List<Either<RegistryKey<EntityType<?>>, TagKey<EntityType<?>>>> entities = new ArrayList<>();

    public EntityKillExpSource(int expGain) {
        super(expGain);
    }

    public void addEntity(EntityType<?> entity) {
        Registries.ENTITY_TYPE.getKey(entity).ifPresent(key -> entities.add(Either.left(key)));
    }

    public List<Either<RegistryKey<EntityType<?>>, TagKey<EntityType<?>>>> getEntities() { return entities; }

    @Override
    public ExpSourceType<?> getType() {
        return OSMCExpSourceTypeRegistry.ENTITY_KILL_EXP_SOURCE;
    }

    @Override
    public MapCodec<? extends ExpSource> getCodec() {
        return CODEC;
    }

    @Override
    public List<SkillTrigger> getTriggers() {
        return List.of(SkillTrigger.ENTITY_KILL);
    }
}
