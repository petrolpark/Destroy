package com.petrolpark.destroy.core.block.entity;

import java.util.ArrayList;
import java.util.List;

import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.core.bettervaluesettings.SidedScrollValueBehaviour;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox.TextValueBox;
import net.createmod.catnip.lang.Lang;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.petrolpark.compat.create.CreateClient.OUTLINER;

/**
 * Mostly a copy of the non-side-specific {@link com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueRenderer Scroll-value renderer}.
 * This also does some other behaviour-specific rendering.
 */
public class BlockEntityBehaviourRenderer {
    
    public static void tick() {
		Minecraft mc = Minecraft.getInstance();
		LocalPlayer player = mc.player;
        if (player == null) return;
		HitResult target = mc.hitResult;
		if (target == null || !(target instanceof BlockHitResult)) return;

		BlockHitResult result = (BlockHitResult) target;
		ClientLevel world = mc.level;
		BlockPos pos = result.getBlockPos();
		Direction face = result.getDirection();

		// Targeted Blocks
		{
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof ISpecialWhenHoveredBlockEntity hover) hover.whenLookedAt(player, result); 
		}

		// Scroll Boxes on the sides of Blocks
		{
			SidedScrollValueBehaviour behaviour = BlockEntityBehaviour.get(world, pos, SidedScrollValueBehaviour.TYPE);
			if (behaviour == null) return;
			if (!behaviour.isActive()) {
				OUTLINER.remove(pos);
				return;
			};
			ItemStack mainhandItem = player.getItemInHand(InteractionHand.MAIN_HAND); // It thinks player might be null (it's not)
			boolean clipboard = AllBlocks.CLIPBOARD.isIn(mainhandItem);
			if (behaviour.onlyVisibleWithWrench() && !AllItems.WRENCH.isIn(mainhandItem) && !clipboard) return;
			boolean highlight = behaviour.testHit(target.getLocation()) && !clipboard;

			// Add the scroll box
			addScrollBox(world, pos, face, behaviour, highlight);

			if (!highlight)
				return;

			List<MutableComponent> tip = new ArrayList<>();
			tip.add(behaviour.label.copy());
			tip.add(DestroyLang.translateDirect("gui.value_settings.hold_to_edit"));
			CreateClient.VALUE_SETTINGS_HANDLER.showHoverTip(tip);
		};
	}

	protected static void addScrollBox(ClientLevel world, BlockPos pos, Direction face, SidedScrollValueBehaviour behaviour,
		boolean highlight) {
		AABB bb = new AABB(Vec3.ZERO, Vec3.ZERO).inflate(.5f)
			.contract(0, 0, -.5f)
			.move(0, 0, -.125f);
		Component label = behaviour.label;
		TextValueBox box = new TextValueBox(label, bb, pos, Component.literal(behaviour.formatValue(face)));

		box.passive(!highlight).wideOutline();

		OUTLINER.showOutline(pos, box.transform(behaviour.getSlotPositioning())).highlightFace(face);
	}
};
