package com.petrolpark.destroy.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockState;


@Mixin(IForgeBlockState.class)
public interface DestroyFrictionModifier extends IForgeBlockState {

    public default BlockState self() { //Method is identical to that in IForgeBlockState cause I got frustrated trying to make @Invoke work so just reimplemented it
        return (BlockState)this;
    }

    default float getFriction(LevelReader level, BlockPos pos, @Nullable Entity entity) {

        //Sponge doesn't allow injecting into Interface methods, so I have to overwrite the method. This section should be identical to the getFriction() method in IForgeBlockState
        float originalFriction = self().getBlock().getFriction(self(), level, pos, entity);
        //
        if (entity != null && entity instanceof LivingEntity) {
            MobEffectInstance alcoholEffect = ((LivingEntity)entity).getEffect(DestroyMobEffects.INEBRIATION.get());
            if (alcoholEffect != null) {
                return (float)(originalFriction + ((1 - originalFriction)
                    * 0.2 * Math.min(5, alcoholEffect.getAmplifier() + 1) //scale the extent of slipping with the amplifier of Inebriation (effects stop compounding after 4)
                    * (DestroyAllConfigs.SERVER.substances.drunkenSlipping.get() - 0.001f)) //maximum level of slipping
                );
            };
        };
        
        return originalFriction;
    }
    
}
