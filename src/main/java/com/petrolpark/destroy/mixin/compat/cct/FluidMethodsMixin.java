package com.petrolpark.destroy.mixin.compat.cct;

import com.petrolpark.destroy.chemistry.legacy.LegacyMixture;
import com.petrolpark.destroy.chemistry.minecraft.MixtureFluid;
import dan200.computercraft.api.detail.ForgeDetailRegistries;
import dan200.computercraft.shared.peripheral.generic.methods.FluidMethods;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Mixin(FluidMethods.class)
public class FluidMethodsMixin {
    @Inject(
            method = "tanks(Lnet/minecraftforge/fluids/capability/IFluidHandler;)Ljava/util/Map;",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true,
            remap = false
    )
    private void inTanks(IFluidHandler fluids, CallbackInfoReturnable<Map<Integer, Map<String, ?>>> cir) {
        Map<Integer, Map<String, ?>> result = new HashMap();
        int size = fluids.getTanks();

        for(int i = 0; i < size; ++i) {
            FluidStack stack = fluids.getFluidInTank(i);
            if (!stack.isEmpty()) {
                Map<String, Object> details = ForgeDetailRegistries.FLUID_STACK.getBasicDetails(stack);
                if (details.get("name").equals("destroy:mixture")) {
                    Map<Integer, Map<String, ?>> contentsTable = new HashMap<>();
                    CompoundTag data = new CompoundTag();

                    stack.writeToNBT(data);
                    ListTag contents = data
                            .getCompound("Tag")
                            .getCompound("Mixture")
                            .getList("Contents", 10);

                    contents.forEach(tag -> {
                        Map<String, Object> molecule = new HashMap<>();
                        CompoundTag moleculeTag = (CompoundTag) tag;

                        molecule.put("id", moleculeTag.getString("Molecule"));
                        molecule.put("concentration", moleculeTag.getFloat("Concentration"));
                        contentsTable.put(contentsTable.size() + 1, molecule);
                    });

                    details.put("contents", contentsTable);
                }
                result.put(i + 1, details);
            }
        }

        cir.setReturnValue(result);
        cir.cancel();
    }
}
