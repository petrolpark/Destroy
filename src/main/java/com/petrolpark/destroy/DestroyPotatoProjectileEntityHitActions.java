package com.petrolpark.destroy;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;

import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class DestroyPotatoProjectileEntityHitActions {

    static {
        register("explode_entity", ExplodeEntity.CODEC);
    }

    public static void init() {
    }

    private static void register(String name, Codec<? extends PotatoProjectileEntityHitAction> codec) {
        Registry.register(CreateBuiltInRegistries.POTATO_PROJECTILE_ENTITY_HIT_ACTION, Destroy.asResource(name), codec);
    }

    private record ExplodeEntity(float radius, boolean causesFire, Level.ExplosionInteraction mode) implements PotatoProjectileEntityHitAction {

        public static final Codec<ExplodeEntity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("radius").forGetter(ExplodeEntity::radius),
                Codec.BOOL.fieldOf("causesFire").forGetter(ExplodeEntity::causesFire),
                Codec.of(Codec.STRING.comap(Enum::name), Codec.STRING.map((value) -> Enum.valueOf(Level.ExplosionInteraction.class, value)))
                                .fieldOf("mode").forGetter(ExplodeEntity::mode)).apply(instance, ExplodeEntity::new));
        @Override
        public boolean execute(ItemStack projectile, EntityHitResult ray, Type type) {
            Entity entity = ray.getEntity();
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), radius, causesFire, mode);
            return false;
        }

        @Override
        public Codec<? extends PotatoProjectileEntityHitAction> codec() {
            return CODEC;
        }
    };
}
