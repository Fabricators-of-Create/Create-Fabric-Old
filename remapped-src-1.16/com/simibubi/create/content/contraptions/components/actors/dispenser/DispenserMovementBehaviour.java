package com.simibubi.create.content.contraptions.components.actors.dispenser;

import java.util.HashMap;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DispenserMovementBehaviour extends DropperMovementBehaviour {
	private static final HashMap<Item, IMovedDispenseItemBehaviour> MOVED_DISPENSE_ITEM_BEHAVIOURS = new HashMap<>();
	private static final HashMap<Item, IMovedDispenseItemBehaviour> MOVED_PROJECTILE_DISPENSE_BEHAVIOURS = new HashMap<>();
	private static final DispenserLookup BEHAVIOUR_LOOKUP = new DispenserLookup();
	private static boolean spawneggsRegistered = false;

	public static void gatherMovedDispenseItemBehaviours() {
		IMovedDispenseItemBehaviour.init();
	}

	public static void registerMovedDispenseItemBehaviour(Item item, IMovedDispenseItemBehaviour movedDispenseItemBehaviour) {
		MOVED_DISPENSE_ITEM_BEHAVIOURS.put(item, movedDispenseItemBehaviour);
	}

	@Override
	protected void activate(MovementContext context, BlockPos pos) {
		if (!spawneggsRegistered) {
			spawneggsRegistered = true;
			IMovedDispenseItemBehaviour.initSpawneggs();
		}
		
		DispenseItemLocation location = getDispenseLocation(context);
		if (location.isEmpty()) {
			context.world.syncWorldEvent(1001, pos, 0);
		} else {
			ItemStack itemstack = getItemStackAt(location, context);
			// Special dispense item behaviour for moving contraptions
			if (MOVED_DISPENSE_ITEM_BEHAVIOURS.containsKey(itemstack.getItem())) {
				setItemStackAt(location, MOVED_DISPENSE_ITEM_BEHAVIOURS.get(itemstack.getItem()).dispense(itemstack, context, pos), context);
				return;
			}

			ItemStack backup = itemstack.copy();
			// If none is there, try vanilla registry
			try {
				if (MOVED_PROJECTILE_DISPENSE_BEHAVIOURS.containsKey(itemstack.getItem())) {
					setItemStackAt(location, MOVED_PROJECTILE_DISPENSE_BEHAVIOURS.get(itemstack.getItem()).dispense(itemstack, context, pos), context);
					return;
				}

				DispenserBehavior idispenseitembehavior = BEHAVIOUR_LOOKUP.getBehaviorForItem(itemstack);
				if (idispenseitembehavior instanceof ProjectileDispenserBehavior) { // Projectile behaviours can be converted most of the time
					IMovedDispenseItemBehaviour iMovedDispenseItemBehaviour = MovedProjectileDispenserBehaviour.of((ProjectileDispenserBehavior) idispenseitembehavior);
					setItemStackAt(location, iMovedDispenseItemBehaviour.dispense(itemstack, context, pos), context);
					MOVED_PROJECTILE_DISPENSE_BEHAVIOURS.put(itemstack.getItem(), iMovedDispenseItemBehaviour); // buffer conversion if successful
					return;
				}

				Vec3d facingVec = Vec3d.of(context.state.get(DispenserBlock.FACING).getVector());
				facingVec = context.rotation.apply(facingVec);
				facingVec.normalize();
				Direction clostestFacing = Direction.getFacing(facingVec.x, facingVec.y, facingVec.z);
				ContraptionBlockSource blockSource = new ContraptionBlockSource(context, pos, clostestFacing);

				if (idispenseitembehavior.getClass() != ItemDispenserBehavior.class) { // There is a dispense item behaviour registered for the vanilla dispenser
					setItemStackAt(location, idispenseitembehavior.dispense(blockSource, itemstack), context);
					return;
				}
			} catch (NullPointerException ignored) {
				itemstack = backup; // Something went wrong with the TE being null in ContraptionBlockSource, reset the stack
			}

			setItemStackAt(location, defaultBehaviour.dispense(itemstack, context, pos), context);  // the default: launch the item
		}
	}

	@ParametersAreNonnullByDefault
	@MethodsReturnNonnullByDefault
	private static class DispenserLookup extends DispenserBlock {
		protected DispenserLookup() {
			super(Block.Properties.copy(Blocks.DISPENSER));
		}

		public DispenserBehavior getBehaviorForItem(ItemStack itemStack) {
			return super.getBehaviorForItem(itemStack);
		}
	}
}
