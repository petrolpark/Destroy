package com.petrolpark.destroy.client;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyBlocks;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.core.chemistry.vat.material.VatMaterial;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.api.registration.MultiTagBuilder;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class DestroyPonderTags {

    public static final ResourceLocation
        CHEMISTRY = Destroy.asResource("chemistry"),
        DESTROY = Destroy.asResource("destroy"),
        VAT_SIDE_BLOCKS = Destroy.asResource("vat_side_blocks");

    private static PonderTagRegistrationHelper<RegistryEntry<?>> HELPER = null;
    private static PonderTagRegistrationHelper<ItemLike> ITEM_HELPER = null;

    public static void register(PonderTagRegistrationHelper<ResourceLocation> helper) {
        HELPER = helper.withKeyFunction(RegistryEntry::getId);
        ITEM_HELPER = helper.withKeyFunction(CatnipServices.REGISTRIES::getKeyOrThrow);

        helper.registerTag(CHEMISTRY)
            .addToIndex()
            .item(DestroyItems.TEST_TUBE, true, false)
            .register();

        helper.registerTag(DESTROY)
            .addToIndex()
            .item(DestroyItems.LOGO)
            .register();

        helper.registerTag(VAT_SIDE_BLOCKS)
            .addToIndex()
            .item(DestroyBlocks.VAT_CONTROLLER, true, false)
            .register();

        HELPER.addToTag(CHEMISTRY)
            .add(AllBlocks.BASIN)
            .add(DestroyBlocks.BLACKLIGHT)
            .add(AllBlocks.BLAZE_BURNER)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COOLER)
            .add(AllBlocks.MECHANICAL_MIXER)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        HELPER.addToTag(DESTROY)
            .add(DestroyBlocks.AGING_BARREL)
            .add(DestroyBlocks.BLACKLIGHT)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CATALYTIC_CONVERTER)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COOLER)
            .add(DestroyBlocks.CREATIVE_PUMP)
            .add(DestroyBlocks.CUSTOM_EXPLOSIVE_MIX)
            .add(DestroyBlocks.DYNAMO)
            .add(DestroyBlocks.EXTRUSION_DIE)
            .add(DestroyItems.HYPERACCUMULATING_FERTILIZER)
            .add(DestroyBlocks.KEYPUNCH)
            .add(DestroyItems.POLLUTION_SYMBOL)
            .add(DestroyBlocks.PUMPJACK)
            .add(DestroyBlocks.REDSTONE_PROGRAMMER)
            .add(DestroyItems.SEISMOMETER)
            .add(DestroyItems.SEISMOGRAPH)
            .add(DestroyBlocks.SIPHON)
            .add(DestroyBlocks.TREE_TAP)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        MultiTagBuilder.Tag<ItemLike> vatSideBlocksTagBuilder = ITEM_HELPER.addToTag(VAT_SIDE_BLOCKS);
        VatMaterial.BLOCK_MATERIALS.forEach((blockIngredient, material) -> blockIngredient.getDisplayedItemStacks().forEach(stack -> vatSideBlocksTagBuilder.add(stack.getItem())));

        HELPER.addToTag(AllCreatePonderTags.FLUIDS)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CATALYTIC_CONVERTER)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.CREATIVE_PUMP)
            .add(DestroyBlocks.PUMPJACK)
            .add(DestroyBlocks.SIPHON)
            .add(DestroyBlocks.TREE_TAP)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        HELPER.addToTag(AllCreatePonderTags.KINETIC_APPLIANCES)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.DYNAMO)
            .add(DestroyBlocks.KEYPUNCH)
            .add(DestroyBlocks.MECHANICAL_SIEVE)
            .add(DestroyBlocks.PUMPJACK)
            .add(DestroyBlocks.TREE_TAP)
        ;

        HELPER.addToTag(AllCreatePonderTags.ARM_TARGETS)
            .add(DestroyBlocks.AGING_BARREL)
        ;

        HELPER.addToTag(AllCreatePonderTags.REDSTONE)
            .add(DestroyBlocks.DYNAMO)
            .add(DestroyBlocks.REDSTONE_PROGRAMMER)
            .add(DestroyBlocks.SIPHON)
        ;

        HELPER.addToTag(AllCreatePonderTags.DISPLAY_SOURCES)
            .add(DestroyBlocks.BUBBLE_CAP)
            .add(DestroyBlocks.CENTRIFUGE)
            .add(DestroyBlocks.COLORIMETER)
            .add(DestroyBlocks.POLLUTOMETER)
            .add(DestroyBlocks.VAT_CONTROLLER)
        ;

        HELPER.addToTag(AllCreatePonderTags.CONTRAPTION_ACTOR)
            .add(DestroyBlocks.EXTRUSION_DIE)
        ;

        HELPER.addToTag(AllCreatePonderTags.CREATIVE)
            .add(DestroyBlocks.CREATIVE_PUMP)
        ;
    }

    public static final void refreshVatMaterialsTag() {
        MultiTagBuilder.Tag<ItemLike> vatSideBlocksTagBuilder = ITEM_HELPER.addToTag(VAT_SIDE_BLOCKS);
        VatMaterial.BLOCK_MATERIALS.keySet().forEach(blockIngredient -> blockIngredient.getDisplayedItemStacks().forEach(stack -> vatSideBlocksTagBuilder.add(stack.getItem())));
    }

}
