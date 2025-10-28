package com.petrolpark.destroy.core.universalarmortrim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.petrolpark.destroy.MoveToPetrolparkLibrary;
import com.petrolpark.destroy.client.DestroySpriteSource;
import com.petrolpark.destroy.client.DummyBaker;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraftforge.client.model.CompositeModel;

@MoveToPetrolparkLibrary
public class UniversalArmorTrimItemOverrides extends ItemOverrides {

    public static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    public static Map<ArmorMaterial, Map<ArmorItem.Type, Map<TrimMaterial, BakedModel>>> MODELS = new HashMap<>(); // Map of armor materials to maps of armor item types to maps of trim materials to the model for that piece of armor of that type and that armor material with a trim of that trim material

    private final ItemOverrides wrapped;

    public UniversalArmorTrimItemOverrides(ItemOverrides wrapped) {
        this.wrapped = wrapped;
    };
    
    @Override
    public BakedModel resolve(BakedModel model, ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
        BakedModel defaultModel = wrapped.resolve(model, stack, level, entity, seed);
        if (level == null) return defaultModel;
        Optional<TrimMaterial> materialOptional = ArmorTrim.getTrim(level.registryAccess(), stack).map(ArmorTrim::material).map(Holder::value);
        if (materialOptional.isEmpty()) return defaultModel;
        if (!(stack.getItem() instanceof ArmorItem armorItem)) return defaultModel;
        return getModel(
            Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(stack), // Calling ItemRenderer#getModel will recurse; we know this isn't a Trident or Spyglass so this method is fine
            armorItem.getMaterial(), armorItem.getType(),
            materialOptional.get()
        ).orElse(defaultModel);
    };

    @Override
    public ImmutableList<BakedOverride> getOverrides() {
        return wrapped.getOverrides();
    };

    @SuppressWarnings("deprecation")
    public static Optional<BakedModel> getModel(BakedModel baseModel, ArmorMaterial armorMaterial, ArmorItem.Type armorType, TrimMaterial trimMaterial) {
        if (!DestroySpriteSource.UNIVERSAL_ARMOR_TRIMS.contains(trimMaterial.assetName())) return Optional.empty();
        Map<ArmorItem.Type, Map<TrimMaterial, BakedModel>> armorTypeMap = MODELS.computeIfAbsent(armorMaterial, am -> new HashMap<>());
        Map<TrimMaterial, BakedModel> trimMaterialMap = armorTypeMap.computeIfAbsent(armorType, at -> new HashMap<>());
        BakedModel overlayModel = getOverlayModel(armorType, trimMaterial);
        if (overlayModel == null) return Optional.empty();
        BakedModel model = new CompositeModel.Baked(
            false,
            false,
            false,
            baseModel.getParticleIcon(),
            baseModel.getTransforms(),
            ItemOverrides.EMPTY,
            ImmutableMap.of(),
            ImmutableList.of(baseModel, overlayModel)
        );
        trimMaterialMap.put(trimMaterial, model);
        return Optional.ofNullable(model);
    };

    public static final ResourceLocation defaultItemModelRl = ResourceLocation.withDefaultNamespace("generated");

    @Nullable
    @SuppressWarnings("deprecation")
    public static BakedModel getOverlayModel(ArmorItem.Type armorType, TrimMaterial trimMaterial) {
        ResourceLocation trimTextureId = ResourceLocation.withDefaultNamespace(armorType.getName() + "_trim_" + trimMaterial.assetName());
        Material trimTexture = new Material(TextureAtlas.LOCATION_BLOCKS, trimTextureId.withPrefix("trims/items/"));
        if (trimTexture.sprite() == null) return null; // Check the texture exists (there's no reason why it shouldn't)
        return DummyBaker.bake(
            ITEM_MODEL_GENERATOR.generateBlockModel(
                Material::sprite, // Tell the Item Model Generator how to fetch the sprite
                new BlockModel(
                    defaultItemModelRl,
                    new ArrayList<>(), // No elements - these get added by the Item Model Generator
                    Map.of("layer0", Either.left(trimTexture)),
                    null,
                    null,
                    ItemTransforms.NO_TRANSFORMS, // These aren't needed as the model will be composited - the Item Model Generator sets them anyway
                    new ArrayList<>()
                )
            ),
            trimTextureId
        );
    };
};
