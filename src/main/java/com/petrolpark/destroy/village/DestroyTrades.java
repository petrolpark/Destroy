package com.petrolpark.destroy.village;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.npc.VillagerTrades;

import com.simibubi.create.AllItems;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.item.DestroyItems;

public class DestroyTrades {

    public static final List<VillagerTrades.ItemListing> INNKEEPER_NOVICE_TRADES = new ArrayList<>();
    public static final List<VillagerTrades.ItemListing> INNKEEPER_APPRENTICE_TRADES = new ArrayList<>();
    public static final List<VillagerTrades.ItemListing> INNKEEPER_JOURNEYMAN_TRADES = new ArrayList<>();
    public static final List<VillagerTrades.ItemListing> INNKEEPER_EXPERT_TRADES = new ArrayList<>();
    public static final List<VillagerTrades.ItemListing> INNKEEPER_MASTER_TRADES = new ArrayList<>();

    static {

        INNKEEPER_NOVICE_TRADES.addAll(List.of(
            (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(DestroyItems.UNDISTILLED_MOONSHINE_BOTTLE.get(), 1),
                16,
                1,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 4),
                ItemStack.EMPTY,
                new ItemStack(DestroyItems.ONCE_DISTILLED_MOONSHINE_BOTTLE.get(), 1),
                16,
                1,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.GLASS_BOTTLE, 1),
                ItemStack.EMPTY,
                PotionUtils.setPotion(new ItemStack(Items.POTION, 1), Potions.WATER),
                24,
                1,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.WHEAT, 20),
                ItemStack.EMPTY,
                new ItemStack(Items.EMERALD, 1),
                16,
                1,
                0.05f
            )
        ));

        INNKEEPER_APPRENTICE_TRADES.addAll(List.of(
            (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.GLASS_BOTTLE, 12),
                ItemStack.EMPTY,
                new ItemStack(Items.EMERALD, 1),
                16,
                3,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 10),
                ItemStack.EMPTY,
                new ItemStack(DestroyItems.YEAST.get(), 1),
                12,
                1,
                0.05f
            )
        ));

        INNKEEPER_JOURNEYMAN_TRADES.addAll(List.of(
            (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 6),
                ItemStack.EMPTY,
                new ItemStack(DestroyItems.TWICE_DISTILLED_MOONSHINE_BOTTLE.get(), 1),
                16,
                1,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 8),
                ItemStack.EMPTY,
                new ItemStack(DestroyItems.THRICE_DISTILLED_MOONSHINE_BOTTLE.get(), 1),
                16,
                1,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 3),
                ItemStack.EMPTY,
                new ItemStack(AllItems.BUILDERS_TEA.get(), 1),
                16,
                5,
                0.05f
            )
        ));

        INNKEEPER_EXPERT_TRADES.addAll(List.of(
            (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.BLUE_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.CYAN_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.GRAY_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.LIME_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.PINK_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.BLACK_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.BROWN_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.GREEN_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.WHITE_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.ORANGE_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.PURPLE_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.YELLOW_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.MAGENTA_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.LIGHT_BLUE_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.LIGHT_GRAY_BED, 1),
                12,
                12,
                0.05f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 2),
                ItemStack.EMPTY,
                new ItemStack(Items.RED_BED, 1),
                12,
                12,
                0.05f
            )
        ));

        INNKEEPER_MASTER_TRADES.addAll(List.of(
            (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 15),
                new ItemStack(Items.CHORUS_FRUIT, 2),
                new ItemStack(DestroyItems.CHORUS_WINE.get(), 1),
                8,
                20,
                0.10f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 11),
                ItemStack.EMPTY,
                new ItemStack(DestroyItems.BANGERS_AND_MASH.get(), 1),
                12,
                15,
                0.10f
            ), (trader, rand) -> new MerchantOffer(
                new ItemStack(Items.EMERALD, 25),
                ItemStack.EMPTY,
                new ItemStack(DestroyBlocks.AGING_BARREL.get(), 1),
                1,
                30,
                0.20f
            )
        ));
    };

};
