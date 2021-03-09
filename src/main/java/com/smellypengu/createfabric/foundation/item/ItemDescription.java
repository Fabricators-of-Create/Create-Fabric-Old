package com.smellypengu.createfabric.foundation.item;

import com.smellypengu.createfabric.AllItems;
import com.smellypengu.createfabric.content.contraptions.base.Rotating;
import com.smellypengu.createfabric.foundation.utility.Lang;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.smellypengu.createfabric.foundation.item.TooltipHelper.cutString;
import static net.minecraft.util.Formatting.*;

public class ItemDescription {

	public static final ItemDescription MISSING = new ItemDescription(null);
	public static LiteralText trim =
		new LiteralText(WHITE + "" + STRIKETHROUGH + "                          ");

	public enum Palette {

		Blue(BLUE, AQUA),
		Green(DARK_GREEN, GREEN),
		Yellow(GOLD, YELLOW),
		Red(DARK_RED, RED),
		Purple(DARK_PURPLE, LIGHT_PURPLE),
		Gray(DARK_GRAY, GRAY),

		;

		private Palette(Formatting primary, Formatting highlight) {
			color = primary;
			hColor = highlight;
		}

		public Formatting color;
		public Formatting hColor;
	}

	private List<LiteralText> lines;
	private List<LiteralText> linesOnShift;
	private List<LiteralText> linesOnCtrl;
	private Palette palette;

	public ItemDescription(Palette palette) {
		this.palette = palette;
		lines = new ArrayList<>();
		linesOnShift = new ArrayList<>();
		linesOnCtrl = new ArrayList<>();
	}

	public ItemDescription withSummary(String summary) {
		add(linesOnShift, cutString(summary, palette.color, palette.hColor));
		add(linesOnShift, "");
		return this;
	}

	public ItemDescription withKineticStats(Block block) { // TODO FIX CONFIGS

		boolean isEngine = false /**block instanceof EngineBlock*/;
		/**CKinetics config = AllConfigs.SERVER.kinetics;*/
		Rotating.SpeedLevel minimumRequiredSpeedLevel =
			isEngine ? Rotating.SpeedLevel.NONE : ((Rotating) block).getMinimumRequiredSpeedLevel();
		boolean hasSpeedRequirement = minimumRequiredSpeedLevel != Rotating.SpeedLevel.NONE;
		MutableText id = block.getName();
		/**Map<Identifier, ConfigValue<Double>> impacts = config.stressValues.getImpacts();
		Map<Identifier, ConfigValue<Double>> capacities = config.stressValues.getCapacities();*/
		/**boolean hasStressImpact = impacts.containsKey(id) && impacts.get(id)
			.get() > 0 && IRotate.StressImpact.isEnabled();
		boolean hasStressCapacity = capacities.containsKey(id) && IRotate.StressImpact.isEnabled();*/
		boolean hasGlasses = AllItems.GOGGLES.getDefaultStack().getItem() == MinecraftClient.getInstance().player.getEquippedStack(EquipmentSlot.HEAD)
				.getItem();

		String rpmUnit = Lang.translate("generic.unit.rpm");
		if (hasSpeedRequirement) {
			List<String> speedLevels = Lang.translatedOptions("tooltip.speedRequirement", "none", "medium", "high");
			int index = minimumRequiredSpeedLevel.ordinal();
			String level =
				minimumRequiredSpeedLevel.getTextColor() + makeProgressBar(3, index) + speedLevels.get(index);

			if (hasGlasses)
				level += " (" + minimumRequiredSpeedLevel.getSpeedValue() + rpmUnit + "+)";

			add(linesOnShift, GRAY + Lang.translate("tooltip.speedRequirement"));
			add(linesOnShift, level);
		}

		if (/**hasStressImpact &&*/ !(!isEngine && ((Rotating) block).hideStressImpact())) {
			List<String> stressLevels = Lang.translatedOptions("tooltip.stressImpact", "low", "medium", "high");
			double impact = /**impacts.get(id).get()*/ 0;
			Rotating.StressImpact impactId = impact >= /**config.highStressImpact.get()*/ 0 ? Rotating.StressImpact.HIGH
				: (impact >= /**config.mediumStressImpact.get()*/ 0 ? Rotating.StressImpact.MEDIUM : Rotating.StressImpact.LOW);
			int index = impactId.ordinal();
			String level = impactId.getAbsoluteColor() + makeProgressBar(3, index) + stressLevels.get(index);

			if (hasGlasses)
				level += " (" + /**impacts.get(id).get()*/ 4 + "x " + rpmUnit + ")";

			add(linesOnShift, GRAY + Lang.translate("tooltip.stressImpact"));
			add(linesOnShift, level);
		}

		if (/**hasStressCapacity*/ true) {
			List<String> stressCapacityLevels =
				Lang.translatedOptions("tooltip.capacityProvided", "low", "medium", "high");
			/**double capacity = capacities.get(id)
				.get();
			IRotate.StressImpact impactId = capacity >= config.highCapacity.get() ? IRotate.StressImpact.LOW
				: (capacity >= config.mediumCapacity.get() ? IRotate.StressImpact.MEDIUM : IRotate.StressImpact.HIGH);
			int index = IRotate.StressImpact.values().length - 2 - impactId.ordinal();*/
			String level = /**impactId.getAbsoluteColor() +*/ makeProgressBar(3, /**index*/ 4) + stressCapacityLevels.get(/**index*/ 4);

			if (hasGlasses)
				level += " (" + /**capacity*/ 4 + "x " + rpmUnit + ")";
			if (!isEngine && ((Rotating) block).showCapacityWithAnnotation())
				level +=
					" " + DARK_GRAY + Formatting.ITALIC + Lang.translate("tooltip.capacityProvided.asGenerator");

			add(linesOnShift, GRAY + Lang.translate("tooltip.capacityProvided"));
			add(linesOnShift, level);

			String genSpeed = generatorSpeed(block, rpmUnit);
			if (!genSpeed.equals("")) {
				add(linesOnShift, GREEN + " " + genSpeed);
			}
		}

		if (hasSpeedRequirement /**|| hasStressImpact || hasStressCapacity*/)
			add(linesOnShift, "");
		return this;
	}

