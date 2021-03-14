package com.simibubi.create.foundation.advancement;

import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.simibubi.create.registrate.util.nullness.MethodsReturnNonnullByDefault;
import com.simibubi.create.registrate.util.nullness.ParametersAreNonnullByDefault;

import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SimpleTrigger extends CriterionTriggerBase<SimpleTrigger.Instance> implements ITriggerable {

	public SimpleTrigger(String id) {
		super(id);
	}

	@Override
	public Instance conditionsFromJson(JsonObject json, AdvancementEntityPredicateDeserializer context) {
		return new Instance(getId());
	}

	public void trigger(ServerPlayerEntity player){
		super.trigger(player, null);
	}
	
	public Instance instance() {
		return new Instance(getId());
	}

	public static class Instance extends CriterionTriggerBase.Instance {

		public Instance(Identifier idIn) {
			super(idIn, EntityPredicate.Extended.EMPTY); // FIXME: Is this right?
		}

		@Override
		protected boolean test(@Nullable List<Supplier<Object>> suppliers) {
			return true;
		}
	}
}
