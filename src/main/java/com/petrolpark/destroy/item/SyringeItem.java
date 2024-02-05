package com.petrolpark.destroy.item;

import java.util.List;
import java.util.function.Consumer;

import com.petrolpark.destroy.item.renderer.SyringeItemRenderer;
import com.petrolpark.destroy.world.damage.DestroyDamageSources;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SyringeItem extends Item implements CustomUseEffectsItem {

    public SyringeItem(Properties properties) {
        super(properties.stacksTo(1));
        DispenserBlock.registerBehavior(this, new SyringeDispenserBehaviour());
    };

    /**
     * Called when a Player injects themselves, or an Entity is injected by another Entity. Should enact the result of the injection.
     * @param itemStack The Item Stack used to inject
     * @param level The Level in which the injection is taking place
     * @param target The Entity being injected
     */
    public void onInject(ItemStack itemStack, Level level, LivingEntity target) {
        return;
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getOrCreateTag().contains("Injecting")) { //continue if Player is already injecting
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.PASS, itemStack);
        };

        if (hand == InteractionHand.OFF_HAND && player.getMainHandItem().isEmpty()) { // ensure Player is using the Syringe with their offhand, and has an empty main hand
            itemStack.getOrCreateTag().putBoolean("Injecting", true);
            player.startUsingItem(hand);

            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
        };

        return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
    };

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && !entity.level().isClientSide()) {
            onInject(stack, entity.level(), livingEntity);
            if (!player.isCreative()) {
                player.getInventory().removeItem(stack);
                player.getInventory().add(new ItemStack(DestroyItems.SYRINGE.get()));
            };
            return true;
        };
        return super.onLeftClickEntity(stack, player, entity);
    };

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        onInject(itemStack, level, entity);
        if (entity instanceof Player player){
            if (player.isCreative()) return itemStack;
        } else {
            return itemStack;
        }
        if (!(entity instanceof Player)) return itemStack;
        itemStack.getOrCreateTag().remove("Injecting");
        return new ItemStack(DestroyItems.SYRINGE.get());
    };

    @Override
    public void releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player)) return;
        itemStack.getOrCreateTag().remove("Injecting");
    };

    @Override
	public Boolean shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
		return true;
	};

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        if (entity.getTicksUsingItem() == 24 && !entity.level().isClientSide()) {
            entity.hurt(DestroyDamageSources.selfNeedle(entity.level()), 1f);
        };
        return true;
    };

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    };

    @Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.NONE;
	};

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SyringeItemRenderer()));
    };

    public class SyringeDispenserBehaviour extends OptionalDispenseItemBehavior {

        @Override
        public ItemStack execute(BlockSource blockSource, ItemStack stack) {
            List<LivingEntity> list = blockSource.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(blockSource.getPos().relative(blockSource.getBlockState().getValue(DispenserBlock.FACING))));
            if (!list.isEmpty()) {
                onInject(stack, blockSource.getLevel(), list.get(0));
                setSuccess(true);
                return DestroyItems.SYRINGE.asStack();
            } else {
                return super.execute(blockSource, stack);
            }
        };
    };
    
};
