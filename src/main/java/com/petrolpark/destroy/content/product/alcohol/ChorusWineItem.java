package com.petrolpark.destroy.content.product.alcohol;

import com.petrolpark.destroy.config.DestroyAllConfigs;
import com.petrolpark.destroy.config.DestroySubstancesConfigs;
import com.petrolpark.destroy.core.item.tooltip.IDynamicItemDescription;
import com.petrolpark.destroy.core.player.PlayerPreviousPositionsCapability;
import com.simibubi.create.foundation.item.ItemDescription;
import net.createmod.catnip.lang.FontHelper.Palette;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChorusWineItem extends AlcoholicDrinkItem implements IDynamicItemDescription {

    public ChorusWineItem(Properties properties, int strength) {
        super(properties, strength);
    };

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        stack = super.finishUsingItem(stack, level, entityLiving);
        if (!level.isClientSide() && entityLiving instanceof Player player) {
            player.getCapability(PlayerPreviousPositionsCapability.CAPABILITY).ifPresent(previousPositions -> {
                if (player.isPassenger()) player.stopRiding();
                BlockPos pos = previousPositions.getOldestPosition();
                if (pos == null) return;
                Vec3 oldLocation = new Vec3(player.getX(), player.getY(), player.getZ());
                Vec3 newLocation = new Vec3(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
                if (player.randomTeleport(newLocation.x, newLocation.y, newLocation.z, true)) {
                    SoundEvent soundevent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                    level.playSound(player, oldLocation.x, oldLocation.y, oldLocation.z, soundevent, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.playSound(soundevent, 1.0F, 1.0F);
                    player.getCooldowns().addCooldown(this, 20);
                };
            });
        };
        return stack;
    }

    @Override
    public String getDescriptionId() {
        return DestroySubstancesConfigs.alcoholEnabled() ? super.getDescriptionId() : "item.destroy.chorus_wine_bottle.pg";
    };

    @Override
    public ItemDescription getItemDescription() {
        return new ItemDescription.Builder(getPalette())
            .addSummary(Component.translatable("item.destroy.chorus_wine_bottle.dynamic_tooltip.summary").getString())
            .addBehaviour(Component.translatable("item.destroy.chorus_wine_bottle.dynamic_tooltip.condition").getString(), Component.translatable("item.destroy.chorus_wine_bottle.dynamic_tooltip.behaviour", DestroyAllConfigs.SERVER.substances.chorusWineTeleportTime.get()).getString())
            .build();
    };

    @Override
    public Palette getPalette() {
        return Palette.STANDARD_CREATE;
    };
    
}
