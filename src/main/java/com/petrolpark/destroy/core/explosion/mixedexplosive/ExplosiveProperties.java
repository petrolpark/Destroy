package com.petrolpark.destroy.core.explosion.mixedexplosive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.petrolpark.destroy.Destroy;
import com.petrolpark.destroy.client.DestroyLang;
import com.petrolpark.destroy.util.DestroyReloadListener;
import net.createmod.catnip.lang.Lang;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;

public class ExplosiveProperties extends EnumMap<ExplosiveProperties.ExplosiveProperty, ExplosiveProperties.ExplosivePropertiesEntry> {

    public static final Map<Item, ExplosiveProperties> ITEM_EXPLOSIVE_PROPERTIES = new HashMap<>();
    public static final Map<ResourceLocation, ExplosivePropertyCondition> EXPLOSIVE_PROPERTY_CONDITIONS = new HashMap<>();

    public static final ExplosivePropertyCondition

    CAN_EXPLODE = register(new ExplosivePropertyCondition(ExplosiveProperty.SENSITIVITY, 0f, Destroy.asResource("can_explode"))),
    DROPS_EXPERIENCE = register(new ExplosivePropertyCondition(ExplosiveProperty.TEMPERATURE, -4f, Destroy.asResource("drops_experience"))),
    DROPS_HEADS = register(new ExplosivePropertyCondition(ExplosiveProperty.TEMPERATURE, -8f, Destroy.asResource("drops_heads"))),
    ENTITIES_PUSHED = register(new ExplosivePropertyCondition(ExplosiveProperty.ENERGY, 7f, Destroy.asResource("entities_pushed"))),
    EXPLODES_RANDOMLY = register(new ExplosivePropertyCondition(ExplosiveProperty.SENSITIVITY, 10f, Destroy.asResource("explodes_randomly"))),
    ITEMS_DESTROYED = register(new ExplosivePropertyCondition(ExplosiveProperty.BRISANCE, 8f, Destroy.asResource("items_destroyed"))),
    EVAPORATES_FLUIDS = register(new ExplosivePropertyCondition(ExplosiveProperty.TEMPERATURE, 5f, Destroy.asResource("evaporates_fluids"))),
    OBLITERATES = register(new ExplosivePropertyCondition(ExplosiveProperty.BRISANCE, 5f, Destroy.asResource("obliterates"))),
    NO_FUSE = register(new ExplosivePropertyCondition(ExplosiveProperty.SENSITIVITY, 4f, Destroy.asResource("no_fuse"))),
    SILK_TOUCH = register(new ExplosivePropertyCondition(ExplosiveProperty.BRISANCE, -5f, Destroy.asResource("silk_touch"))),
    SOUND_ACTIVATED = register(new ExplosivePropertyCondition(ExplosiveProperty.SENSITIVITY, 8f, Destroy.asResource("sound_activated"))),
    UNDERWATER = register(new ExplosivePropertyCondition(ExplosiveProperty.OXYGEN_BALANCE, 2f, Destroy.asResource("underwater")));
  
    public ExplosiveProperties() {
        super(Arrays.stream(ExplosiveProperty.values()).collect(Collectors.toMap(p -> p, p -> new ExplosivePropertiesEntry(0f, p.getDefaultDescription()))));
    };

    public ExplosiveProperties withConditions(ExplosivePropertyCondition ...conditions) {
        forEach((p, e) -> e.conditions.clear());
        for (ExplosivePropertyCondition condition : conditions) {
            get(condition.property).conditions.add(condition);
        };
        return this;
    };

    public boolean fulfils(ExplosivePropertyCondition condition) {
        if (!hasCondition(condition)) return false;
        float value = get(condition.property).value;
        return condition.negative() ? value <= condition.threshhold : value >= condition.threshhold;
    };

    public boolean hasCondition(ExplosivePropertyCondition condition) {
        return get(condition.property).conditions.contains(condition);
    };

