package com.simibubi.create.foundation.utility.extensions;

import com.simibubi.create.foundation.utility.MixinHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Contract;

public final class Matrix4fUtils {
    @Contract(value = "_,_->_", mutates = "param1")
    public static void multiplyBackward(Matrix4f $this, Matrix4f other) {
        Matrix4f copy = other.copy();
        copy.multiply($this); // Uno reverse card
        get($this).setFrom(copy);
    }

    public static Matrix4f fromFloatArray(float[] values) {
        Matrix4f matrix = new Matrix4f();
        Matrix4fExtensions ext = get(matrix);

        ext.fromFloatArray(values);

        return matrix;
    }

    private static Matrix4fExtensions get(Matrix4f m) {
        return MixinHelper.cast(m);
    }

    private Matrix4fUtils() {}
}
