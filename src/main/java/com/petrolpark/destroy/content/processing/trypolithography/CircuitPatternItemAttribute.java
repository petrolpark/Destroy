package com.petrolpark.destroy.content.processing.trypolithography;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.petrolpark.contamination.HasContaminantItemAttribute;
import com.petrolpark.destroy.DestroyItemAttributeTypes;
import com.petrolpark.util.BinaryMatrix4x4;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CircuitPatternItemAttribute implements ItemAttribute {

    public int position;
    public boolean punched;

    public CircuitPatternItemAttribute(int position, boolean punched) {
        this.position = position;
        this.punched = punched;
    };

    @Override
    public boolean appliesTo(ItemStack stack, Level world) {
        return (stack.getItem() instanceof CircuitPatternItem && BinaryMatrix4x4.is1(CircuitPatternItem.getPattern(stack), position) == punched);
    }

    @Override
    public ItemAttributeType getType() {
        return DestroyItemAttributeTypes.isCircuitPatternPunched;
    }

    @Override
    public String getTranslationKey() {
        return punched ? "circuit_pattern_punched" : "circuit_pattern_punched.inverted";
    };

    @Override
    public Object[] getTranslationParameters() {
        return new Object[]{(position % 4) + 1, (position / 4) + 1};
    };

    @Override
    public void save(CompoundTag nbt) {
        nbt.putInt("Position", position);
        nbt.putBoolean("Punched", punched);
    };

    @Override
    public void load(CompoundTag nbt) {
        this.position = nbt.getInt("Position");
        this.punched = nbt.getBoolean("Punched");
    };

    static class LegacyDeserializer implements ItemAttribute.LegacyDeserializer {

        @Override
        public String getNBTKey() {
            return "CircuitPositionPunched";
        }

        @Override
        public ItemAttribute readNBT(CompoundTag nbt) {
            return new CircuitPatternItemAttribute(nbt.getInt("Position"), nbt.getBoolean("Punched"));
        }
    }

    public static class Type implements ItemAttributeType {
        @Override
        public @NotNull ItemAttribute createAttribute() {
            return new HasContaminantItemAttribute(null);
        }

        @Override
        public List<ItemAttribute> getAllAttributes(ItemStack stack, Level level) {
            if (!(stack.getItem() instanceof CircuitPatternItem)) return List.of();
            int pattern = CircuitPatternItem.getPattern(stack);
            List<ItemAttribute> attributes = new ArrayList<>(16);
            for (int i = 0; i < 16; i++) attributes.add(new CircuitPatternItemAttribute(i, BinaryMatrix4x4.is1(pattern, i)));
            return attributes;
        };
    }
}
