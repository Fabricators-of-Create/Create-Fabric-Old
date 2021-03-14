package com.simibubi.create.foundation.mixin.accessor;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Criteria.class)
public interface CriteriaRegistryAccessor {

	@Invoker("register")
	static <T extends Criterion<?>> T register(T object) {
		throw new RuntimeException("Invoker :)");
	}
}