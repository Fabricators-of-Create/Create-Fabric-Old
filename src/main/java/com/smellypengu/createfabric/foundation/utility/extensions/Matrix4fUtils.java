package com.smellypengu.createfabric.foundation.utility.extensions;

import com.smellypengu.createfabric.foundation.utility.MixinHelper;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.Contract;

public final class Matrix4fUtils {
    @Contract(value = "_,_->_", mutates = "param1")
    public static void multiplyBackward(Matrix4f $this, Matrix4f other) {
        Matrix4f copy = other.copy();
        copy.multiply($this); // Uno reverse card
        MixinHelper.<Matrix4fExtensions>cast($this).setFrom(copy);
    }

    private Matrix4fUtils() {}
}
