package com.simibubi.create.foundation.render.backend.gl.attrib;

import java.util.ArrayList;
import java.util.Arrays;

public class VertexFormat {

	private final ArrayList<VertexAttrib> allAttributes;

	private final int numAttributes;
	private final int stride;

	public VertexFormat(ArrayList<VertexAttrib> allAttributes) {
		this.allAttributes = allAttributes;

		int numAttributes = 0, stride = 0;
		for (VertexAttrib attrib : allAttributes) {
			VertexAttribSpec spec = attrib.attribSpec();
			numAttributes += spec.getAttributeCount();
			stride += spec.getSize();
		}
		this.numAttributes = numAttributes;
		this.stride = stride;
	}

	public static Builder builder() {
		return new Builder();
	}

	public int getShaderAttributeCount() {
		return numAttributes;
	}

	public int getStride() {
		return stride;
	}

	public void vertexAttribPointers(int index) {
		int offset = 0;
		for (VertexAttrib attrib : this.allAttributes) {
			VertexAttribSpec spec = attrib.attribSpec();
			spec.vertexAttribPointer(stride, index, offset);
			index += spec.getAttributeCount();
			offset += spec.getSize();
		}
	}

	public static class Builder {
		private final ArrayList<VertexAttrib> allAttributes;

		public Builder() {
			allAttributes = new ArrayList<>();
		}

		public <A extends Enum<A> & VertexAttrib> Builder addAttributes(Class<A> attribEnum) {
			allAttributes.addAll(Arrays.asList(attribEnum.getEnumConstants()));
			return this;
		}

		public VertexFormat build() {
			return new VertexFormat(allAttributes);
		}
	}
}
