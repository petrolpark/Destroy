package com.petrolpark.destroy.block.display;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.petrolpark.destroy.chemistry.ClientMixture;
import com.petrolpark.destroy.chemistry.ReadOnlyMixture;
import com.petrolpark.destroy.util.DestroyLang;
import com.petrolpark.destroy.util.DestroyLang.TemperatureUnit;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fluids.FluidStack;

public abstract class MixtureContentsDisplaySource extends DisplaySource {

    private static DecimalFormat df = new DecimalFormat();
    static {
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
    };

    /**
     * Whether to display Molecule quantities as a number of moles rather than a concentration.
     */
    protected final boolean molesNotConcentration;

    public MixtureContentsDisplaySource(boolean molesNotConcentration) {
        this.molesNotConcentration = molesNotConcentration;
    };

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext context, DisplayTargetStats stats) {

        List<MutableComponent> tooltip = new ArrayList<>();
        MutableComponent name;
        String temperature = "";

        FluidStack fluidStack = getFluidStack(context);
        if (fluidStack == null || fluidStack.isEmpty()) return tooltip;

        CompoundTag mixtureTag = fluidStack.getOrCreateTag().getCompound("Mixture");
        if (mixtureTag.isEmpty()) { // If this is not a Mixture
            name = fluidStack.getDisplayName().copy();
        } else { // If this is a Mixture
            boolean iupac = !context.sourceConfig().getBoolean("MoleculeNameType");
            TemperatureUnit temperatureUnit = TemperatureUnit.values()[context.sourceConfig().getInt("TemperatureUnit")];
            ReadOnlyMixture mixture = ReadOnlyMixture.readNBT(ClientMixture::new, mixtureTag);

            name = mixture.getName().copy();
            temperature = temperatureUnit.of(mixture.getTemperature());
            tooltip.addAll(mixture.getContentsTooltip(iupac, true, molesNotConcentration, fluidStack.getAmount(), df).stream().map(c -> c.copy()).toList());
        };

        if (!molesNotConcentration) name.append(" "+fluidStack.getAmount()).append(Lang.translateDirect("generic.unit.millibuckets"));

        tooltip.add(0, name
            .append(" "+temperature)
        );

        return tooltip;
    };

    /**
     * Get the Fluid Stack which should be displayed.
     * @return
     */
    @Nullable
    public abstract FluidStack getFluidStack(DisplayLinkContext context);

	@Override
	public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
		if (isFirstLine) {
            addTemperatureUnitSelection(builder);
        } else {
            addMoleculeNameTypeSelection(builder);
        };
	};

    private void addTemperatureUnitSelection(ModularGuiLineBuilder builder) {
        builder.addSelectionScrollInput(0, 137, (si, l) -> {
            si.forOptions(List.of(DestroyLang.translate("display_source.mixture.temperature_unit.kelvin").component(), DestroyLang.translate("display_source.mixture.temperature_unit.celcius").component(), DestroyLang.translate("display_source.mixture.temperature_unit.farenheit").component()))
            .titled(DestroyLang.translate("display_source.mixture.temperature_unit").component());   
        }, "TemperatureUnit");
    };

    private void addMoleculeNameTypeSelection(ModularGuiLineBuilder builder) {
        builder.addSelectionScrollInput(0, 137, (si, l) -> {
            si.forOptions(List.of(DestroyLang.translate("display_source.mixture.molecule_name_type.iupac").component(), DestroyLang.translate("display_source.mixture.molecule_name_type.common").component()))
            .titled(DestroyLang.translate("display_source.mixture.molecule_name_type").component());
        }, "MoleculeNameType");
    };
};
