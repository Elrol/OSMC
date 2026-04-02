package dev.elrol.osmc.data.ability_effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.elrol.osmc.data.AbilityEffect;
import dev.elrol.osmc.data.AbilityEffectType;
import dev.elrol.osmc.registries.OSMCAbilityEffectTypeRegistry;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Random;

public class ParticleAbilityEffect extends AbilityEffect {

    public static final Random RANDOM = new Random();

    public static final MapCodec<ParticleAbilityEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            getCommonCodec(instance).and(ParticleSettings.CODEC.fieldOf("settings").forGetter(ParticleAbilityEffect::settings)
    ).apply(instance, ParticleAbilityEffect::new));

    private final ParticleSettings settings;

    public ParticleAbilityEffect(Identifier abilityEffectID, int reqLevel, boolean togglable, Text displayName, Text description, ParticleSettings settings) {
        super(abilityEffectID, reqLevel, togglable, displayName, description);
        this.settings = settings;
    }

    public ParticleSettings settings() { return settings; }

    @Override
    public AbilityEffectType<?> getType() {
        return OSMCAbilityEffectTypeRegistry.PARTICLE_ABILITY_EFFECT;
    }

    public static class ParticleSettings {
        public static final Codec<ParticleSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.fieldOf("width").forGetter(ParticleSettings::getWidth),
                Codec.FLOAT.fieldOf("height").forGetter(ParticleSettings::getHeight),
                Codec.INT.fieldOf("perSecond").forGetter(ParticleSettings::getPerSecond),
                Codec.STRING.fieldOf("hexColor").forGetter(ParticleSettings::getHexColor),
                Identifier.CODEC.fieldOf("particleType").forGetter(ParticleSettings::getParticleType)
        ).apply(instance, ParticleSettings::new));

        private final float width;
        private final float height;
        private final int perSecond;
        private final String hexColor;
        private final Identifier particleType;

        public ParticleSettings(float width, float height, int perSecond, String hexColor, Identifier particleType) {
            this.width = width;
            this.height = height;
            this.perSecond = perSecond;
            this.hexColor = hexColor;
            this.particleType = particleType;
        }

        public float getWidth() { return width; }
        public float getHeight() { return height; }
        public int getPerSecond() { return perSecond; }
        public String getHexColor() { return hexColor; }
        public Identifier getParticleType() { return particleType; }
        public ParticleType<?> getParticle() { return Registries.PARTICLE_TYPE.get(particleType); }

        public void spawn(ServerPlayerEntity player) {

            ServerWorld world = player.getServerWorld();
            if(world == null) return;

            ParticleType<?> type = getParticle();
            ParticleEffect effect;

            int hex = Integer.parseInt(getHexColor().replaceAll("#", ""), 16);
            float r = ((hex >> 16) & 0xFF) / 255.0F;
            float g = ((hex >> 8) & 0xFF) / 255.0F;
            float b = (hex & 0xFF) / 255.0F;

            if(type == ParticleTypes.DUST) {
                effect = new DustParticleEffect(new Vector3f(r, g, b), 1.0f);
            } else {
                effect = (ParticleEffect) type;
            }

            float floatCount = (float) perSecond / 20.0f;
            int count = (int) floatCount;
            if(RANDOM.nextFloat() < (floatCount - count)) count++;

            float halfWidth = width / 2.0f;
            float halfHeight = height / 2.0f;

            if(type != null) {
                for(int i = 0; i < count; i++) {
                    Vec3d pos = player.getPos().add(
                            RANDOM.nextFloat(-halfWidth, halfWidth),
                            RANDOM.nextFloat(-halfHeight, halfHeight),
                            RANDOM.nextFloat(-halfWidth, halfWidth));
                    world.spawnParticles(
                            effect,
                            pos.getX(),
                            pos.getY(),
                            pos.getZ(),
                            1,0,0, 0, 0.0
                    );
                }
            }
        }
    }
}
