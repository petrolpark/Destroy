package com.petrolpark.destroy;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.content.logistics.creativepump.CreativePumpBlockEntity;
import com.petrolpark.destroy.content.logistics.siphon.SiphonBlockEntity;
import com.petrolpark.destroy.content.logistics.siphon.SiphonRenderer;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackBlockEntity;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackCamBlockEntity;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackVisual;
import com.petrolpark.destroy.content.oil.pumpjack.PumpjackRenderer;
import com.petrolpark.destroy.content.processing.ageing.AgeingBarrelBlockEntity;
import com.petrolpark.destroy.content.processing.ageing.AgeingBarrelRenderer;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugeBlockEntity;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugeCogVisual;
import com.petrolpark.destroy.content.processing.centrifuge.CentrifugeRenderer;
import com.petrolpark.destroy.content.processing.cooler.CoolerBlockEntity;
import com.petrolpark.destroy.content.processing.cooler.CoolerRenderer;
import com.petrolpark.destroy.content.processing.distillation.BubbleCapBlockEntity;
import com.petrolpark.destroy.content.processing.distillation.BubbleCapRenderer;
import com.petrolpark.destroy.content.processing.dynamo.DynamoBlockEntity;
import com.petrolpark.destroy.content.processing.dynamo.DynamoCogVisual;
import com.petrolpark.destroy.content.processing.dynamo.DynamoRenderer;
import com.petrolpark.destroy.content.processing.extrusion.ExtrusionDieBlockEntity;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeBlockEntity;
import com.petrolpark.destroy.content.processing.glassblowing.BlowpipeBlockEntityRenderer;
import com.petrolpark.destroy.content.processing.sieve.MechanicalSieveBlockEntity;
import com.petrolpark.destroy.content.processing.sieve.MechanicalSieveVisual;
import com.petrolpark.destroy.content.processing.sieve.MechanicalSieveRenderer;
import com.petrolpark.destroy.content.processing.treetap.TreeTapBlockEntity;
import com.petrolpark.destroy.content.processing.treetap.TreeTapVisual;
import com.petrolpark.destroy.content.processing.treetap.TreeTapRenderer;
import com.petrolpark.destroy.content.processing.trypolithography.keypunch.KeypunchBlockEntity;
import com.petrolpark.destroy.content.processing.trypolithography.keypunch.KeypunchVisual;
import com.petrolpark.destroy.content.processing.trypolithography.keypunch.KeypunchRenderer;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgrammerBlockEntity;
import com.petrolpark.destroy.content.redstone.programmer.RedstoneProgrammerBlockEntityRenderer;
import com.petrolpark.destroy.content.sandcastle.SandCastleBlockEntity;
import com.petrolpark.destroy.core.chemistry.storage.ElementTankBlockEntity;
import com.petrolpark.destroy.core.chemistry.storage.ElementTankRenderer;
import com.petrolpark.destroy.core.chemistry.storage.SimpleMixtureTankBlockEntity.SimplePlaceableMixtureTankBlockEntity;
import com.petrolpark.destroy.core.chemistry.storage.measuringcylinder.MeasuringCylinderBlockEntity;
import com.petrolpark.destroy.core.chemistry.storage.SimpleMixtureTankRenderer;
import com.petrolpark.destroy.core.chemistry.storage.testtube.TestTubeRackRenderer;
import com.petrolpark.destroy.core.chemistry.storage.testtube.TestTubeRackBlockEntity;
import com.petrolpark.destroy.core.chemistry.vat.VatControllerBlockEntity;
import com.petrolpark.destroy.core.chemistry.vat.VatRenderer;
import com.petrolpark.destroy.core.chemistry.vat.VatSideBlockEntity;
import com.petrolpark.destroy.core.chemistry.vat.VatSideRenderer;
import com.petrolpark.destroy.core.chemistry.vat.observation.colorimeter.ColorimeterBlockEntity;
import com.petrolpark.destroy.core.explosion.DynamiteBlockEntity;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlockEntity;
import com.petrolpark.destroy.core.explosion.mixedexplosive.MixedExplosiveBlockEntityRenderer;
import com.petrolpark.destroy.core.pollution.catalyticconverter.CatalyticConverterBlockEntity;
import com.petrolpark.destroy.core.pollution.pollutometer.PollutometerBlockEntity;
import com.petrolpark.destroy.core.pollution.pollutometer.PollutometerRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class DestroyBlockEntityTypes {

    public static final BlockEntityEntry<AgeingBarrelBlockEntity> AGING_BARREL = REGISTRATE
        .blockEntity("aging_barrel", AgeingBarrelBlockEntity::new)
        .validBlocks(DestroyBlocks.AGING_BARREL)
        .renderer(() -> AgeingBarrelRenderer::new)
        .register();

    public static final BlockEntityEntry<SimplePlaceableMixtureTankBlockEntity> SIMPLE_MIXTURE_TANK = REGISTRATE
        .blockEntity("simple_mixture_tank", SimplePlaceableMixtureTankBlockEntity::new)
        .validBlocks(DestroyBlocks.BEAKER, DestroyBlocks.ROUND_BOTTOMED_FLASK)
        .renderer(() -> SimpleMixtureTankRenderer::new)
        .register();

    public static final BlockEntityEntry<BlowpipeBlockEntity> BLOWPIPE = REGISTRATE
        .blockEntity("blowpipe", BlowpipeBlockEntity::new)
        .validBlocks(DestroyBlocks.BLOWPIPE)
        .renderer(() -> BlowpipeBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<BubbleCapBlockEntity> BUBBLE_CAP = REGISTRATE
        .blockEntity("bubble_cap", BubbleCapBlockEntity::new)
        .validBlocks(DestroyBlocks.BUBBLE_CAP)
        .renderer(() -> BubbleCapRenderer::new)
        .register();

    public static final BlockEntityEntry<CatalyticConverterBlockEntity> CATALYTIC_CONVERTER = REGISTRATE
        .blockEntity("catalytic_converter", CatalyticConverterBlockEntity::new)
        .validBlocks(DestroyBlocks.CATALYTIC_CONVERTER)
        .register();

    public static final BlockEntityEntry<CentrifugeBlockEntity> CENTRIFUGE = REGISTRATE
        .blockEntity("centrifuge", CentrifugeBlockEntity::new)
        .visual(() -> CentrifugeCogVisual::new)
        .validBlocks(DestroyBlocks.CENTRIFUGE)
        .renderer(() -> CentrifugeRenderer::new)
        .register();

    public static final BlockEntityEntry<ColorimeterBlockEntity> COLORIMETER = REGISTRATE
        .blockEntity("colorimeter", ColorimeterBlockEntity::new)
        .validBlocks(DestroyBlocks.COLORIMETER)
        .register();

    public static final BlockEntityEntry<CoolerBlockEntity> COOLER = REGISTRATE
        .blockEntity("cooler", CoolerBlockEntity::new)
        .validBlocks(DestroyBlocks.COOLER)
        .renderer(() -> CoolerRenderer::new)
        .register();

    public static final BlockEntityEntry<CreativePumpBlockEntity> CREATIVE_PUMP = REGISTRATE
        .blockEntity("creative_pump", CreativePumpBlockEntity::new)
        .validBlocks(DestroyBlocks.CREATIVE_PUMP)
        .register();

    public static final BlockEntityEntry<MixedExplosiveBlockEntity> CUSTOM_EXPLOSIVE_MIX = REGISTRATE
        .blockEntity("custom_explosive_mix", MixedExplosiveBlockEntity::new)
        .validBlocks(DestroyBlocks.CUSTOM_EXPLOSIVE_MIX)
        .renderer(() -> MixedExplosiveBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<DynamiteBlockEntity> DYNAMITE = REGISTRATE
        .blockEntity("dynamite", DynamiteBlockEntity::new)
        .validBlocks(DestroyBlocks.DYNAMITE_BLOCK)
        .register();

    public static final BlockEntityEntry<DynamoBlockEntity> DYNAMO = REGISTRATE
        .blockEntity("dynamo", DynamoBlockEntity::new)
        .visual(() -> DynamoCogVisual::new)
        .validBlocks(DestroyBlocks.DYNAMO)
        .renderer(() -> DynamoRenderer::new)
        .register();
    
    public static final BlockEntityEntry<ElementTankBlockEntity> ELEMENT_TANK = REGISTRATE
        .blockEntity("element_tank", ElementTankBlockEntity::new)
        .validBlocks(DestroyBlocks.ELEMENT_TANK)
        .renderer(() -> ElementTankRenderer::new)
        .register();

    public static final BlockEntityEntry<ExtrusionDieBlockEntity> EXTRUSION_DIE = REGISTRATE
        .blockEntity("extrusion_die", ExtrusionDieBlockEntity::new)
        .validBlocks(DestroyBlocks.EXTRUSION_DIE)
        .register();

    public static final BlockEntityEntry<KeypunchBlockEntity> KEYPUNCH = REGISTRATE
        .blockEntity("keypunch", KeypunchBlockEntity::new)
        .visual(() -> KeypunchVisual::new)
        .validBlocks(DestroyBlocks.KEYPUNCH)
        .renderer(() -> KeypunchRenderer::new)
        .register();

    public static final BlockEntityEntry<MeasuringCylinderBlockEntity> MEASURING_CYLINDER = REGISTRATE
        .blockEntity("measuring_cylinder", MeasuringCylinderBlockEntity::new)
        .validBlock(DestroyBlocks.MEASURING_CYLINDER)
        .renderer(() -> SimpleMixtureTankRenderer::new)
        .register();

    public static final BlockEntityEntry<MechanicalSieveBlockEntity> MECHANICAL_SIEVE = REGISTRATE
        .blockEntity("mechanical_sieve", MechanicalSieveBlockEntity::new)
        .visual(() -> MechanicalSieveVisual::new)
        .validBlock(DestroyBlocks.MECHANICAL_SIEVE)
        .renderer(() -> MechanicalSieveRenderer::new)
        .register();

    public static final BlockEntityEntry<PollutometerBlockEntity> POLLUTOMETER = REGISTRATE
        .blockEntity("pollutometer", PollutometerBlockEntity::new)
        .validBlocks(DestroyBlocks.POLLUTOMETER)
        .renderer(() -> PollutometerRenderer::new)
        .register();

    public static final BlockEntityEntry<PumpjackBlockEntity> PUMPJACK = REGISTRATE
        .blockEntity("pumpjack", PumpjackBlockEntity::new)
		.visual(() -> PumpjackVisual::new)
		.validBlocks(DestroyBlocks.PUMPJACK)
		.renderer(() -> PumpjackRenderer::new)
		.register();

    public static final BlockEntityEntry<PumpjackCamBlockEntity> PUMPJACK_CAM = REGISTRATE
        .blockEntity("pumpjack_cam", PumpjackCamBlockEntity::new)
		.validBlocks(DestroyBlocks.PUMPJACK_CAM)
		.register();

    public static final BlockEntityEntry<RedstoneProgrammerBlockEntity> REDSTONE_PROGRAMMER = REGISTRATE
        .blockEntity("redstone_programmer", RedstoneProgrammerBlockEntity::new)
        .validBlocks(DestroyBlocks.REDSTONE_PROGRAMMER)
        .renderer(() -> RedstoneProgrammerBlockEntityRenderer::new)
        .register();

    public static final BlockEntityEntry<SandCastleBlockEntity> SAND_CASTLE = REGISTRATE
        .blockEntity("sand_castle", SandCastleBlockEntity::new)
        .validBlocks(DestroyBlocks.SAND_CASTLE)
        .register();

    public static final BlockEntityEntry<SiphonBlockEntity> SIPHON = REGISTRATE
        .blockEntity("siphon", SiphonBlockEntity::new)
        .validBlocks(DestroyBlocks.SIPHON)
        .renderer(() -> SiphonRenderer::new)
        .register();

    public static final BlockEntityEntry<TestTubeRackBlockEntity> TEST_TUBE_RACK = REGISTRATE
        .blockEntity("test_tube_rack", TestTubeRackBlockEntity::new)
        .validBlocks(DestroyBlocks.TEST_TUBE_RACK)
        .renderer(() -> TestTubeRackRenderer::new)
        .register();

    public static final BlockEntityEntry<TreeTapBlockEntity> TREE_TAP = REGISTRATE
        .blockEntity("tree_tap", TreeTapBlockEntity::new)
        .visual(() -> TreeTapVisual::new)
        .validBlock(DestroyBlocks.TREE_TAP)
        .renderer(() -> TreeTapRenderer::new)
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
