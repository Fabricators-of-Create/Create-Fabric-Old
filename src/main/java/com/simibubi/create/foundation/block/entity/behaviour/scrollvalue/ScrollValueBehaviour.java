package com.simibubi.create.foundation.block.entity.behaviour.scrollvalue;

import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.BehaviourType;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.networking.AllPackets;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;
import java.util.function.Function;

public class ScrollValueBehaviour extends BlockEntityBehaviour {

	public static BehaviourType<ScrollValueBehaviour> TYPE = new BehaviourType<>();
	public int value;
	public int scrollableValue;
	ValueBoxTransform slotPositioning;
	Vec3d textShift;
	int min = 0;
	int max = 1;
	int ticksUntilScrollPacket;
	boolean forceClientState;
	Text label;
	Consumer<Integer> callback;
	Consumer<Integer> clientCallback;
	Function<Integer, String> formatter;
	Function<Integer, Text> unit;
	Function<StepContext, Integer> step;
	boolean needsWrench;

	public ScrollValueBehaviour(Text label, SmartBlockEntity te, ValueBoxTransform slot) {
		super(te);
		this.setLabel(label);
		slotPositioning = slot;
		callback = i -> {
		};
		clientCallback = i -> {
		};
		textShift = Vec3d.ZERO;
		formatter = i -> Integer.toString(i);
		step = (c) -> 1;
		value = 0;
		ticksUntilScrollPacket = -1;
	}

	@Override
	public void write(CompoundTag nbt, boolean clientPacket) {
		nbt.putInt("ScrollValue", value);
		if (clientPacket && forceClientState) {
			nbt.putBoolean("ForceScrollable", true);
			forceClientState = false;
		}
		super.write(nbt, clientPacket);
	}

	@Override
	public void read(CompoundTag nbt, boolean clientPacket) {
		value = nbt.getInt("ScrollValue");
		if (nbt.contains("ForceScrollable")) {
			ticksUntilScrollPacket = -1;
			scrollableValue = value;
		}
		super.read(nbt, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();

		if (!getWorld().isClient)
			return;
		if (ticksUntilScrollPacket == -1)
			return;
		if (ticksUntilScrollPacket > 0) {
			ticksUntilScrollPacket--;
			return;
		}

		AllPackets.CHANNEL.sendToServer(new ScrollValueUpdatePacket(getPos(), scrollableValue));
		ticksUntilScrollPacket = -1;
	}

	public ScrollValueBehaviour withClientCallback(Consumer<Integer> valueCallback) {
		clientCallback = valueCallback;
		return this;
	}

	public ScrollValueBehaviour withCallback(Consumer<Integer> valueCallback) {
		callback = valueCallback;
		return this;
	}

	public ScrollValueBehaviour between(int min, int max) {
		this.min = min;
		this.max = max;
		return this;
	}

	public ScrollValueBehaviour moveText(Vec3d shift) {
		textShift = shift;
		return this;
	}

	public ScrollValueBehaviour requiresWrench() {
		this.needsWrench = true;
		return this;
	}

	public ScrollValueBehaviour withFormatter(Function<Integer, String> formatter) {
		this.formatter = formatter;
		return this;
	}

	public ScrollValueBehaviour withUnit(Function<Integer, Text> unit) {
		this.unit = unit;
		return this;
	}

	public ScrollValueBehaviour withStepFunction(Function<StepContext, Integer> step) {
		this.step = step;
		return this;
	}

	@Override
	public void initialize() {
		super.initialize();
		setValue(value);
		scrollableValue = value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		value = MathHelper.clamp(value, min, max);
		if (value == this.value)
			return;
		this.value = value;
		forceClientState = true;
		callback.accept(value);
		blockEntity.markDirty();
		blockEntity.sendData();
		scrollableValue = value;
	}

	public String formatValue() {
		return formatter.apply(scrollableValue);
	}

	@Override
	public BehaviourType<?> getType() {
		return TYPE;
	}

	public boolean testHit(Vec3d hit) {
		BlockState state = blockEntity.getCachedState();
		Vec3d localHit = hit.subtract(new Vec3d(blockEntity.getPos().getX(), blockEntity.getPos().getY(), blockEntity.getPos().getZ()));
		return slotPositioning.testHit(state, localHit);
	}

	public void setLabel(Text label) {
		this.label = label;
	}

	public static class StepContext {
		public int currentValue;
		public boolean forward;
		public boolean shift;
		public boolean control;
	}

}
