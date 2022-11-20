package com.petrolpark.destroy.item;

import java.util.List;
import java.util.function.Consumer;

import com.petrolpark.destroy.item.renderer.SyringeItemRenderer;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.world.DestroyDamageSources;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class SyringeItem extends Item implements CustomUseEffectsItem {
    public SyringeItem(Properties properties) {
        super(properties.stacksTo(1));
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
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        onInject(itemStack, level, entity);
        if (!(entity instanceof Player)) return itemStack;
        itemStack.getOrCreateTag().remove("Injecting");
        return new ItemStack(DestroyItems.SYRINGE.get());
    };

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (count == 8 && !player.getLevel().isClientSide()) {
            player.hurt(DestroyDamageSources.SELF_NEEDLE, 1f);
        };
        super.onUsingTick(stack, player, count);
    }

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
        //TODO Sounds and particles here
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
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(DestroyLang.translate("tooltip.syringe").style(ChatFormatting.GRAY).component());
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SyringeItemRenderer()));
    };
    
}
