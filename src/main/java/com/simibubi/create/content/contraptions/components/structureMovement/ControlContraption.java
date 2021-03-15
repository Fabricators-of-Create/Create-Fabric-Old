package com.simibubi.create.content.contraptions.components.structureMovement;

import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.NamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.util.math.BlockPos;

public interface ControlContraption {
	boolean isAttachedTo(AbstractContraptionEntity contraption);

	void attach(ControlledContraptionEntity contraption);

	void onStall();

	boolean isValid();

	void collided();

	BlockPos getBlockPosition();

	enum MovementMode implements NamedIconOptions {

		MOVE_PLACE(AllIcons.I_MOVE_PLACE),
		MOVE_PLACE_RETURNED(AllIcons.I_MOVE_PLACE_RETURNED),
		MOVE_NEVER_PLACE(AllIcons.I_MOVE_NEVER_PLACE),

		;

		private final String translationKey;
		private final AllIcons icon;

		MovementMode(AllIcons icon) {
			this.icon = icon;
			translationKey = "contraptions.movement_mode." + Lang.asId(name());
		}

		@Override
		public AllIcons getIcon() {
			return icon;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}

	}

	enum RotationMode implements NamedIconOptions {

		ROTATE_PLACE(AllIcons.I_ROTATE_PLACE),
		ROTATE_PLACE_RETURNED(AllIcons.I_ROTATE_PLACE_RETURNED),
		ROTATE_NEVER_PLACE(AllIcons.I_ROTATE_NEVER_PLACE),

		;

		private final String translationKey;
		private final AllIcons icon;

		RotationMode(AllIcons icon) {
			this.icon = icon;
			translationKey = "contraptions.movement_mode." + Lang.asId(name());
		}

		@Override
		public AllIcons getIcon() {
			return icon;
		}

		@Override
		public String getTranslationKey() {
			return translationKey;
		}

	}

}
