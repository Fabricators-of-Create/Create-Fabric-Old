package com.simibubi.create.foundation.render.backend.gl;

import com.simibubi.create.foundation.render.backend.Backend;

import java.util.function.Consumer;

public class GlVertexArray extends GlObject {
	public GlVertexArray() {
		setHandle(Backend.compat.genVertexArrays());
	}

	public void bind() {
		Backend.compat.bindVertexArray(handle());
	}

	public void unbind() {
		Backend.compat.bindVertexArray(0);
	}

	public void with(Consumer<GlVertexArray> action) {
		bind();
		action.accept(this);
		unbind();
	}

	protected void deleteInternal(int handle) {
		Backend.compat.deleteVertexArrays(handle);
	}
}
