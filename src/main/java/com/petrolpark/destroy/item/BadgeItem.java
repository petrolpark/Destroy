package com.petrolpark.destroy.item;

import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.badge.Badge;
import com.petrolpark.destroy.util.DestroyLang;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

@MoveToPetrolparkLibrary
public class BadgeItem extends Item {

    protected static final DateFormat df = new SimpleDateFormat("YYYY-MM-DD");

    public BadgeItem(Properties properties) {
        super(properties);
    };

    public static ItemStack of(Player player, Badge badge, Date date) {
        ItemStack stack = DestroyItems.BADGE.asStack();
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("Player", player.getScoreboardName());
        tag.putString("Badge", Badge.getId(badge).toString());
        tag.putLong("Date", date.getTime());
        return stack;
    };

    @Override
    public Component getName(ItemStack stack) {
        Badge badge = Badge.getBadge(ResourceLocation.of(stack.getOrCreateTag().getString("Badge"), ':'));
        if (badge != null) return badge.getName();
        return super.getName(stack);
    };

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getOrCreateTag();
        Badge badge = Badge.getBadge(ResourceLocation.of(stack.getOrCreateTag().getString("Badge"), ':'));
        if (badge == null) {
            tooltipComponents.add(DestroyLang.translate("tooltip.badge.unknown").component());
            return;
        };
        tooltipComponents.addAll(TooltipHelper.cutTextComponent(badge.getDescription(), Palette.STANDARD_CREATE));
        tooltipComponents.addAll(TooltipHelper.cutTextComponent(DestroyLang.translate("tooltip.badge", tag.getString("Player"), df.format(new Date(tag.getLong("Date")))).component(), Palette.STANDARD_CREATE));
    };
    
};
