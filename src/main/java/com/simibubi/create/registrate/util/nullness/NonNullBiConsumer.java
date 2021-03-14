package com.simibubi.create.registrate.util.nullness;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface NonNullBiConsumer<@NonnullType T, @NonnullType U> extends BiConsumer<T, U> {

	static <T, U> NonNullBiConsumer<T, U> noop() {
		return (t, u) -> {
		};
	}

	@Override
	void accept(T t, U u);
}
