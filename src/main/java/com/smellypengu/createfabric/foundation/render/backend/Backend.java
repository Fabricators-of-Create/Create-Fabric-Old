package com.smellypengu.createfabric.foundation.render.backend;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.smellypengu.createfabric.foundation.render.backend.gl.shader.*;
import com.smellypengu.createfabric.foundation.render.backend.gl.versioned.GlFeatureCompat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureUtil;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

public class Backend {
    public static final Logger log = LogManager.getLogger(Backend.class);
    public static final FloatBuffer MATRIX_BUFFER = MemoryUtil.memAllocFloat(16);

    private static final Map<Identifier, ProgramSpec<?>> registry = new HashMap<>();
    private static final Map<ProgramSpec<?>, GlProgram> programs = new HashMap<>();

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
        return (P) programs.get(spec);
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

    public static void init() {
        // Can be null when running datagenerators due to the unfortunate time we call this
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) return;

        ResourceManager manager = mc.getResourceManager();

        if (manager instanceof ReloadableResourceManager) {
            /*ResourceReloadListener listener = Backend::onResourceManagerReload;
            ((ReloadableResourceManager) manager).registerListener(listener);*/
        }
    }

    private static void onResourceManagerReload(ResourceManager manager, Predicate<ResourceType> predicate) {
        /*if (predicate.test(ResourceType.SHADERS)) {
            capabilities = GL.createCapabilities();
            compat = new GlFeatureCompat(capabilities);

            //OptifineHandler.refresh(); TODO OPTIFINE THING
            refresh();

            if (gl20()) {

                programs.values().forEach(GlProgram::delete);
                programs.clear();
                for (ProgramSpec<?> shader : registry.values()) {
                    loadProgram(manager, shader);
                }
            }
        }*/
    }

    public static void refresh() {
        enabled = true; //TODO CONFIG THING PLS HELP AllConfigs.CLIENT.experimentalRendering.get() && !OptifineHandler.usingShaders();
    }

    private static <P extends GlProgram, S extends ProgramSpec<P>> void loadProgram(ResourceManager manager, S programSpec) {
        GlShader vert = null;
        GlShader frag = null;
        try {
            vert = loadShader(manager, programSpec.getVert(), ShaderType.VERTEX, programSpec.defines);
            frag = loadShader(manager, programSpec.getFrag(), ShaderType.FRAGMENT, programSpec.defines);

            GlProgram.Builder builder = GlProgram.builder(programSpec.name).attachShader(vert).attachShader(frag);

            programSpec.attributes.forEach(builder::addAttribute);

            P program = builder.build(programSpec.factory);

            programs.put(programSpec, program);

            log.info("Loaded program {}", programSpec.name);
        } catch (IOException ex) {
            log.error("Failed to load program {}", programSpec.name, ex);
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
