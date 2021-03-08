package com.simibubi.create.foundation.render.backend.gl.shader;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.util.Identifier;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.render.backend.gl.attrib.IVertexAttrib;

public class ProgramSpec<P extends GlProgram> {

    public final Identifier name;
    public final Identifier vert;
    public final Identifier frag;

    public final ShaderConstants defines;

    public final GlProgram.ProgramFactory<P> factory;

    public final ArrayList<IVertexAttrib> attributes;

    public static <P extends GlProgram> Builder<P> builder(String name, GlProgram.ProgramFactory<P> factory) {
        return builder(new Identifier(Create.ID, name), factory);
    }

    public static <P extends GlProgram> Builder<P> builder(Identifier name, GlProgram.ProgramFactory<P> factory) {
        return new Builder<>(name, factory);
    }

    public ProgramSpec(Identifier name, Identifier vert, Identifier frag, GlProgram.ProgramFactory<P> factory, ShaderConstants defines, ArrayList<IVertexAttrib> attributes) {
        this.name = name;
        this.vert = vert;
        this.frag = frag;
        this.defines = defines;


        this.factory = factory;
        this.attributes = attributes;
    }

    public Identifier getVert() {
        return vert;
    }

    public Identifier getFrag() {
        return frag;
    }

    public static class Builder<P extends GlProgram> {
        private Identifier vert;
        private Identifier frag;
        private ShaderConstants defines = null;

        private final Identifier name;
        private final GlProgram.ProgramFactory<P> factory;
        private final ArrayList<IVertexAttrib> attributes;

        public Builder(Identifier name, GlProgram.ProgramFactory<P> factory) {
            this.name = name;
            this.factory = factory;
            attributes = new ArrayList<>();
        }

        public Builder<P> setVert(Identifier vert) {
            this.vert = vert;
            return this;
        }

        public Builder<P> setFrag(Identifier frag) {
            this.frag = frag;
            return this;
        }

        public Builder<P> setDefines(ShaderConstants defines) {
            this.defines = defines;
            return this;
        }

        public <A extends Enum<A> & IVertexAttrib> Builder<P> addAttributes(Class<A> attributeEnum) {
            attributes.addAll(Arrays.asList(attributeEnum.getEnumConstants()));
            return this;
        }

        public ProgramSpec<P> createProgramSpec() {
            return new ProgramSpec<>(name, vert, frag, factory, defines, attributes);
        }
    }
}
