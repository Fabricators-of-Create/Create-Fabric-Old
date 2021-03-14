package com.simibubi.create.content.contraptions.components.structureMovement.mounted;

import java.util.List;

import com.simibubi.create.AllBlockEntities;
import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
import com.simibubi.create.content.contraptions.components.structureMovement.DisplayAssemblyExceptionsProvider;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.NamedIconOptions;
import com.simibubi.create.foundation.block.entity.behaviour.scrollvalue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;

public class CartAssemblerBlockEntity extends SmartBlockEntity implements DisplayAssemblyExceptionsProvider {
	private static final int assemblyCooldown = 8;

	protected ScrollOptionBehaviour<CartMovementMode> movementMode;
	private int ticksSinceMinecartUpdate;
	protected AssemblyException lastException;

	public CartAssemblerBlockEntity() {
		super(AllBlockEntities.CART_ASSEMBLER);
		ticksSinceMinecartUpdate = assemblyCooldown;
	}

	@Override
	public void tick() {
		super.tick();
		if (ticksSinceMinecartUpdate < assemblyCooldown) {
			ticksSinceMinecartUpdate++;
		}
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		movementMode = new ScrollOptionBehaviour<>(CartMovementMode.class,
			Lang.translate("contraptions.cart_movement_mode"), this, getMovementModeSlot());
		movementMode.requiresWrench();
		behaviours.add(movementMode);
	}

	@Override
	public void toTag(CompoundTag compound, boolean clientPacket) {
		AssemblyException.write(compound, lastException);
		super.toTag(compound, clientPacket);
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		lastException = AssemblyException.read(compound);
		super.fromTag(state, compound, clientPacket);
	}

	@Override
	public AssemblyException getLastAssemblyException() {
		return lastException;
	}

	protected ValueBoxTransform getMovementModeSlot() {
		return new CartAssemblerValueBoxTransform();
	}

	private class CartAssemblerValueBoxTransform extends CenteredSideValueBoxTransform {
		
		public CartAssemblerValueBoxTransform() {
			super((state, d) -> {
				if (d.getAxis()
					.isVertical())
					return false;
				if (!BlockHelper.hasBlockStateProperty(state, CartAssemblerBlock.RAIL_SHAPE))
					return false;
				RailShape railShape = state.get(CartAssemblerBlock.RAIL_SHAPE);
				return (d.getAxis() == Axis.X) == (railShape == RailShape.NORTH_SOUTH);
			});
		}
		
		@Override
		protected Vec3d getSouthLocation() {
			return VecHelper.voxelSpace(8, 8, 18);
		}
		
	}
	
	public static enum CartMovementMode implements NamedIconOptions {

		ROTATE(AllIcons.I_CART_ROTATE),
		ROTATE_PAUSED(AllIcons.I_CART_ROTATE_PAUSED),
		ROTATION_LOCKED(AllIcons.I_CART_ROTATE_LOCKED),

		;

		private String translationKey;
		private AllIcons icon;

		private CartMovementMode(AllIcons icon) {
			this.icon = icon;
			translationKey = "contraptions.cart_movement_mode." + Lang.asId(name());
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

	public void resetTicksSinceMinecartUpdate() {
		ticksSinceMinecartUpdate = 0;
	}

	public boolean isMinecartUpdateValid() {
		return ticksSinceMinecartUpdate >= assemblyCooldown;
	}
}
