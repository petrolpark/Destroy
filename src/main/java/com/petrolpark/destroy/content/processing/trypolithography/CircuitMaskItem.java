package com.petrolpark.destroy.content.processing.trypolithography;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import com.petrolpark.compat.create.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.client.DestroyGuiTextures;
import com.petrolpark.destroy.client.DestroyLang;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class CircuitMaskItem extends CircuitPatternItem {

    public CircuitMaskItem(Properties properties) {
        super(properties);
    };

    public static final ChatFormatting[] directionColors = new ChatFormatting[]{ChatFormatting.WHITE, ChatFormatting.WHITE, ChatFormatting.RED, ChatFormatting.WHITE, ChatFormatting.YELLOW, ChatFormatting.BLUE};

    public static List<UUID> getContaminants(ItemStack stack) {
        List<UUID> previousPunches = new ArrayList<>(3);
        stack.getOrCreateTag().getList("PunchedBy", Tag.TAG_INT_ARRAY).forEach(uuidTag -> {
            previousPunches.add(NbtUtils.loadUUID(uuidTag));
        });
        return previousPunches;
    };  
    
    public static ItemStack contaminate(ItemStack stack, UUID uuid) {
        stack = stack.copy();
        CompoundTag tag = stack.getOrCreateTag();
        List<UUID> previousPunches = getContaminants(stack);
        if (previousPunches.contains(uuid)) return stack;
        if (previousPunches.size() >= 3) return DestroyItems.RUINED_CIRCUIT_MASK.asStack();
        previousPunches.add(uuid);
        ListTag newPunches = new ListTag();
        previousPunches.forEach(id -> newPunches.add(NbtUtils.createUUID(id)));
        tag.put("PunchedBy", newPunches);
        stack.setTag(tag);
        return stack;
    };
    
    @Override
    public void launch(DirectionalTransportedItemStack stack, Direction launchDirection) {
        // If it is 'flipped', the Item has been rotated around 180 the north-south axis before being rotated around the up-down axis
        CompoundTag tag = stack.stack.getOrCreateTag();
        boolean alreadyFlipped = tag.contains("Flipped");
        tag.remove("Flipped"); 

        Rotation rotation = stack.getRotation();
        if (
            launchDirection.getAxis() == Axis.Z && (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_90)
            || launchDirection.getAxis() == Axis.X && (rotation == Rotation.COUNTERCLOCKWISE_90 || rotation == Rotation.CLOCKWISE_180)
        ) {
            stack.rotate(Rotation.CLOCKWISE_180); // Fix the Rotation if certain orientations are flipped over certain axes
        };

        if (!alreadyFlipped) tag.putBoolean("Flipped", true);
        super.launch(stack, launchDirection);
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        stack.getOrCreateTag().remove("Flipped");
        stack.getOrCreateTag().remove("RotationWhileFlying");

        if (level.isClientSide() && isSelected && entity instanceof Player player && player.isCrouching()) {
            Direction direction = player.getDirection();
            player.displayClientMessage(DestroyLang.translate("tooltip.circuit_mask.facing_direction", DestroyLang.direction(direction).style(directionColors[direction.ordinal()])).component(), true);
        };
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        if (stack.getOrCreateTag().contains("HideContaminants")) return;

        if(level == null) // Fixes a crash caused by Jade
            level = Minecraft.getInstance().level;

        List<UUID> previousPunchers = getContaminants(stack);
        tooltipComponents.add(Component.literal(" "));
        tooltipComponents.add(DestroyLang.translate("tooltip.circuit_mask.punched_by", previousPunchers.size()).style(ChatFormatting.GRAY).component());
        for (UUID uuid : previousPunchers) {
            tooltipComponents.add(DestroyLang.translate("tooltip.circuit_mask.puncher", Destroy.CIRCUIT_PUNCHER_HANDLER.getPuncher(level, uuid).getName()).style(ChatFormatting.GRAY).component());
        };
        for (int i = 0; i < 3 - previousPunchers.size(); i++) {
            tooltipComponents.add(DestroyLang.translate("tooltip.circuit_mask.puncher_free").style(ChatFormatting.GRAY).component());
        };
    };

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.of(new CircuitPatternTooltipComponent(getPattern(stack), false, DestroyGuiTextures.CIRCUIT_MASK_BORDER, DestroyGuiTextures.CIRCUIT_MASK_CELL, DestroyGuiTextures.CIRCUIT_MASK_CELL_SHADING));
    };

    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new CircuitPatternItemRenderer(Destroy.asResource("item/circuit_pattern/circuit_mask"))));
    };
    
};
