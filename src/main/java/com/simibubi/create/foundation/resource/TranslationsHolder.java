package com.simibubi.create.foundation.resource;

import com.simibubi.create.foundation.resource.translation.CreateTranslation;
import com.simibubi.create.foundation.resource.translation.EnUsTranslation;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.Create.ID;
import static com.simibubi.create.Create.id;

/**
 * Handles translating Create to other languages.
 * Only Minecraft-supported languages are allowed.
 *
 * <p>
 * All translations extend (or are instances of) {@link CreateTranslation}, which
 * supplies an initialization method. Translations should only translate the configuration
 * screen for now.
 * </p>
 *
 * <p>
 * Instances of {@link CreateTranslation} should be registered in the static
 * initializer and added to {@link #TRANSLATIONS}. These will then be used when
 * ARRP loads our runtime resource pack.
 * </p>
 *
 * @author YTG1234
 */
@SuppressWarnings("SameParameterValue")
public final class TranslationsHolder {
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(id(ID).toString());

	private static final List<CreateTranslation> TRANSLATIONS = new ArrayList<>();

	static {
		TRANSLATIONS.add(EnUsTranslation.INSTANCE);
		// Add more languages here
	}

	private static void addResources(RuntimeResourcePack pack) {
		TRANSLATIONS.forEach(t -> t.register(pack));
	}

	public static void initialize() {
		addResources(RESOURCE_PACK);

		RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
	}
}
