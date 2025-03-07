package com.petrolpark.destroy.content.tool.swissarmyknife;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.petrolpark.destroy.DestroyMessages;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;

public class SwissArmyKnifeItem extends DiggerItem {

    private static int timeUntilToolPutAway = 20;

    @Nullable
    public static Tool selectedTool = null;

    public SwissArmyKnifeItem(float attackDamageModifier, float attackSpeedModifier, Tier tier, Properties properties) {
        super(attackDamageModifier, attackSpeedModifier, tier, BlockTags.MINEABLE_WITH_PICKAXE, properties); // The tag supplied here is ignored
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        Map<Tool, LerpedFloat> chasers = getChasers(stack);
        chasers.values().forEach(LerpedFloat::tickChaser);
        putChasers(stack, chasers);
        if (level.isClientSide()) {
            if (getTool(stack) != selectedTool) DestroyMessages.sendToServer(new SwissArmyKnifeToolC2SPacket(selectedTool));
        };
    };

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    };

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Tool tool = getTool(context.getItemInHand());
        if (tool != null) return tool.exampleTool.get().getItem().useOn(context);
        return super.useOn(context);
    };

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity entity, InteractionHand hand) {
        if (getTool(stack) == Tool.SHEARS) return Items.SHEARS.interactLivingEntity(stack, playerIn, entity, hand);
        return super.interactLivingEntity(stack, playerIn, entity, hand);
    };

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
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
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        Tool tool = getTool(stack);
        return (tool != null && tool.actions.contains(toolAction));
    };

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, boolean slotChanged) {
        return !(from.getItem() instanceof SwissArmyKnifeItem) || !(to.getItem() instanceof SwissArmyKnifeItem);
    };

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() instanceof SwissArmyKnifeItem && newStack.getItem() instanceof SwissArmyKnifeItem;
    };

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return !(oldStack.getItem() instanceof SwissArmyKnifeItem) || !(newStack.getItem() instanceof SwissArmyKnifeItem);
    };

    @Override
	@OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new SwissArmyKnifeItemRenderer()));
	};

    @OnlyIn(Dist.CLIENT)
    public static void clientPlayerTick() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) return;
        Tool newTool;
        boolean switchTool = false;
        if (!(player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof SwissArmyKnifeItem) && !(player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof SwissArmyKnifeItem)) {
            newTool = null;
            switchTool = true;
        } else {
            newTool = getTool(minecraft.level, minecraft.hitResult, player.isCrouching());
            if (newTool == null && timeUntilToolPutAway > 0) {
                timeUntilToolPutAway--;
                if (timeUntilToolPutAway == 0) switchTool = true;
            } else {
                timeUntilToolPutAway = 20;
            };
        };
        switchTool |= newTool != null && newTool != selectedTool;
        if (switchTool) {
            DestroyMessages.sendToServer(new SwissArmyKnifeToolC2SPacket(newTool));
            selectedTool = newTool;
        };
    };

    @Nullable
    @SuppressWarnings("deprecation")
    public static Tool getTool(Level level, HitResult ray, boolean shiftDown) {
        Tool tool = null;
        if (ray instanceof BlockHitResult bhr) {
            BlockState state = level.getBlockState(bhr.getBlockPos());

            if (state.getBlock() instanceof IPreferredSwissArmyKnifeToolBlock specialBlock) return specialBlock.getToolForSwissArmyKnife(level, bhr.getBlockPos(), state, false);

            if (state.is(BlockTags.MINEABLE_WITH_AXE)) tool = Tool.AXE;
            if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) tool = Tool.SHOVEL;
            if (state.is(BlockTags.MINEABLE_WITH_HOE)) tool = Tool.HOE;
            if (state.is(BlockTags.MINEABLE_WITH_PICKAXE)) tool = Tool.PICKAXE;
            if (state instanceof IForgeShearable) tool = Tool.SHEARS;
            if (state.is(BlockTags.LEAVES) || state.is(BlockTags.WOOL)) tool = Tool.SHEARS;

            if (shiftDown || tool == null) {
                if (state.getBlock() instanceof PumpkinBlock) tool = Tool.SHEARS;
                if (AxeItem.getAxeStrippingState(state) != null) tool = Tool.AXE;
                if (ShovelItem.FLATTENABLES.containsKey(state.getBlock()) || (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT))) tool = Tool.SHOVEL;
                if (HoeItem.TILLABLES.containsKey(state.getBlock())) tool = Tool.HOE;
            };
        } else if (ray instanceof EntityHitResult ehr) {
            Entity entity = ehr.getEntity();
            if (entity instanceof IPreferredSwissArmyKnifeToolEntity specialEntity) return specialEntity.getToolForSwissArmyKnife(shiftDown);
            if (entity instanceof LivingEntity) tool = Tool.AXE;
            if (entity instanceof IForgeShearable) tool = shiftDown ? Tool.AXE : Tool.SHEARS;
        };
        return tool;
    };

    @Nullable
    public static Tool getTool(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("ActiveTool")) return null;
        return Tool.values()[tag.getInt("ActiveTool")];
    };

    public static void putTool(ItemStack stack, @Nullable Tool tool) {
        stack.removeTagKey("ActiveTool");
        if (tool != null) stack.getOrCreateTag().putInt("ActiveTool", tool.ordinal());
        Map<Tool, LerpedFloat> chasers = getChasers(stack);
        chasers.entrySet().forEach(entry -> entry.getValue().chase(entry.getKey() == tool ? 1d : 0d, 0.4d, LerpedFloat.Chaser.EXP));
        putChasers(stack, chasers);
    };

    public static Map<Tool, LerpedFloat> getChasers(ItemStack stack) {
        EnumMap<Tool, LerpedFloat> map = new EnumMap<>(Tool.class);
        int ordinal = 0;
        if (!stack.getOrCreateTag().contains("ToolAnimations", Tag.TAG_LIST)) return Tool.ALL_RETRACTED;
        for (Tag t : stack.getOrCreateTag().getList("ToolAnimations", Tag.TAG_COMPOUND)) {
            CompoundTag tag = (CompoundTag)t;
            LerpedFloat toolAngle = LerpedFloat.angular().chase(tag.getFloat("Target"), 0.4d, LerpedFloat.Chaser.EXP);
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
            if (toolAngle == null) toolAngle = LerpedFloat.angular().chase(0d, 0.4d, LerpedFloat.Chaser.EXP);
            tag.putFloat("Target", toolAngle.getChaseTarget());
            tag.putFloat("Value", toolAngle.getValue());
            list.add(tag);
        };
        stack.getOrCreateTag().put("ToolAnimations", list);
    };

    public static enum Tool {
        PICKAXE(ToolActions.DEFAULT_PICKAXE_ACTIONS, () -> new ItemStack(Items.IRON_PICKAXE)),
        AXE(ToolActions.DEFAULT_AXE_ACTIONS,() -> new ItemStack(Items.IRON_AXE)),
        SHOVEL(ToolActions.DEFAULT_SHOVEL_ACTIONS, () -> new ItemStack(Items.IRON_SHOVEL)),
        HOE(ToolActions.DEFAULT_HOE_ACTIONS,() -> new ItemStack(Items.IRON_HOE)),
        SHEARS(ToolActions.DEFAULT_SHEARS_ACTIONS, () -> new ItemStack(Items.IRON_PICKAXE));

        public static final Map<Tool, LerpedFloat> ALL_RETRACTED = new EnumMap<>(Tool.class);
        static {
            for (Tool tool : values()) ALL_RETRACTED.put(tool, LerpedFloat.angular().chase(0d, 0.4d, LerpedFloat.Chaser.EXP));
        };

        public final Set<ToolAction> actions;
        public final Supplier<ItemStack> exampleTool;

        Tool(Set<ToolAction> toolActions, Supplier<ItemStack> exampleTool) {
            actions = toolActions;
            this.exampleTool = exampleTool;
        };
    };
    
};
