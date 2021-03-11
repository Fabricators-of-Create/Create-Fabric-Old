package com.simibubi.create.foundation.render.backend.gl.attrib;

public interface VertexAttrib {
	String attribName();

	VertexAttribSpec attribSpec();

	int getDivisor();

	int getBufferIndex();
}
