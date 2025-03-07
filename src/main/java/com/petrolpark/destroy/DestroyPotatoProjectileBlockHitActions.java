package com.petrolpark.destroy;


import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.potatoCannon.PotatoCannonProjectileType;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileBlockHitAction;
import com.simibubi.create.api.equipment.potatoCannon.PotatoProjectileEntityHitAction;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.content.equipment.potatoCannon.AllPotatoProjectileEntityHitActions;
import com.simibubi.create.foundation.utility.GlobalRegistryAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

public class DestroyPotatoProjectileBlockHitActions {

    static {
        register("explode_block", ExplodeBlock.CODEC);
    }

    public static void init() {
    }

    private static void register(String name, Codec<? extends PotatoProjectileBlockHitAction> codec) {
        Registry.register(CreateBuiltInRegistries.POTATO_PROJECTILE_BLOCK_HIT_ACTION, Destroy.asResource(name), codec);
    }

    private record ExplodeBlock(float radius, boolean causesFire, Level.ExplosionInteraction mode) implements PotatoProjectileBlockHitAction {

        public static final Codec<ExplodeBlock> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("radius").forGetter(ExplodeBlock::radius),
                Codec.BOOL.fieldOf("causesFire").forGetter(ExplodeBlock::causesFire),
                Codec.of(Codec.STRING.comap(Enum::name), Codec.STRING.map((value) -> Enum.valueOf(Level.ExplosionInteraction.class, value)))
                        .fieldOf("mode").forGetter(ExplodeBlock::mode)).apply(instance, ExplodeBlock::new));

        @Override
        public boolean execute(LevelAccessor level, ItemStack projectile, BlockHitResult ray) {
            if (level.isClientSide()) return true;
            BlockPos pos = ray.getBlockPos();
            if (level instanceof Level l && !l.isLoaded(pos)) return true;
            ((Level) level).explode(null, pos.getX(), pos.getY(), pos.getZ(), radius, causesFire, mode);
            return false;
        }

        @Override
        public Codec<? extends PotatoProjectileBlockHitAction> codec() {
            return CODEC;
        }
    };

    private record ExplodeEntity(float radius, boolean causesFire, Level.ExplosionInteraction mode) implements PotatoProjectileEntityHitAction {

        public static final Codec<ExplodeEntity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("radius").forGetter(ExplodeEntity::radius),
                Codec.BOOL.fieldOf("causesFire").forGetter(ExplodeEntity::causesFire),
                Codec.of(Codec.BYTE.comap((value) -> (byte) value.ordinal()), Codec.BYTE.map((value) -> Level.ExplosionInteraction.values()[value]))
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
