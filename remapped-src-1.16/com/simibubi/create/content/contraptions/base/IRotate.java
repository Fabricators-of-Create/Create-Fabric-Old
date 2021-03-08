package com.simibubi.create.content.contraptions.base;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.block.BlockState;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.WorldView;

public interface IRotate extends IWrenchable {

	enum SpeedLevel {
		NONE,
		MEDIUM,
		FAST;

		public Formatting getTextColor() {
			return this == NONE ? Formatting.GREEN
					: this == MEDIUM ? Formatting.AQUA : Formatting.LIGHT_PURPLE;
		}

		public int getColor() {
			return this == NONE ? 0x22FF22 : this == MEDIUM ? 0x0084FF : 0xFF55FF;
		}

		public int getParticleSpeed() {
			return this == NONE ? 10 : this == MEDIUM ? 20 : 30;
		}

		public static SpeedLevel of(float speed) {
			speed = Math.abs(speed);

			if (speed >= AllConfigs.SERVER.kinetics.fastSpeed.get()) {
				return FAST;
			} else if (speed >= AllConfigs.SERVER.kinetics.mediumSpeed.get()) {
				return MEDIUM;
			}
			return NONE;
		}

		public float getSpeedValue() {
			switch (this) {
			case FAST:
				return AllConfigs.SERVER.kinetics.fastSpeed.get().floatValue();
			case MEDIUM:
				return AllConfigs.SERVER.kinetics.mediumSpeed.get().floatValue();
			case NONE:
			default:
				return 0;
			}
		}

		public static Text getFormattedSpeedText(float speed, boolean overstressed){
			SpeedLevel speedLevel = of(speed);

			MutableText level = new LiteralText(ItemDescription.makeProgressBar(3, speedLevel.ordinal()));

			if (speedLevel == SpeedLevel.MEDIUM)
				level.append(Lang.translate("tooltip.speedRequirement.medium"));
			if (speedLevel == SpeedLevel.FAST)
				level.append(Lang.translate("tooltip.speedRequirement.high"));

			level.append(" (" + IHaveGoggleInformation.format(Math.abs(speed))).append(Lang.translate("generic.unit.rpm")).append(") ");

			if (overstressed)
				level.formatted(Formatting.DARK_GRAY, Formatting.STRIKETHROUGH);
			else
				level.formatted(speedLevel.getTextColor());

			return level;
		}

	}

	enum StressImpact {
		LOW,
		MEDIUM,
		HIGH,
		OVERSTRESSED;

		public Formatting getAbsoluteColor() {
			return this == LOW ? Formatting.YELLOW : this == MEDIUM ? Formatting.GOLD : Formatting.RED;
		}

		public Formatting getRelativeColor() {
			return this == LOW ? Formatting.GREEN : this == MEDIUM ? Formatting.YELLOW : this == HIGH ? Formatting.GOLD : Formatting.RED;
		}

		public static StressImpact of(double stressPercent){
			if (stressPercent > 1) return StressImpact.OVERSTRESSED;
			else if (stressPercent > .75d) return StressImpact.HIGH;
			else if (stressPercent > .5d) return StressImpact.MEDIUM;
			else return StressImpact.LOW;
		}
		
		public static boolean isEnabled() {
			return !AllConfigs.SERVER.kinetics.disableStress.get();
		}

		public static Text getFormattedStressText(double stressPercent){
			StressImpact stressLevel = of(stressPercent);
			Formatting color = stressLevel.getRelativeColor();

			MutableText level = new LiteralText(ItemDescription.makeProgressBar(3, Math.min(stressLevel.ordinal(), 2)));
			level.append(Lang.translate("tooltip.stressImpact." + Lang.asId(stressLevel.name())));

			level.append(String.format(" (%s%%) ", (int) (stressPercent * 100)));

			return level.formatted(color);
		}
	}

	public boolean hasShaftTowards(WorldView world, BlockPos pos, BlockState state, Direction face);
	
	public boolean hasIntegratedCogwheel(WorldView world, BlockPos pos, BlockState state);

	public Axis getRotationAxis(BlockState state);

	public default SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.NONE;
	}

	public default boolean hideStressImpact() {
		return false;
	}

	public default boolean showCapacityWithAnnotation() {
		return false;
	}

}
