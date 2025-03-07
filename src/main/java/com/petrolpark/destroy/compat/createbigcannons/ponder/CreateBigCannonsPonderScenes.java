package com.petrolpark.destroy.compat.createbigcannons.ponder;

import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.client.DestroyPonderTags;
import com.petrolpark.destroy.compat.createbigcannons.block.CreateBigCannonsBlocks;
import com.petrolpark.destroy.core.explosion.ExplosivesPonderScenes;

import com.simibubi.create.Create;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.foundation.registration.DefaultPonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.ponder.CBCPonderTags;
import rbasamoyai.createbigcannons.ponder.CannonLoadingScenes;

public class CreateBigCannonsPonderScenes {

    private static PonderSceneRegistrationHelper<ResourceLocation> HELPER = null;
    private static PonderSceneRegistrationHelper<ResourceLocation> CBC_HELPER = null;
    
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        HELPER = helper.withKeyFunction((s) -> ResourceLocation.tryParse(Destroy.MOD_ID));
        CBC_HELPER = helper.withKeyFunction((s) -> ResourceLocation.tryParse(CreateBigCannons.MOD_ID));

        HELPER.forComponents(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE.getId())
            .addStoryBoard("explosives/custom_explosive_mix_charge", (u, s) -> ExplosivesPonderScenes.filling(u, s, CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE::asStack))
            .addStoryBoard("explosives/custom_explosive_mix_charge", (u, s) -> ExplosivesPonderScenes.dyeing(u, s, CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE::asStack));
        /*CBC_HELPER.forComponents(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE.getId())
			.addStoryBoard("munitions/cannon_loads", CannonLoadingScenes::cannonLoads, CBCPonderTags.MUNITIONS);*/ // TODO: CBC

        /*HELPER.forComponents(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL.getId())
            .addStoryBoard("explosives/custom_explosive_mix_shell", (u, s) -> ExplosivesPonderScenes.filling(u, s, CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL::asStack))
            .addStoryBoard("explosives/custom_explosive_mix_shell_explosion", ExplosivesPonderScenes::exploding)
            .addStoryBoard("explosives/custom_explosive_mix_shell", (u, s) -> ExplosivesPonderScenes.dyeing(u, s, CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL::asStack));
        CBC_HELPER.forComponents(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL.getId())
            .addStoryBoard("munitions/fuzing_munitions", CannonLoadingScenes::fuzingMunitions, CBCPonderTags.MUNITIONS);*/ // TODO: CBC
    
    };

    public static void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        helper.registerTag(DestroyPonderTags.DESTROY)
            .item(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE);
        /*    .item(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL);

        helper.registerTag(CBCPonderTags.MUNITIONS)
            .item(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_CHARGE)
            .item(CreateBigCannonsBlocks.CUSTOM_EXPLOSIVE_MIX_SHELL);*/ // TODO: CBC
            
    };
};
