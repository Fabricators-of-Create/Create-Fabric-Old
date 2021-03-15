package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.Criterion;

@Mixin(Criteria.class)
public interface CriteriaRegistryAccessor {

	@Invoker("register")
	static <T extends Criterion<?>> T register(T object) {
		throw new RuntimeException("Invoker :)");
	}
}