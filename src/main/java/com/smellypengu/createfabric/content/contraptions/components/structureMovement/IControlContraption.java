package com.smellypengu.createfabric.content.contraptions.components.structureMovement;

import com.smellypengu.createfabric.foundation.gui.AllIcons;
import com.smellypengu.createfabric.foundation.block.entity.behaviour.scrollvalue.NamedIconOptions;
import com.smellypengu.createfabric.foundation.utility.Lang;
import net.minecraft.util.math.BlockPos;

public interface IControlContraption {

	public boolean isAttachedTo(AbstractContraptionEntity contraption);
	
	public void attach(ControlledContraptionEntity contraption);

	public void onStall();

	public boolean isValid();
	
	public void collided();
	
	public BlockPos getBlockPosition();

	static enum MovementMode implements NamedIconOptions {

		MOVE_PLACE(AllIcons.I_MOVE_PLACE),
		MOVE_PLACE_RETURNED(AllIcons.I_MOVE_PLACE_RETURNED),
		MOVE_NEVER_PLACE(AllIcons.I_MOVE_NEVER_PLACE),

		;

		private String translationKey;
		private AllIcons icon;

		private MovementMode(AllIcons icon) {
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

	static enum RotationMode implements NamedIconOptions {

		ROTATE_PLACE(AllIcons.I_ROTATE_PLACE),
		ROTATE_PLACE_RETURNED(AllIcons.I_ROTATE_PLACE_RETURNED),
		ROTATE_NEVER_PLACE(AllIcons.I_ROTATE_NEVER_PLACE),

		;

		private String translationKey;
		private AllIcons icon;

		private RotationMode(AllIcons icon) {
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
