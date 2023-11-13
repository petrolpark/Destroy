package com.petrolpark.destroy.entity;

import static com.petrolpark.destroy.Destroy.REGISTRATE;

import com.petrolpark.destroy.entity.renderer.PrimedBombRenderer;
import com.tterrag.registrate.util.entry.EntityEntry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.item.PrimedTnt;

public class DestroyEntityTypes {
    
    public static final EntityEntry<PrimedBomb.Anfo> PRIMED_ANFO = bomb("primed_anfo", PrimedBomb.Anfo::new);
    public static final EntityEntry<PrimedBomb.PicricAcid> PRIMED_PICRIC_ACID = bomb("primed_picric_acid", PrimedBomb.PicricAcid::new);
    public static final EntityEntry<PrimedBomb.Cordite> PRIMED_CORDITE = bomb("primed_cordite", PrimedBomb.Cordite::new);
    public static final EntityEntry<PrimedBomb.Nitrocellulose> PRIMED_NITROCELLULOSE = bomb("primed_nitrocellulose", PrimedBomb.Nitrocellulose::new);

    private static <T extends PrimedTnt> EntityEntry<T> bomb(String name, EntityFactory<T> factory) {
        return REGISTRATE.entity(EntityType.TNT, name, factory, MobCategory.MISC)
            .renderer(() -> PrimedBombRenderer::new)
            .register();
    };

    public static final void register() {};
};
