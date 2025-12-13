package com.petrolpark.destroy.compat.vs2;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

public class DestroyVSUtil {
    public static AABB AABBtoWorld(Level level, Vec3 pos, AABB box) {
        if (!(level instanceof ClientLevel clientLevel)) {
            return box;
        }

        return VSGameUtilsKt.transformRenderAABBToWorld(clientLevel, pos, box);
    }

}
