package com.petrolpark.destroy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.destroy.advancement.DestroyAdvancements;
import com.petrolpark.destroy.effect.DestroyMobEffects;
import com.simibubi.create.AllTags;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlock.class)
public class CuttingBoardMixin {
    
    @Inject(
        method = "Lvectorwing/farmersdelight/common/block/CuttingBoardBlock;use(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lvectorwing/farmersdelight/common/block/CuttingBoardBlock;spawnCuttingParticles(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;I)V"
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void inUse(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir, BlockEntity tileEntity, CuttingBoardBlockEntity cuttingBoardEntity, ItemStack heldStack, ItemStack offhandStack, ItemStack boardStack) {
        if (boardStack.is(AllTags.forgeItemTag("crops/onion"))) {
            DestroyAdvancements.CUT_ONIONS.award(level, player);
            level.getEntitiesOfClass(LivingEntity.class, new AABB(pos).inflate(2f)).forEach(entity -> entity.addEffect(new MobEffectInstance(DestroyMobEffects.CRYING.get(), 400, 0, false, false, true)));
        };
    };
};
