package com.simibubi.create.content.contraptions.relays.advanced.sequencer;

import java.util.Vector;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import com.simibubi.create.foundation.utility.NBTHelper;

public class Instruction {

	SequencerInstructions instruction;
	InstructionSpeedModifiers speedModifier;
	int value;

	public Instruction(SequencerInstructions instruction) {
		this(instruction, 1);
	}

	public Instruction(SequencerInstructions instruction, int value) {
		this.instruction = instruction;
		speedModifier = InstructionSpeedModifiers.FORWARD;
		this.value = value;
	}

	int getDuration(float initialProgress, float speed) {
		int offset = speed > 0 && speedModifier.value < 0 ? 1 : 2;
		speed *= speedModifier.value;
		speed = Math.abs(speed);

		double degreesPerTick = (speed * 360) / 60 / 20;
		double metersPerTick = speed / 512;
		switch (instruction) {

		case TURN_ANGLE:
			return (int) ((1 - initialProgress) * value / degreesPerTick + 1);

		case TURN_DISTANCE:
			return (int) ((1 - initialProgress) * value / metersPerTick + offset);

		case DELAY:
			return (int) ((1 - initialProgress) * value + 1);

		case AWAIT:
			return -1;

		case END:
		default:
			break;

		}
		return 0;
	}

	int getSpeedModifier() {
		switch (instruction) {

		case TURN_ANGLE:
		case TURN_DISTANCE:
			return speedModifier.value;

		case END:
		case DELAY:
		case AWAIT:
		default:
			break;

		}
		return 0;
	}

	OnIsPoweredResult onRedstonePulse() {
		return instruction == SequencerInstructions.AWAIT ? OnIsPoweredResult.CONTINUE : OnIsPoweredResult.NOTHING;
	}

	public static ListTag serializeAll(Vector<Instruction> instructions) {
		ListTag list = new ListTag();
		instructions.forEach(i -> list.add(i.serialize()));
		return list;
	}

	public static Vector<Instruction> deserializeAll(ListTag list) {
		if (list.isEmpty())
			return createDefault();
		Vector<Instruction> instructions = new Vector<>(5);
		list.forEach(inbt -> instructions.add(deserialize((CompoundTag) inbt)));
		return instructions;
	}

	public static Vector<Instruction> createDefault() {
		Vector<Instruction> instructions = new Vector<>(5);
		instructions.add(new Instruction(SequencerInstructions.TURN_ANGLE, 90));
		instructions.add(new Instruction(SequencerInstructions.END));
		return instructions;
	}

	CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		NBTHelper.writeEnum(tag, "Type", instruction);
		NBTHelper.writeEnum(tag, "Modifier", speedModifier);
		tag.putInt("Value", value);
		return tag;
	}

	static Instruction deserialize(CompoundTag tag) {
		Instruction instruction = new Instruction(NBTHelper.readEnum(tag, "Type", SequencerInstructions.class));
		instruction.speedModifier = NBTHelper.readEnum(tag, "Modifier", InstructionSpeedModifiers.class);
		instruction.value = tag.getInt("Value");
		return instruction;
	}

}
