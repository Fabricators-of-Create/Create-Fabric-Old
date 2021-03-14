package com.simibubi.create.foundation.render.backend;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import com.simibubi.create.foundation.render.backend.gl.GlFog;
import com.simibubi.create.foundation.render.backend.gl.GlFogMode;
import com.simibubi.create.foundation.render.backend.gl.shader.GlProgram;
import com.simibubi.create.foundation.render.backend.gl.shader.GlShader;
import com.simibubi.create.foundation.render.backend.gl.shader.ProgramGroup;
import com.simibubi.create.foundation.render.backend.gl.shader.ProgramSpec;
import com.simibubi.create.foundation.render.backend.gl.shader.ShaderConstants;
import com.simibubi.create.foundation.render.backend.gl.shader.ShaderType;
import com.simibubi.create.foundation.render.backend.gl.versioned.GlFeatureCompat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class Backend {
    public static final Boolean SHADER_DEBUG_OUTPUT = false;

    public static final Logger log = LogManager.getLogger(Backend.class);
    public static final FloatBuffer MATRIX_BUFFER = MemoryUtil.memAllocFloat(16);

    private static final Map<Identifier, ProgramSpec<?>> registry = new HashMap<>();
    private static final Map<ProgramSpec<?>, ProgramGroup<?>> programs = new HashMap<>();

    private static boolean enabled;

    public static GLCapabilities capabilities;
    public static GlFeatureCompat compat;

    public Backend() {
        throw new IllegalStateException();
    }

    /**
     * Register a shader program. TODO: replace with forge registry?
     */
    public static <P extends GlProgram, S extends ProgramSpec<P>> S register(S spec) {
        Identifier name = spec.name;
        if (registry.containsKey(name)) {
            throw new IllegalStateException("Program spec '" + name + "' already registered.");
        }
        registry.put(name, spec);
        return spec;
    }

    @SuppressWarnings("unchecked")
    public static <P extends GlProgram, S extends ProgramSpec<P>> P getProgram(S spec) {
        return (P) programs.get(spec).get(GlFog.getFogMode());
    }

    public static boolean available() {
        return canUseVBOs();
    }

    public static boolean canUseInstancing() {
        return enabled &&
                compat.vertexArrayObjectsSupported() &&
                compat.drawInstancedSupported() &&
                compat.instancedArraysSupported();
    }

    public static boolean canUseVBOs() {
        return enabled && gl20();
    }

    public static boolean gl33() {
        return capabilities.OpenGL33;
    }

    public static boolean gl20() {
        return capabilities.OpenGL20;
    }

    public static void init(MinecraftClient client) {
        // Can be null when running datagenerators due to the unfortunate time we call this

        ResourceManager manager = client.getResourceManager();

		capabilities = GL.createCapabilities();
		compat = new GlFeatureCompat(capabilities);

		//OptifineHandler.refresh();
		refresh();

		if (gl20()) {

			programs.values().forEach(ProgramGroup::delete);
			programs.clear();
			for (ProgramSpec<?> shader : registry.values()) {
				loadProgram(manager, shader);
			}
		}
    }

    public static void refresh() {
        enabled = true; //TODO CONFIG AllConfigs.CLIENT.experimentalRendering.get() && !OptifineHandler.usingShaders();
    }

    private static <P extends GlProgram, S extends ProgramSpec<P>> void loadProgram(ResourceManager manager, S programSpec) {
        try {
            Map<GlFogMode, P> programGroup = new EnumMap<>(GlFogMode.class);

            for (GlFogMode fogMode : GlFogMode.values()) {
                programGroup.put(fogMode, loadProgram(manager, programSpec, fogMode));
            }

            programs.put(programSpec, new ProgramGroup<>(programGroup));

            log.info("Loaded program {}", programSpec.name);
        } catch (IOException ex) {
            log.error("Failed to load program {}", programSpec.name, ex);
            return;
        }
    }

    private static <P extends GlProgram, S extends ProgramSpec<P>> P loadProgram(ResourceManager manager, S programSpec, GlFogMode fogMode) throws IOException {
        GlShader vert = null;
        GlShader frag = null;
        try {
            ShaderConstants defines = new ShaderConstants(programSpec.defines);

            defines.defineAll(fogMode.getDefines());

            vert = loadShader(manager, programSpec.getVert(), ShaderType.VERTEX, defines);
            frag = loadShader(manager, programSpec.getFrag(), ShaderType.FRAGMENT, defines);

            GlProgram.Builder builder = GlProgram.builder(programSpec.name, fogMode).attachShader(vert).attachShader(frag);

            programSpec.attributes.forEach(builder::addAttribute);

            return builder.build(programSpec.factory);

        } finally {
            if (vert != null) vert.delete();
            if (frag != null) frag.delete();
        }
    }

    private static GlShader loadShader(ResourceManager manager, Identifier name, ShaderType type, ShaderConstants preProcessor) throws IOException {
        try (InputStream is = new BufferedInputStream(manager.getResource(name).getInputStream())) {
            String source = TextureUtil.readAllToString(is);

            if (source == null) {
                throw new IOException("Could not load program " + name);
            } else {
                return new GlShader(type, name, source, preProcessor);
            }
        }
    }
}
