package com.simibubi.create.foundation.advancement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.simibubi.create.Create;
import com.simibubi.create.registrate.util.nullness.MethodsReturnNonnullByDefault;
import com.simibubi.create.registrate.util.nullness.ParametersAreNonnullByDefault;

import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class CriterionTriggerBase<T extends CriterionTriggerBase.Instance> implements Criterion<T> {

	public CriterionTriggerBase(String id) {
		this.ID = new Identifier(Create.ID, id);
	}

	private final Identifier ID;
	protected final Map<PlayerAdvancementTracker, Set<ConditionsContainer<T>>> listeners = Maps.newHashMap();

	@Override
	public void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementsIn, ConditionsContainer<T> listener) {
		Set<ConditionsContainer<T>> playerListeners = this.listeners.computeIfAbsent(playerAdvancementsIn, k -> new HashSet<>());

		playerListeners.add(listener);
	}

	@Override
	public void endTrackingCondition(PlayerAdvancementTracker playerAdvancementsIn, ConditionsContainer<T> listener) {
		Set<ConditionsContainer<T>> playerListeners = this.listeners.get(playerAdvancementsIn);
		if (playerListeners != null){
			playerListeners.remove(listener);
			if (playerListeners.isEmpty()){
				this.listeners.remove(playerAdvancementsIn);
			}
		}
	}

	@Override
	public void endTracking(PlayerAdvancementTracker playerAdvancementsIn) {
		this.listeners.remove(playerAdvancementsIn);
	}

	@Override
	public Identifier getId() {
		return ID;
	}

	protected void trigger(ServerPlayerEntity player, @Nullable List<Supplier<Object>> suppliers){
		PlayerAdvancementTracker playerAdvancements = player.getAdvancementTracker();
		Set<ConditionsContainer<T>> playerListeners = this.listeners.get(playerAdvancements);
		if (playerListeners != null){
			List<ConditionsContainer<T>> list = new LinkedList<>();

			for (ConditionsContainer<T> listener :
					playerListeners) {
				if (listener.getConditions().test(suppliers)) {
					list.add(listener);
				}
			}

			list.forEach(listener -> listener.grant(playerAdvancements));

		}
	}

	public abstract static class Instance extends AbstractCriterionConditions {

		public Instance(Identifier idIn, EntityPredicate.Extended p_i231464_2_) {
			super(idIn, p_i231464_2_);
		}

		protected abstract boolean test(@Nullable List<Supplier<Object>> suppliers);
	}


}