	public static String makeProgressBar(int length, int filledLength) {
		String bar = " ";
		int emptySpaces = length - 1 - filledLength;
		for (int i = 0; i <= filledLength; i++)
			bar += "\u2588";
		for (int i = 0; i < emptySpaces; i++)
			bar += "\u2592";
		return bar + " ";
	}

	public ItemDescription withBehaviour(String condition, String behaviour) {
		add(linesOnShift, GRAY + condition);
		add(linesOnShift, cutString(behaviour, palette.color, palette.hColor, 1));
		return this;
	}

	public ItemDescription withControl(String condition, String action) {
		add(linesOnCtrl, GRAY + condition);
		add(linesOnCtrl, cutString(action, palette.color, palette.hColor, 1));
		return this;
	}

	public ItemDescription createTabs() {
		boolean hasDescription = !linesOnShift.isEmpty();
		boolean hasControls = !linesOnCtrl.isEmpty();

		if (hasDescription || hasControls) {
			String[] holdKey = Lang.translate("tooltip.holdKey", "$")
				.split("\\$");
			String[] holdKeyOrKey = Lang.translate("tooltip.holdKeyOrKey", "$", "$")
				.split("\\$");
			String keyShift = Lang.translate("tooltip.keyShift");
			String keyCtrl = Lang.translate("tooltip.keyCtrl");
			for (List<LiteralText> list : Arrays.asList(lines, linesOnShift, linesOnCtrl)) {
				boolean shift = list == linesOnShift;
				boolean ctrl = list == linesOnCtrl;

				if (holdKey.length != 2 || holdKeyOrKey.length != 3) {
					list.add(0, new LiteralText("Invalid lang formatting!"));
					continue;
				}

				StringBuilder tabBuilder = new StringBuilder();
				tabBuilder.append(DARK_GRAY);
				if (hasDescription && hasControls) {
					tabBuilder.append(holdKeyOrKey[0]);
					tabBuilder.append(shift ? palette.hColor : palette.color);
					tabBuilder.append(keyShift);
					tabBuilder.append(DARK_GRAY);
					tabBuilder.append(holdKeyOrKey[1]);
					tabBuilder.append(ctrl ? palette.hColor : palette.color);
					tabBuilder.append(keyCtrl);
					tabBuilder.append(DARK_GRAY);
					tabBuilder.append(holdKeyOrKey[2]);

				} else {
					tabBuilder.append(holdKey[0]);
					tabBuilder.append((hasDescription ? shift : ctrl) ? palette.hColor : palette.color);
					tabBuilder.append(hasDescription ? keyShift : keyCtrl);
					tabBuilder.append(DARK_GRAY);
					tabBuilder.append(holdKey[1]);
				}

				list.add(0, new LiteralText(tabBuilder.toString()));
				if (shift || ctrl)
					list.add(1, new LiteralText(""));
			}
		}

		if (!hasDescription)
			linesOnShift = lines;
		if (!hasControls)
			linesOnCtrl = lines;

		return this;
	}

	public static String hightlight(String s, Palette palette) {
		return palette.hColor + s + palette.color;
	}

	public static void add(List<LiteralText> infoList, List<String> textLines) {
		textLines.forEach(s -> add(infoList, s));
	}

	public static void add(List<LiteralText> infoList, String line) {
		infoList.add(new LiteralText(line));
	}

	public Palette getPalette() {
		return palette;
	}

	public List<LiteralText> addInformation(List<LiteralText> tooltip) {
		if (Screen.hasShiftDown()) {
			tooltip.addAll(linesOnShift);
			return tooltip;
		}

		if (Screen.hasControlDown()) {
			tooltip.addAll(linesOnCtrl);
			return tooltip;
		}

		tooltip.addAll(lines);
		return tooltip;
	}

	public List<LiteralText> getLines() {
		return lines;
	}

	public List<LiteralText> getLinesOnCtrl() {
		return linesOnCtrl;
	}

	public List<LiteralText> getLinesOnShift() {
		return linesOnShift;
	}

	private String generatorSpeed(Block block, String unitRPM) {
		String value = "";

		/**if (block instanceof WaterWheelBlock) { TODO WaterWheelBlock EncasedFanBlock FurnaceEngineBlock CHECK
			int baseSpeed = AllConfigs.SERVER.kinetics.waterWheelBaseSpeed.get();
			int speedmod = AllConfigs.SERVER.kinetics.waterWheelFlowSpeed.get();
			value = (speedmod + baseSpeed) + "-" + (baseSpeed + (speedmod * 3));
		}

		else if (block instanceof EncasedFanBlock)
			value = AllConfigs.SERVER.kinetics.generatingFanSpeed.get()
				.toString();

		else if (block instanceof FurnaceEngineBlock) {
			int baseSpeed = AllConfigs.SERVER.kinetics.furnaceEngineSpeed.get();
			value = baseSpeed + "-" + (baseSpeed * 2);
		}*/

		return !value.equals("") ? Lang.translate("tooltip.generationSpeed", value, unitRPM) : "";
	}

}
