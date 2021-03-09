package com.smellypengu.registrate.util;

import com.smellypengu.registrate.util.nullness.NonNullSupplier;
import com.smellypengu.registrate.util.nullness.NonnullType;
import net.minecraft.util.Lazy;

public class NonNullLazyValue<T> extends Lazy<T> implements NonNullSupplier<T> {

	public NonNullLazyValue(NonNullSupplier<T> supplier) {
		super(supplier);
	}

	@Override
	public @NonnullType T get() {
		return get();
	}
}
