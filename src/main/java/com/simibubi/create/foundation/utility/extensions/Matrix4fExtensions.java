package com.simibubi.create.foundation.utility.extensions;

import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Matrix4fExtensions {
    void create$set(@NotNull Matrix4f other);

    @ApiStatus.Internal
	@Contract(mutates = "this")
    void create$fromFloatArray(float[] floats);
}
