package com.smellypengu.createfabric.foundation.render.backend.gl.attrib;

public interface IVertexAttrib {

    String attribName();

    VertexAttribSpec attribSpec();

    int getDivisor();

    int getBufferIndex();
}
