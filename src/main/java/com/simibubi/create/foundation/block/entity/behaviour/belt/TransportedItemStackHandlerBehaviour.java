package com.simibubi.create.foundation.block.entity.behaviour.belt;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class TransportedItemStackHandlerBehaviour extends BlockEntityBehaviour {

	public static BehaviourType<TransportedItemStackHandlerBehaviour> TYPE = new BehaviourType<>();
	private final ProcessingCallback processingCallback;
	private PositionGetter positionGetter;

	public TransportedItemStackHandlerBehaviour(SmartBlockEntity te, ProcessingCallback processingCallback) {
		super(te);
		this.processingCallback = processingCallback;
		positionGetter = t -> VecHelper.getCenterOf(te.getPos());
	}

	public TransportedItemStackHandlerBehaviour withStackPlacement(PositionGetter function) {
		this.positionGetter = function;
		return this;
	}

	public void handleProcessingOnAllItems(Function<TransportedItemStack, TransportedResult> processFunction) {
		handleCenteredProcessingOnAllItems(.51f, processFunction);
	}

	public void handleProcessingOnItem(TransportedItemStack item, TransportedResult processOutput) {
		handleCenteredProcessingOnAllItems(.51f, t -> {
			if (t == item)
				return processOutput;
			return null;
		});
	}

	public void handleCenteredProcessingOnAllItems(float maxDistanceFromCenter,
												   Function<TransportedItemStack, TransportedResult> processFunction) {
		this.processingCallback.applyToAllItems(maxDistanceFromCenter, processFunction);
	}

	public Vec3d getWorldPositionOf(TransportedItemStack transported) {
		return positionGetter.getWorldPositionVector(transported);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	@FunctionalInterface
	public interface ProcessingCallback {
		void applyToAllItems(float maxDistanceFromCenter,
							 Function<TransportedItemStack, TransportedResult> processFunction);
	}

	@FunctionalInterface
	public interface PositionGetter {
		Vec3d getWorldPositionVector(TransportedItemStack transported);
	}

	public static class TransportedResult {
		private static final TransportedResult DO_NOTHING = new TransportedResult(null, null);
		private static final TransportedResult REMOVE_ITEM = new TransportedResult(ImmutableList.of(), null);
		List<TransportedItemStack> outputs;
		TransportedItemStack heldOutput;

		private TransportedResult(List<TransportedItemStack> outputs, TransportedItemStack heldOutput) {
			this.outputs = outputs;
			this.heldOutput = heldOutput;
		}

		public static TransportedResult doNothing() {
			return DO_NOTHING;
		}

		public static TransportedResult removeItem() {
			return REMOVE_ITEM;
		}

		public static TransportedResult convertTo(TransportedItemStack output) {
			return new TransportedResult(ImmutableList.of(output), null);
		}

		public static TransportedResult convertTo(List<TransportedItemStack> outputs) {
			return new TransportedResult(outputs, null);
		}

		public static TransportedResult convertToAndLeaveHeld(List<TransportedItemStack> outputs,
															  TransportedItemStack heldOutput) {
			return new TransportedResult(outputs, heldOutput);
		}

		public boolean doesNothing() {
			return outputs == null;
		}

		public boolean didntChangeFrom(ItemStack stackBefore) {
			return doesNothing()
				|| outputs.size() == 1 && !outputs.get(0).stack.equals(stackBefore) && !hasHeldOutput();
		}

		public List<TransportedItemStack> getOutputs() {
			if (outputs == null)
				throw new IllegalStateException("Do not call getOutputs() on a Result that doesNothing().");
			return outputs;
		}

		public boolean hasHeldOutput() {
			return heldOutput != null;
		}

		@Nullable
		public TransportedItemStack getHeldOutput() {
			if (heldOutput == null)
				throw new IllegalStateException(
					"Do not call getHeldOutput() on a Result with hasHeldOutput() == false.");
			return heldOutput;
		}

	}

}
