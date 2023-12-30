package com.petrolpark.destroy.item;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.badge.Badge;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

@MoveToPetrolparkLibrary
public class BadgeItem extends Item {

    protected static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public final Supplier<Badge> badge;

    public BadgeItem(Properties properties, Supplier<Badge> badge) {
        super(properties);
        this.badge = badge;
    };

    public static ItemStack of(Player player, Badge badge, Date date) {
        ItemStack stack = new ItemStack(badge.getItem());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("Player", player == null ? "unknown" : player.getScoreboardName());
        tag.putLong("Date", date.getTime());
        tag.getCompound("display").putString("Name", stack.getDisplayName().toString()); // Always display the name in an item frame
        return stack;
    };

    @Override
    public Component getName(ItemStack stack) {
        return badge.get().getName();
    };

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        Badge badge = this.badge.get();
        if (!tag.contains("Date", Tag.TAG_LONG) || !tag.contains("Player", Tag.TAG_STRING)) {
            tooltipComponents.add(DestroyLang.translate("tooltip.badge.unknown").style(ChatFormatting.GRAY).component());
            return;
        };
        tooltipComponents.addAll(TooltipHelper.cutTextComponent(badge.getDescription(), Palette.STANDARD_CREATE));
        tooltipComponents.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.badge", tag.getString("Player"), df.format(new Date(tag.getLong("Date")))).component(), Palette.STANDARD_CREATE));
    };

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return stack;
    };

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    };
    
};
