package com.simibubi.create.foundation.resource;

import com.simibubi.create.foundation.resource.translation.TranslationHandler;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;

import static com.simibubi.create.Create.ID;
import static com.simibubi.create.Create.id;

@SuppressWarnings("SameParameterValue")
public final class AllClientResources { // Data packs in another class
	public static final RuntimeResourcePack RESOURCE_PACK = RuntimeResourcePack.create(id(ID).toString());

	private static void addResources(RuntimeResourcePack pack) {
		TranslationHandler.addLang(pack);
	}

	public static void initialize() {
		addResources(RESOURCE_PACK);

		RRPCallback.EVENT.register(a -> a.add(RESOURCE_PACK));
	}
}
