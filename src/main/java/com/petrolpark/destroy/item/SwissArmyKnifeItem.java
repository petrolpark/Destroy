package com.petrolpark.destroy.item;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.item.renderer.SwissArmyKnifeRenderer;
import com.petrolpark.destroy.item.renderer.SwissArmyKnifeRenderer.RenderedTool;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.foundation.utility.animation.LerpedFloat.Chaser;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.TierSortingRegistry;

public class SwissArmyKnifeItem extends DiggerItem {

    public SwissArmyKnifeItem(float attackDamageModifier, float attackSpeedModifier, Tier tier, Properties properties) {
        super(attackDamageModifier, attackSpeedModifier, tier, BlockTags.MINEABLE_WITH_PICKAXE, properties); // The tag supplied here is ignored
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        Map<Tool, LerpedFloat> chasers = getChasers(stack);
        chasers.values().forEach(LerpedFloat::tickChaser);
        putChasers(stack, chasers);      
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Tool tool = Tool.values()[context.getPlayer().getRandom().nextInt(Tool.values().length)];
        Map<Tool, LerpedFloat> chasers = getChasers(context.getItemInHand());
        chasers.get(tool).chase(1d, 0.4d, Chaser.EXP);
        putChasers(context.getItemInHand(), chasers);
        Destroy.LOGGER.info("he he ");
        return InteractionResult.SUCCESS;
    };

    @Override
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return speed;
    };

    @Override
    public boolean isCorrectToolForDrops(BlockState state) {
        return TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
    };

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return TierSortingRegistry.isCorrectTierForDrops(getTier(), state);
    };

    @Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new SwissArmyKnifeRenderer()));
	};

    public static Map<Tool, LerpedFloat> getChasers(ItemStack stack) {
        EnumMap<Tool, LerpedFloat> map = new EnumMap<>(Tool.class);
        int ordinal = 0;
        if (!stack.getOrCreateTag().contains("ToolAnimations", Tag.TAG_LIST)) return Tool.ALL_RETRACTED;
        for (Tag t : stack.getOrCreateTag().getList("ToolAnimations", Tag.TAG_COMPOUND)) {
            CompoundTag tag = (CompoundTag)t;
            LerpedFloat toolAngle = LerpedFloat.angular().chase(tag.getFloat("Target"), 0.4d, Chaser.EXP);
            toolAngle.setValue(tag.getFloat("Value"));
            map.put(Tool.values()[ordinal], toolAngle);
            ordinal++;
        };
        return map;
    };

    public static void putChasers(ItemStack stack, Map<Tool, LerpedFloat> chasers) {
        ListTag list = new ListTag();
        for (Tool tool : Tool.values()) {
            CompoundTag tag = new CompoundTag();
            LerpedFloat toolAngle = chasers.get(tool);
            if (toolAngle == null) toolAngle = LerpedFloat.angular().chase(0d, 0.4d, Chaser.EXP);
            tag.putFloat("Target", toolAngle.getChaseTarget());
            tag.putFloat("Value", toolAngle.getValue());
            list.add(tag);
        };
        stack.getOrCreateTag().put("ToolAnimations", list);
    };

    public static enum Tool {
        PICKAXE(RenderedTool.PICKAXE),
        AXE(RenderedTool.AXE),
        SHOVEL(RenderedTool.SHOVEL),
        HOE(RenderedTool.HOE),
        SHEARS(RenderedTool.LOWER_SHEARS, RenderedTool.UPPER_SHEARS);

        public static Map<Tool, LerpedFloat> ALL_RETRACTED = new EnumMap<>(Tool.class);
        static {
            for (Tool tool : values()) ALL_RETRACTED.put(tool, LerpedFloat.angular().chase(0d, 0.4d, Chaser.EXP));
        };

        public final RenderedTool[] renderedTools;

        Tool(RenderedTool ...renderedTools) {
            this.renderedTools = renderedTools;
        };
    };
    
};
