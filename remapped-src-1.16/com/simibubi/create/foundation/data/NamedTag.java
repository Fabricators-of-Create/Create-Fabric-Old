package com.simibubi.create.foundation.data;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NamedTag<T> implements Tag.Identified<T> {
	private final Identifier id;
	private final Tag<T> tag;

	public NamedTag(@Nullable Tag<T> tag, Identifier id) {
		this.tag = tag;
		this.id = id;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public boolean contains(T p_230235_1_) {
		if (tag == null)
			return false;
		return tag.contains(p_230235_1_);
	}

	@Override
	public List<T> values() {
		if (tag == null)
			return Collections.emptyList();
		return tag.values();
	}
}