    public List<ExplosivePropertyCondition> getConditions() {
        List<ExplosivePropertyCondition> conditions = new ArrayList<>();
        for (ExplosivePropertiesEntry entry : values()) conditions.addAll(entry.conditions);
        return conditions;
    };

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        forEach((p, e) -> {
            if (e.value != 0f) tag.putFloat(p.name(), e.value);
        });
        return tag;
    };

    public static ExplosiveProperties read(CompoundTag tag) {
        ExplosiveProperties properties = new ExplosiveProperties();
        properties.forEach((p, e) -> e.value = tag.getFloat(p.name()));
        return properties;
    };

    public static ExplosiveProperties fromJson(JsonObject json) {
        ExplosiveProperties properties = new ExplosiveProperties();
        for (ExplosiveProperty property : ExplosiveProperty.values()) {
            if (json.has(Lang.asId(property.name()))) {
                try { properties.put(property, new ExplosivePropertiesEntry(json.get(Lang.asId(property.name())).getAsFloat(), property.getDefaultDescription())); } catch (Throwable e) {};
            };
        };
        return properties;
    };

    public static ExplosiveProperties read(FriendlyByteBuf buffer) {
        ExplosiveProperties properties = new ExplosiveProperties();
        for (ExplosiveProperty property : ExplosiveProperty.values()) properties.get(property).value = buffer.readFloat();
        return properties.withConditions(buffer.readCollection(ArrayList<ExplosivePropertyCondition>::new, b -> EXPLOSIVE_PROPERTY_CONDITIONS.get(b.readResourceLocation())).toArray(i -> new ExplosivePropertyCondition[i]));
    };

    public void write(FriendlyByteBuf buffer) {
        for (ExplosivePropertiesEntry entry : values()) buffer.writeFloat(entry.value);
        buffer.writeCollection(getConditions(), (b, c) -> b.writeResourceLocation(c.rl));
    };

    public static class ExplosivePropertiesEntry {

        public float value;
        public final Set<ExplosivePropertyCondition> conditions;
        public Component description;

        public ExplosivePropertiesEntry(float value, Component description) {
            this.value = value;
            this.conditions = new HashSet<>();
            this.description = description;
        };

    };

    public static class ExplosivePropertyCondition implements ExplosivePropertiesTooltip.Selectable {

        public final ExplosiveProperty property;
        public final float threshhold;
        public final ResourceLocation rl;

        public ExplosivePropertyCondition(ExplosiveProperty property, float threshhold, ResourceLocation rl) {
            this.property = property;
            this.threshhold = threshhold;
            this.rl = rl;
        };

        public boolean negative() {
            return threshhold < 0f;
        };

        public Component getDescription() {
            return Component.translatable(rl.getNamespace()+".explosion_condition."+rl.getPath());
        }

        @Override
        public List<Component> getTooltip(ExplosiveProperties properties) {
            boolean active = properties.fulfils(this);
            List<Component> tooltip = new ArrayList<>(2);
            tooltip.add(getDescription().copy().withStyle(active ? ChatFormatting.WHITE : ChatFormatting.GRAY));
            tooltip.add(DestroyLang.translate("tooltip.explosion_condition_active", DestroyLang.tickOrCross(active)).style(ChatFormatting.GRAY).component());
            return tooltip;
        };
    };

    public static ExplosivePropertyCondition register(ExplosivePropertyCondition condition) {
        EXPLOSIVE_PROPERTY_CONDITIONS.put(condition.rl, condition);
        return condition;
    };

    public static enum ExplosiveProperty implements ExplosivePropertiesTooltip.Selectable {

        ENERGY,
        OXYGEN_BALANCE,
        TEMPERATURE,
        BRISANCE,
        SENSITIVITY;

        public Component getName() {
            return DestroyLang.translate("explosive_property."+Lang.asId(name())).component();
        };

        public Component getSymbol() {
            return DestroyLang.translate("explosive_property."+Lang.asId(name())+".symbol").component();
        };

        public Component getDefaultDescription() {
            return Component.translatable(getDescriptionTranslationKey());
        };

        public String getDescriptionTranslationKey() {
            return "destroy.explosive_property."+Lang.asId(name())+".description";
        };

        @Override
        public List<Component> getTooltip(ExplosiveProperties properties) {
            List<Component> tooltip = new ArrayList<>(2);
            tooltip.add(getName());
            tooltip.add(properties.get(this).description.copy().withStyle(ChatFormatting.GRAY));
            return tooltip;
        };
    };

    public static class Listener extends DestroyReloadListener {

        public final IContext context;

        public Listener(IContext context) {
            super();
            this.context = context;
        };

        @Override
        public String getPath() {
            return "destroy_compat/explosive_items";
        };

        @Override
        public void beforeReload() {
            ITEM_EXPLOSIVE_PROPERTIES.clear();
            super.beforeReload();
        };

        @Override
        @SuppressWarnings("deprecation")
        public void forEachNameSpaceJsonFile(JsonObject jsonObject) {
            for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                JsonObject object = entry.getValue().getAsJsonObject();
                if (!CraftingHelper.processConditions(object, "conditions", this.context)) return;

                Optional<? extends Holder<Item>> itemOptional = BuiltInRegistries.ITEM.asLookup().get(ResourceKey.create(Registries.ITEM, new ResourceLocation(entry.getKey())));
                if (itemOptional.isEmpty()) throw new IllegalStateException("Invalid item ID: "+entry.getKey());
                ITEM_EXPLOSIVE_PROPERTIES.put(itemOptional.get().value(), fromJson(object));
            };
        };

    };
};
