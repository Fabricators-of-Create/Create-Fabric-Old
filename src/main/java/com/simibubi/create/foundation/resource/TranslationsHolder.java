package com.simibubi.create.foundation.resource;

import com.simibubi.create.foundation.resource.translation.CreateTranslation;
import com.simibubi.create.foundation.resource.translation.EnUsTranslation;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.Create.ID;
import static com.simibubi.create.Create.id;

@SuppressWarnings("SameParameterValue")
public final class TranslationsHolder {
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(id(ID).toString());

	/**
	 * Handles translating Create to other languages.
	 *
	 * <p>
	 * Translations should be registered
	 * in the static initializer and are processed when {@link #initialize()} is called.
	 * </p>
	 */
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
