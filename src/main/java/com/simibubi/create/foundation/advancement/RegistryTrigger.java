package com.simibubi.create.foundation.advancement;

/*@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RegistryTrigger<T extends IForgeRegistryEntry<T>> extends StringSerializableTrigger<T> {
	private final IForgeRegistry<T> registry;

	public RegistryTrigger(String id, IForgeRegistry<T> registry) {
		super(id);
		this.registry = registry;
	}

	@Nullable
	@Override
	protected T getValue(String key) {
		return registry.getValue(new Identifier(key));
	}

	@Nullable
	@Override
	protected String getKey(T value) {
		Identifier key = registry.getKey(value);
		return key == null ? null : key.toString();
	}
}*/
