package com.simibubi.create.foundation.utility;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.Tags;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class EmptyNamedTag<T> implements Tags.IOptionalNamedTag<T> {
	private final Identifier id;

	public EmptyNamedTag(Identifier id) {
		this.id = id;
	}

	@Override
	public boolean isDefaulted() {
		return false;
	}

	@Override
	public Identifier getId() {
		return id;
	}

	@Override
	public boolean contains(Object p_230235_1_) {
		return false;
	}

	@Override
	public List<T> values() {
		return Collections.emptyList()	;
	}
}
