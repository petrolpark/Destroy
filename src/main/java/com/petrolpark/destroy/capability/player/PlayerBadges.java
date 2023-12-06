package com.petrolpark.destroy.capability.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.petrolpark.destroy.badge.Badge;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerBadges {

    private final Set<Pair<Badge, Date>> badges = new HashSet<>();

    public Collection<Pair<Badge, Date>> getBadges() {
        return Collections.unmodifiableSet(badges);
    };

    public void setBadges(Collection<Pair<Badge, Date>> badges) {
        this.badges.clear();
        this.badges.addAll(badges);
    };

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

        public static Capability<PlayerBadges> PLAYER_BADGES = CapabilityManager.get(new CapabilityToken<PlayerBadges>() {});

        private PlayerBadges playerBadges = null;
        private final LazyOptional<PlayerBadges> optional = LazyOptional.of(this::createPlayerBadges);

        private PlayerBadges createPlayerBadges() {
            if (playerBadges == null) playerBadges = new PlayerBadges();
            return playerBadges;
        };

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            ListTag listTag = new ListTag();
            for (Pair<Badge, Date> pair : createPlayerBadges().badges) {
                CompoundTag badgeTag = new CompoundTag();
                ResourceLocation id = Badge.getId(pair.getFirst());
                if (id == null) continue;
                badgeTag.putString("Id", id.toString());
                badgeTag.putLong("DateAwarded", pair.getSecond().getTime());
                listTag.add(badgeTag);
            };
            tag.put("Badges", listTag);
            return tag;
        };

        @Override
        public void deserializeNBT(CompoundTag tag) {
            List<Pair<Badge, Date>> badges = new ArrayList<>();
            tag.getList("Badges", Tag.TAG_COMPOUND).forEach(t -> {
                CompoundTag badgeTag = (CompoundTag)t;
                Badge badge = Badge.getBadge(ResourceLocation.of(badgeTag.getString("Id"), ':'));
                if (badge == null) return;
                badges.add(Pair.of(badge, new Date(badgeTag.getLong("DateAwarded"))));
            });
            createPlayerBadges().setBadges(badges);
        };

        @Override
        public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == PLAYER_BADGES) return optional.cast();
            return LazyOptional.empty();
        };

    };
};
