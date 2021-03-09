package com.smellypengu.createfabric.foundation.mixin;

import com.smellypengu.createfabric.foundation.utility.MixinHelper;
import com.smellypengu.createfabric.foundation.utility.extensions.Matrix4fExtensions;
import net.minecraft.util.math.Matrix4f;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Matrix4f.class)
public abstract class Matrix4fMixin implements Matrix4fExtensions {
    @Shadow protected float a00;
    @Shadow protected float a01;
    @Shadow protected float a02;
    @Shadow protected float a03;
    @Shadow protected float a10;
    @Shadow protected float a11;
    @Shadow protected float a12;
    @Shadow protected float a13;
    @Shadow protected float a20;
    @Shadow protected float a21;
    @Shadow protected float a22;
    @Shadow protected float a23;
    @Shadow protected float a30;
    @Shadow protected float a31;
    @Shadow protected float a32;
    @Shadow protected float a33;

    @Override
    public void setFrom(@NotNull Matrix4f other) {
        Matrix4fMixin o = MixinHelper.cast(other); // This will look weird in the merged class

        a00 = o.a00;
        a01 = o.a01;
        a02 = o.a02;
        a03 = o.a03;
        a10 = o.a10;
        a11 = o.a11;
        a12 = o.a12;
        a13 = o.a13;
        a20 = o.a20;
        a21 = o.a21;
        a22 = o.a22;
        a23 = o.a23;
        a30 = o.a30;
        a31 = o.a31;
        a32 = o.a32;
        a33 = o.a33;
    }
}
