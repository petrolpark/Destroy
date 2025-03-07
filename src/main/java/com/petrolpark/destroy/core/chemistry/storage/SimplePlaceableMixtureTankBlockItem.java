package com.petrolpark.destroy.core.chemistry.storage;

import java.util.function.Consumer;

import org.joml.Vector3f;

import com.petrolpark.destroy.core.chemistry.storage.SimpleMixtureTankRenderer.ISimpleMixtureTankRenderInformation;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import net.createmod.catnip.data.Couple;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fluids.FluidStack;

public class SimplePlaceableMixtureTankBlockItem<T extends SimplePlaceableMixtureTankBlock> extends PlaceableMixtureTankItem<T> implements ISimpleMixtureTankRenderInformation<ItemStack> {

    public SimplePlaceableMixtureTankBlockItem(T block, Properties properties) {
        super(block, properties.stacksTo(1));
    };

    @Override
    public int getCapacity(ItemStack stack) {
        return tankBlock.getMixtureCapacity();
    };
    
    @Override
    public Couple<Vector3f> getFluidBoxDimensions() {
       return tankBlock.getFluidBoxDimensions();
    };

    @Override
    public float getFluidLevel(ItemStack container, float partialTicks) {
        return getContents(container).map(fs -> (float)fs.getAmount()).orElse(0f) / getCapacity(container);
    };

    @Override
    public FluidStack getRenderedFluid(ItemStack container) {
        return getContents(container).orElse(FluidStack.EMPTY);
    };

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SimpleMixtureTankItemRenderer(this)));
    };
};
