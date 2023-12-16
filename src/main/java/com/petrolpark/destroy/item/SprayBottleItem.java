package com.petrolpark.destroy.item;

import javax.annotation.Nullable;

import com.petrolpark.destroy.sound.DestroySoundEvents;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class SprayBottleItem extends Item {

    public final MobEffectInstance[] effects;

    public SprayBottleItem(Properties properties, MobEffectInstance ...effects) {
        super(properties);
        this.effects = effects;
        DispenserBlock.registerBehavior(this, new SprayBottleDispenserBehaviour());
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();

        spawnEffectCloud(context.getLevel(), context.getClickLocation(), player);
        DestroySoundEvents.SPRAY_BOTTLE_SPRAYS.play(context.getLevel(), player, context.getClickedPos());

        if (player != null && !player.isCreative()) {
            context.getItemInHand().shrink(1);
            player.getInventory().placeItemBackInInventory(DestroyItems.SPRAY_BOTTLE.asStack());
        };

        return InteractionResult.SUCCESS;
    };

    public void spawnEffectCloud(Level level, Vec3 position, @Nullable Player player) {
        AreaEffectCloud effectCloud = new AreaEffectCloud(level, position.x, position.y, position.z);
        if (player != null) effectCloud.setOwner(player);
        effectCloud.setPotion(new Potion(effects));
        for (MobEffectInstance instance : effects) effectCloud.addEffect(instance);

        effectCloud.setRadius(3f);
        effectCloud.setRadiusOnUse(-0.5f);
        effectCloud.setWaitTime(10);
        effectCloud.setRadiusPerTick(-effectCloud.getRadius() / (float)effectCloud.getDuration());

        level.addFreshEntity(effectCloud);
    };

    public class SprayBottleDispenserBehaviour extends OptionalDispenseItemBehavior {

        @Override
        protected ItemStack execute(BlockSource blockSource, ItemStack stack) {
            Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
            Vec3 effectCloudPosition = Vec3.atBottomCenterOf(blockSource.getPos().relative(direction));

            spawnEffectCloud(blockSource.getLevel(), effectCloudPosition, null);
            DestroySoundEvents.SPRAY_BOTTLE_SPRAYS.play(blockSource.getLevel(), null, blockSource.getPos());

            LazyOptional<IItemHandler> invCap = blockSource.getEntity().getCapability(ForgeCapabilities.ITEM_HANDLER);
            boolean couldInsertEmptyBottle = invCap.map(inv -> ItemHandlerHelper.insertItem(inv, DestroyItems.SPRAY_BOTTLE.asStack(), false).isEmpty()).orElse(false);
            if (!couldInsertEmptyBottle) spawnItem(blockSource.getLevel(), DestroyItems.SPRAY_BOTTLE.asStack(), 6, direction, DispenserBlock.getDispensePosition(blockSource));

            setSuccess(true);
            stack.shrink(1);
            return stack;
        };
    };
};
