package com.petrolpark.destroy.block.entity;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.block.DestroyBlocks;
import com.petrolpark.destroy.block.instance.CentrifugeCogInstance;
import com.petrolpark.destroy.block.instance.DoubleCardanShaftInstance;
import com.petrolpark.destroy.block.instance.DynamoCogInstance;
import com.petrolpark.destroy.block.instance.PlanetaryGearsetInstance;
import com.petrolpark.destroy.block.renderer.AgingBarrelRenderer;
import com.petrolpark.destroy.block.renderer.BubbleCapRenderer;
import com.petrolpark.destroy.block.renderer.CentrifugeRenderer;
import com.petrolpark.destroy.block.renderer.CoolerRenderer;
import com.petrolpark.destroy.block.renderer.DoubleCardanShaftRenderer;
import com.petrolpark.destroy.block.renderer.DynamoRenderer;
import com.petrolpark.destroy.block.renderer.PlanetaryGearsetRenderer;
import com.petrolpark.destroy.block.renderer.PollutometerRenderer;
import com.petrolpark.destroy.block.renderer.PumpjackRenderer;
import com.petrolpark.destroy.block.renderer.VatRenderer;
import com.petrolpark.destroy.block.renderer.VatSideRenderer;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.simpleRelays.BracketedKineticBlockEntityRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class DestroyBlockEntityTypes {

    public static final BlockEntityEntry<AgingBarrelBlockEntity> AGING_BARREL = REGISTRATE
        .blockEntity("aging_barrel", AgingBarrelBlockEntity::new)
        .validBlocks(DestroyBlocks.AGING_BARREL)
        .renderer(() -> AgingBarrelRenderer::new)
        .register();

    public static final BlockEntityEntry<BubbleCapBlockEntity> BUBBLE_CAP = REGISTRATE
        .blockEntity("bubble_cap", BubbleCapBlockEntity::new)
        .validBlocks(DestroyBlocks.BUBBLE_CAP)
        .renderer(() -> BubbleCapRenderer::new)
        .register();

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = REGISTRATE
        .blockEntity("centrifuge", CentrifugeBlockEntity::new)
        .instance(() -> CentrifugeCogInstance::new)
        .validBlocks(DestroyBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();

    public static final BlockEntityEntry<CoaxialGearBlockEntity> COAXIAL_GEAR = REGISTRATE
		.blockEntity("coaxial_gear", CoaxialGearBlockEntity::new)
		.instance(() -> BracketedKineticBlockEntityInstance::new, false)
		.validBlocks(DestroyBlocks.COAXIAL_GEAR)
		.renderer(() -> BracketedKineticBlockEntityRenderer::new)
		.register();

    public static final BlockEntityEntry<CoolerBlockEntity> COOLER = REGISTRATE
        .blockEntity("cooler", CoolerBlockEntity::new)
        .validBlocks(DestroyBlocks.COOLER)
        .renderer(() -> CoolerRenderer::new)
        .register();

    public static final BlockEntityEntry<DoubleCardanShaftBlockEntity> DOUBLE_CARDAN_SHAFT = REGISTRATE
        .blockEntity("double_cardan_shaft", DoubleCardanShaftBlockEntity::new)
        .instance(() -> DoubleCardanShaftInstance::new)
        .validBlock(DestroyBlocks.DOUBLE_CARDAN_SHAFT)
        .renderer(() -> DoubleCardanShaftRenderer::new)
        .register();

    public static final BlockEntityEntry<DynamiteBlockEntity> DYNAMITE = REGISTRATE
        .blockEntity("dynamite", DynamiteBlockEntity::new)
        .validBlocks(DestroyBlocks.DYNAMITE_BLOCK)
        .register();

    public static final BlockEntityEntry<DynamoBlockEntity> DYNAMO = REGISTRATE
        .blockEntity("dynamo", DynamoBlockEntity::new)
        .instance(() -> DynamoCogInstance::new)
        .validBlocks(DestroyBlocks.DYNAMO)
        .renderer(() -> DynamoRenderer::new)
        .register();

    public static final BlockEntityEntry<ExtrusionDieBlockEntity> EXTRUSION_DIE = REGISTRATE
        .blockEntity("extrusion_die", ExtrusionDieBlockEntity::new)
        .validBlocks(DestroyBlocks.EXTRUSION_DIE)
        .register();

    public static final BlockEntityEntry<LongShaftBlockEntity> LONG_SHAFT = REGISTRATE
        .blockEntity("long_shaft", LongShaftBlockEntity::new)
        .instance(() -> BracketedKineticBlockEntityInstance::new, false)
        .validBlocks(DestroyBlocks.LONG_SHAFT)
        .renderer(() -> BracketedKineticBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<PlanetaryGearsetBlockEntity> PLANETARY_GEARSET = REGISTRATE
        .blockEntity("planetary_gearset", PlanetaryGearsetBlockEntity::new)
        .instance(() -> PlanetaryGearsetInstance::new, false)
        .validBlocks(DestroyBlocks.PLANETARY_GEARSET)
        .renderer(() -> PlanetaryGearsetRenderer::new)
        .register();

    public static final BlockEntityEntry<PollutometerBlockEntity> POLLUTOMETER = REGISTRATE
        .blockEntity("pollutometer", PollutometerBlockEntity::new)
        .validBlocks(DestroyBlocks.POLLUTOMETER)
        .renderer(() -> PollutometerRenderer::new)
        .register();

    public static final BlockEntityEntry<PumpjackBlockEntity> PUMPJACK = REGISTRATE
        .blockEntity("pumpjack", PumpjackBlockEntity::new)
		//.instance(() -> PumpjackInstance::new, false) Can't use instancing because that can't render cutout for some reason
		.validBlocks(DestroyBlocks.PUMPJACK)
		.renderer(() -> PumpjackRenderer::new)
		.register();

    public static final BlockEntityEntry<PumpjackCamBlockEntity> PUMPJACK_CAM = REGISTRATE
        .blockEntity("pumpjack_cam", PumpjackCamBlockEntity::new)
		.validBlocks(DestroyBlocks.PUMPJACK_CAM)
		.register();

    public static final BlockEntityEntry<SandCastleBlockEntity> SAND_CASTLE = REGISTRATE
        .blockEntity("sand_castle", SandCastleBlockEntity::new)
        .validBlocks(DestroyBlocks.SAND_CASTLE)
        .register();

    public static final BlockEntityEntry<VatControllerBlockEntity> VAT_CONTROLLER = REGISTRATE
        .blockEntity("vat_controller", VatControllerBlockEntity::new)
        .validBlock(DestroyBlocks.VAT_CONTROLLER)
        .renderer(() -> VatRenderer::new)
        .register();

    public static final BlockEntityEntry<VatSideBlockEntity> VAT_SIDE = REGISTRATE
        .blockEntity("vat_side", VatSideBlockEntity::new)
        .validBlock(DestroyBlocks.VAT_SIDE)
        .renderer(() -> VatSideRenderer::new)
        .register();

    public static void register() {};
    
};
