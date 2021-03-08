package com.simibubi.create.content.curiosities.symmetry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.simibubi.create.content.curiosities.symmetry.mirror.CrossPlaneMirror;
import com.simibubi.create.content.curiosities.symmetry.mirror.EmptyMirror;
import com.simibubi.create.content.curiosities.symmetry.mirror.PlaneMirror;
import com.simibubi.create.content.curiosities.symmetry.mirror.SymmetryMirror;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.Iterate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;

public class SymmetryWandItem extends Item {

	public static final String SYMMETRY = "symmetry";
	private static final String ENABLE = "enable";

	public SymmetryWandItem(Settings properties) {
		super(properties.maxCount(1)
			.rarity(Rarity.UNCOMMON));
	}

	@Nonnull
	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity player = context.getPlayer();
		BlockPos pos = context.getBlockPos();
		if (player == null)
			return ActionResult.PASS;
		player.getItemCooldownManager()
			.set(this, 5);
		ItemStack wand = player.getStackInHand(context.getHand());
		checkNBT(wand);

		// Shift -> open GUI
		if (player.isSneaking()) {
			if (player.world.isClient) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					openWandGUI(wand, context.getHand());
				});
				player.getItemCooldownManager()
					.set(this, 5);
			}
			return ActionResult.SUCCESS;
		}

		if (context.getWorld().isClient || context.getHand() != Hand.MAIN_HAND)
			return ActionResult.SUCCESS;

		CompoundTag compound = wand.getTag()
			.getCompound(SYMMETRY);
		pos = pos.offset(context.getSide());
		SymmetryMirror previousElement = SymmetryMirror.fromNBT(compound);

		// No Shift -> Make / Move Mirror
		wand.getTag()
			.putBoolean(ENABLE, true);
		Vec3d pos3d = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
		SymmetryMirror newElement = new PlaneMirror(pos3d);

		if (previousElement instanceof EmptyMirror) {
			newElement.setOrientation(
				(player.getHorizontalFacing() == Direction.NORTH || player.getHorizontalFacing() == Direction.SOUTH)
					? PlaneMirror.Align.XY.ordinal()
					: PlaneMirror.Align.YZ.ordinal());
			newElement.enable = true;
			wand.getTag()
				.putBoolean(ENABLE, true);

		} else {
			previousElement.setPosition(pos3d);

			if (previousElement instanceof PlaneMirror) {
				previousElement.setOrientation(
					(player.getHorizontalFacing() == Direction.NORTH || player.getHorizontalFacing() == Direction.SOUTH)
						? PlaneMirror.Align.XY.ordinal()
						: PlaneMirror.Align.YZ.ordinal());
			}

			if (previousElement instanceof CrossPlaneMirror) {
				float rotation = player.getHeadYaw();
				float abs = Math.abs(rotation % 90);
				boolean diagonal = abs > 22 && abs < 45 + 22;
				previousElement
					.setOrientation(diagonal ? CrossPlaneMirror.Align.D.ordinal() : CrossPlaneMirror.Align.Y.ordinal());
			}

			newElement = previousElement;
		}

		compound = newElement.writeToNbt();
		wand.getTag()
			.put(SYMMETRY, compound);

		player.setStackInHand(context.getHand(), wand);
		return ActionResult.SUCCESS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack wand = playerIn.getStackInHand(handIn);
		checkNBT(wand);

		// Shift -> Open GUI
		if (playerIn.isSneaking()) {
			if (worldIn.isClient) {
				DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
					openWandGUI(playerIn.getStackInHand(handIn), handIn);
				});
				playerIn.getItemCooldownManager()
					.set(this, 5);
			}
			return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, wand);
		}

		// No Shift -> Clear Mirror
		wand.getTag()
			.putBoolean(ENABLE, false);
		return new TypedActionResult<ItemStack>(ActionResult.SUCCESS, wand);
	}

	@Environment(EnvType.CLIENT)
	private void openWandGUI(ItemStack wand, Hand hand) {
		ScreenOpener.open(new SymmetryWandScreen(wand, hand));
	}

	private static void checkNBT(ItemStack wand) {
		if (!wand.hasTag() || !wand.getTag()
			.contains(SYMMETRY)) {
			wand.setTag(new CompoundTag());
			wand.getTag()
				.put(SYMMETRY, new EmptyMirror(new Vec3d(0, 0, 0)).writeToNbt());
			wand.getTag()
				.putBoolean(ENABLE, false);
		}
	}

	public static boolean isEnabled(ItemStack stack) {
		checkNBT(stack);
		return stack.getTag()
			.getBoolean(ENABLE);
	}

	public static SymmetryMirror getMirror(ItemStack stack) {
		checkNBT(stack);
		return SymmetryMirror.fromNBT((CompoundTag) stack.getTag()
			.getCompound(SYMMETRY));
	}

	public static void apply(World world, ItemStack wand, PlayerEntity player, BlockPos pos, BlockState block) {
		checkNBT(wand);
		if (!isEnabled(wand))
			return;
		if (!BlockItem.BLOCK_ITEMS.containsKey(block.getBlock()))
			return;

		Map<BlockPos, BlockState> blockSet = new HashMap<>();
		blockSet.put(pos, block);
		SymmetryMirror symmetry = SymmetryMirror.fromNBT((CompoundTag) wand.getTag()
			.getCompound(SYMMETRY));

		Vec3d mirrorPos = symmetry.getPosition();
		if (mirrorPos.distanceTo(Vec3d.of(pos)) > AllConfigs.SERVER.curiosities.maxSymmetryWandRange.get())
			return;
		if (!player.isCreative() && isHoldingBlock(player, block)
			&& BlockHelper.findAndRemoveInInventory(block, player, 1) == 0)
			return;

		symmetry.process(blockSet);
		BlockPos to = new BlockPos(mirrorPos);
		List<BlockPos> targets = new ArrayList<>();
		targets.add(pos);

		for (BlockPos position : blockSet.keySet()) {
			if (position.equals(pos))
				continue;

			if (world.canPlace(block, position, ShapeContext.of(player))) {
				BlockState blockState = blockSet.get(position);
				for (Direction face : Iterate.directions)
					blockState = blockState.getStateForNeighborUpdate(face, world.getBlockState(position.offset(face)), world,
						position, position.offset(face));

				if (player.isCreative()) {
					world.setBlockState(position, blockState);
					targets.add(position);
					continue;
				}

				BlockState toReplace = world.getBlockState(position);
				if (!toReplace.getMaterial()
					.isReplaceable())
					continue;
				if (toReplace.getHardness(world, position) == -1)
					continue;
				if (BlockHelper.findAndRemoveInInventory(blockState, player, 1) == 0)
					continue;

				world.setBlockState(position, blockState);
				targets.add(position);
			}
		}

		AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
			new SymmetryEffectPacket(to, targets));
	}

	private static boolean isHoldingBlock(PlayerEntity player, BlockState block) {
		ItemStack itemBlock = BlockHelper.getRequiredItem(block);
		return player.getMainHandStack()
			.isItemEqualIgnoreDamage(itemBlock)
			|| player.getOffHandStack()
				.isItemEqualIgnoreDamage(itemBlock);
	}

	public static void remove(World world, ItemStack wand, PlayerEntity player, BlockPos pos) {
		BlockState air = Blocks.AIR.getDefaultState();
		BlockState ogBlock = world.getBlockState(pos);
		checkNBT(wand);
		if (!isEnabled(wand))
			return;

		Map<BlockPos, BlockState> blockSet = new HashMap<>();
		blockSet.put(pos, air);
		SymmetryMirror symmetry = SymmetryMirror.fromNBT((CompoundTag) wand.getTag()
			.getCompound(SYMMETRY));

		Vec3d mirrorPos = symmetry.getPosition();
		if (mirrorPos.distanceTo(Vec3d.of(pos)) > AllConfigs.SERVER.curiosities.maxSymmetryWandRange.get())
			return;

		symmetry.process(blockSet);

		BlockPos to = new BlockPos(mirrorPos);
		List<BlockPos> targets = new ArrayList<>();

		targets.add(pos);
		for (BlockPos position : blockSet.keySet()) {
			if (!player.isCreative() && ogBlock.getBlock() != world.getBlockState(position)
				.getBlock())
				continue;
			if (position.equals(pos))
				continue;

			BlockState blockstate = world.getBlockState(position);
			if (!blockstate.isAir(world, position)) {
				targets.add(position);
				world.syncWorldEvent(2001, position, Block.getRawIdFromState(blockstate));
				world.setBlockState(position, air, 3);

				if (!player.isCreative()) {
					if (!player.getMainHandStack()
						.isEmpty())
						player.getMainHandStack()
							.postMine(world, blockstate, position, player);
					BlockEntity tileentity = blockstate.hasTileEntity() ? world.getBlockEntity(position) : null;
					Block.dropStacks(blockstate, world, pos, tileentity, player, player.getMainHandStack()); // Add fortune, silk touch and other loot modifiers
				}
			}
		}

		AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
			new SymmetryEffectPacket(to, targets));
	}

}
