package com.petrolpark.destroy.content.oil.seismology;

import java.util.List;
import java.util.function.Consumer;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.DestroyAdvancementTrigger;
import com.petrolpark.destroy.DestroyItems;
import com.petrolpark.destroy.DestroyMessages;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.content.oil.ChunkCrudeOil;
import com.petrolpark.destroy.content.oil.seismology.SeismographItem.Seismograph;
import com.petrolpark.destroy.core.extendedinventory.ExtendedInventory;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.minecraft.ChatFormatting;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Destroy.MOD_ID)
public class SeismometerItem extends Item {

    public SeismometerItem(Properties properties) {
        super(properties);
    };

    @Override
    @OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new SeismometerItemRenderer()));
	};

    /**
     * Trigger Handheld Seismometers when there are nearby Explosions.
     */
    @SubscribeEvent
    public static final void onExplosion(ExplosionEvent.Start event) {
        Level level = event.getLevel();
        level.getEntitiesOfClass(Player.class, AABB.ofSize(event.getExplosion().getPosition(), 16, 16, 16), player -> true).forEach(player -> {
            if (player.getInventory().hasAnyMatching(DestroyItems.SEISMOMETER::isIn)) {
                int chunkX = SectionPos.blockToSectionCoord(player.getOnPos().getX());
                int chunkZ = SectionPos.blockToSectionCoord(player.getOnPos().getZ());
                
                List<ItemStack> seismographs = ExtendedInventory.get(player).stream()
                    .filter(DestroyItems.SEISMOGRAPH::isIn)
                    .filter(stack -> {
                        MapItemSavedData mapData = MapItem.getSavedData(stack, level);
                        if (mapData == null) return false;
                        return (SeismographItem.mapChunkCenter(chunkX) * 16 == mapData.centerX && SeismographItem.mapChunkCenter(chunkZ) * 16 == mapData.centerZ);
                    })
                    .toList();

                // Generate the Oil in this chunk
                LevelChunk chunk = level.getChunk(chunkX, chunkZ);
                LazyOptional<ChunkCrudeOil> ccoOptional = chunk.getCapability(ChunkCrudeOil.Provider.CHUNK_CRUDE_OIL);
                int newOilGenerated = 0;
                if (ccoOptional.isPresent()) {
                    ChunkCrudeOil cco = ccoOptional.resolve().get();
                    if (!cco.isGenerated()) {
                        cco.generate(chunk, player);
                        newOilGenerated = cco.getAmount();
                    };
                };         

                boolean newInfo = false; // Whether new information was added to any Seismographs

                // Add information to Seismographs and display information to the player
                if (level instanceof ServerLevel serverLevel) {
                    byte xSignals = ChunkCrudeOil.getSignals(serverLevel, chunkX, chunkZ, true);
                    byte zSignals = ChunkCrudeOil.getSignals(serverLevel, chunkX, chunkZ, false);
                    int modX = chunkX - SeismographItem.mapChunkLowerCorner(chunkX);
                    int modZ = chunkZ - SeismographItem.mapChunkLowerCorner(chunkZ);
                    for (ItemStack stack : seismographs) {
                        Seismograph seismograph = SeismographItem.readSeismograph(stack);
                        // Mark this chunk as definitively seismically active or not on the Seismograph
                        newInfo |= seismograph.mark(modX, modZ, (zSignals & 1 << modZ) != 0 ? Seismograph.Mark.TICK : Seismograph.Mark.CROSS);
                        // Add nonogram info
                        newInfo |= seismograph.discoverColumn(modX, level, player);
                        newInfo |= seismograph.discoverRow(modZ, level, player);
                        seismograph.getColumns()[modX] = zSignals;
                        seismograph.getRows()[modZ] = xSignals;
                        SeismographItem.writeSeismograph(stack, seismograph);
                    };
                    // Show message (and award XP if necessary)
                    if (newOilGenerated > 0) {
                        player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.struck_oil", newOilGenerated / 1000).component(), true);
                        ExperienceOrb.award(serverLevel, player.position(), newOilGenerated / 10000);  
                    } else if (seismographs.isEmpty()) player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.no_seismograph").style(ChatFormatting.RED).component(), true);
                    else if (newInfo) player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.added_info").component(), true);
                    else player.displayClientMessage(DestroyLang.translate("tooltip.seismometer.no_new_info").style(ChatFormatting.RED).component(), true);
                };
                
                // Update the animation of the Seismometer(s)
                if (player instanceof ServerPlayer serverPlayer) DestroyMessages.sendToClient(new SeismometerSpikeS2CPacket(), serverPlayer);
                // Award Advancement if some Seismograph info was filled in
                if (newInfo) DestroyAdvancementTrigger.USE_SEISMOMETER.award(level, player);
            };
        });
    };
    
};
