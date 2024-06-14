package com.petrolpark.destroy.compat.crafttweaker.natives;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.tag.manager.ITagManager;
import com.blamejared.crafttweaker.api.tag.type.KnownTag;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.blamejared.crafttweaker_annotations.annotations.NativeTypeRegistration;
import com.petrolpark.destroy.chemistry.IItemReactant;
import com.petrolpark.destroy.chemistry.Molecule;
import com.petrolpark.destroy.chemistry.Reaction;
import com.petrolpark.destroy.chemistry.ReactionResult;
import com.petrolpark.destroy.compat.crafttweaker.action.AddReactionAction;
import net.minecraft.world.item.Item;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.shared.Tag;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ZenRegister
@Document("mods/destroy/ReactionBuilder")
@NativeTypeRegistration(value = Reaction.ReactionBuilder.class, zenCodeName = "mods.destroy.ReactionBuilder")
public class CTReactionBuilder {
    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addReactant(Reaction.ReactionBuilder internal, Molecule molecule) {
        return internal.addReactant(molecule);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addReactant(Reaction.ReactionBuilder internal, Molecule molecule, int ratio) {
        return internal.addReactant(molecule, ratio);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addReactant(Reaction.ReactionBuilder internal, Molecule molecule, int ratio, int order) {
        return internal.addReactant(molecule, ratio, order);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder setOrder(Reaction.ReactionBuilder internal, Molecule molecule, int order) {
        return internal.setOrder(molecule, order);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addItemReactant(Reaction.ReactionBuilder internal, IItemStack itemReactant, float moles) {
        return internal.addItemReactant(new IItemReactant.SimpleItemReactant(() -> itemReactant.getInternal().getItem()), moles);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addSimpleItemReactant(Reaction.ReactionBuilder internal, Supplier<Item> item, float moles) {
        return internal.addSimpleItemReactant(item, moles);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addSimpleItemTagReactant(Reaction.ReactionBuilder internal, KnownTag<Item> tag, float moles) {
        return internal.addSimpleItemTagReactant(tag.getTagKey(), moles);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addSimpleItemCatalyst(Reaction.ReactionBuilder internal, Supplier<Item> item, float moles) {
        return internal.addSimpleItemCatalyst(item, moles);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addSimpleItemTagCatalyst(Reaction.ReactionBuilder internal, KnownTag<Item> tag, float moles) {
        return internal.addSimpleItemTagCatalyst(tag.getTagKey(), moles);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder requireUV(Reaction.ReactionBuilder internal) {
        return internal.requireUV();
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addProduct(Reaction.ReactionBuilder internal, Molecule molecule) {
        return internal.addProduct(molecule);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addProduct(Reaction.ReactionBuilder internal, Molecule molecule, int ratio) {
        return internal.addProduct(molecule, ratio);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder addCatalyst(Reaction.ReactionBuilder internal, Molecule molecule, int order) {
        return internal.addCatalyst(molecule, order);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder dontIncludeInJei(Reaction.ReactionBuilder internal) {
        return internal.dontIncludeInJei();
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder displayAsReversible(Reaction.ReactionBuilder internal) {
        return internal.displayAsReversible();
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder id(Reaction.ReactionBuilder internal, String id) {
        return internal.id(id);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder preexponentialFactor(Reaction.ReactionBuilder internal, float preexponentialFactor) {
        return internal.preexponentialFactor(preexponentialFactor);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder activationEnergy(Reaction.ReactionBuilder internal, float activationEnergy) {
        return internal.activationEnergy(activationEnergy);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder enthalpyChange(Reaction.ReactionBuilder internal, float enthalpyChange) {
        return internal.enthalpyChange(enthalpyChange);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder withResult(Reaction.ReactionBuilder internal, float moles, BiFunction<Float, Reaction, ReactionResult> reactionresultFactory) {
        return internal.withResult(moles, reactionresultFactory);
    }

    @ZenCodeType.Method
    public static Reaction acid(Reaction.ReactionBuilder internal, Molecule acid, Molecule conjugateBase, float pKa) {
        return internal.acid(acid, conjugateBase, pKa);
    }

    @ZenCodeType.Method
    public static Reaction.ReactionBuilder reverseReaction(Reaction.ReactionBuilder internal, Consumer<Reaction.ReactionBuilder> reverseReactionModifier) {
        return internal.reverseReaction(reverseReactionModifier);
    }

    @ZenCodeType.Method
    public static Reaction build(Reaction.ReactionBuilder internal) {
        Reaction result = internal.build();
        CraftTweakerAPI.apply(new AddReactionAction(result));
        return result;
    }
}