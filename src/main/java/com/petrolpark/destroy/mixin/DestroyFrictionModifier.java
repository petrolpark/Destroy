package com.petrolpark.destroy.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.config.DestroySubstancesConfigs;
import com.petrolpark.destroy.effect.DestroyMobEffects;

import net.minecraft.core.BlockPos;
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

        if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(DestroyMobEffects.INEBRIATION.get())) {
            return (float)(originalFriction + ((1 - originalFriction) * (DestroyAllConfigs.SERVER.substances.drunkenSlipping.get() - 0.001f))); //whatever the friction of the Block is, increase it halfway more to one
        };
        
        return originalFriction;
    }
    
}
