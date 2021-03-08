package com.simibubi.create;

import com.simibubi.create.content.logistics.block.inventories.AdjustableCrateContainer;
import com.simibubi.create.content.logistics.block.inventories.AdjustableCrateScreen;
import com.simibubi.create.content.logistics.item.filter.AttributeFilterContainer;
import com.simibubi.create.content.logistics.item.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.item.filter.FilterContainer;
import com.simibubi.create.content.logistics.item.filter.FilterScreen;
import com.simibubi.create.content.schematics.block.SchematicTableContainer;
import com.simibubi.create.content.schematics.block.SchematicTableScreen;
import com.simibubi.create.content.schematics.block.SchematicannonContainer;
import com.simibubi.create.content.schematics.block.SchematicannonScreen;
import com.simibubi.create.foundation.utility.Lang;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.gui.screen.ingame.HandledScreens.Provider;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType.Factory;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.IContainerFactory;

public enum AllContainerTypes {

	SCHEMATIC_TABLE(SchematicTableContainer::new),
	SCHEMATICANNON(SchematicannonContainer::new),
	FLEXCRATE(AdjustableCrateContainer::new),
	FILTER(FilterContainer::new),
	ATTRIBUTE_FILTER(AttributeFilterContainer::new),

	;

	public ScreenHandlerType<? extends ScreenHandler> type;
	private Factory<?> factory;

	private <C extends ScreenHandler> AllContainerTypes(IContainerFactory<C> factory) {
		this.factory = factory;
	}

	public static void register(RegistryEvent.Register<ScreenHandlerType<?>> event) {
		for (AllContainerTypes container : values()) {
			container.type = new ScreenHandlerType<>(container.factory)
					.setRegistryName(new Identifier(Create.ID, Lang.asId(container.name())));
			event.getRegistry().register(container.type);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void registerScreenFactories() {
		bind(SCHEMATIC_TABLE, SchematicTableScreen::new);
		bind(SCHEMATICANNON, SchematicannonScreen::new);
		bind(FLEXCRATE, AdjustableCrateScreen::new);
		bind(FILTER, FilterScreen::new);
		bind(ATTRIBUTE_FILTER, AttributeFilterScreen::new);
	}

	@Environment(EnvType.CLIENT)
	@SuppressWarnings("unchecked")
	private static <C extends ScreenHandler, S extends Screen & ScreenHandlerProvider<C>> void bind(AllContainerTypes c,
			Provider<C, S> factory) {
		HandledScreens.register((ScreenHandlerType<C>) c.type, factory);
	}

}
