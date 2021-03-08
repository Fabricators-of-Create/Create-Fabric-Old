package com.simibubi.create.content.contraptions.components.deployer;

import static net.minecraftforge.eventbus.api.Event.Result.DEFAULT;
import static net.minecraftforge.eventbus.api.Event.Result.DENY;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Multimap;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity.Mode;
import com.simibubi.create.content.curiosities.tools.SandPaperItem;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.worldWrappers.WrappedWorld;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;

public class DeployerHandler {

	private static final class ItemUseWorld extends WrappedWorld {
		private final Direction face;
		private final BlockPos pos;
		boolean rayMode = false;

		private ItemUseWorld(World world, Direction face, BlockPos pos) {
			super(world);
			this.face = face;
			this.pos = pos;
		}

		@Override
		public BlockHitResult raycast(RaycastContext context) {
			rayMode = true;
			BlockHitResult rayTraceBlocks = super.raycast(context);
			rayMode = false;
			return rayTraceBlocks;
		}

		@Override
		public BlockState getBlockState(BlockPos position) {
			if (rayMode && (pos.offset(face.getOpposite(), 3)
				.equals(position)
				|| pos.offset(face.getOpposite(), 1)
					.equals(position)))
				return Blocks.BEDROCK.getDefaultState();
			return world.getBlockState(position);
		}
	}

	static boolean shouldActivate(ItemStack held, World world, BlockPos targetPos) {
		if (held.getItem() instanceof BlockItem)
			if (world.getBlockState(targetPos)
				.getBlock() == ((BlockItem) held.getItem()).getBlock())
				return false;

		if (held.getItem() instanceof BucketItem) {
			BucketItem bucketItem = (BucketItem) held.getItem();
			Fluid fluid = bucketItem.getFluid();
			if (fluid != Fluids.EMPTY && world.getFluidState(targetPos)
				.getFluid() == fluid)
				return false;
		}

		return true;
	}

	static void activate(DeployerFakePlayer player, Vec3d vec, BlockPos clickedPos, Vec3d extensionVector, Mode mode) {
		Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = player.getMainHandStack()
			.getAttributeModifiers(EquipmentSlot.MAINHAND);
		player.getAttributes()
			.addTemporaryModifiers(attributeModifiers);
		activateInner(player, vec, clickedPos, extensionVector, mode);
		player.getAttributes()
			.addTemporaryModifiers(attributeModifiers);
	}

	private static void activateInner(DeployerFakePlayer player, Vec3d vec, BlockPos clickedPos, Vec3d extensionVector,
		Mode mode) {

		Vec3d rayOrigin = vec.add(extensionVector.multiply(3 / 2f + 1 / 64f));
		Vec3d rayTarget = vec.add(extensionVector.multiply(5 / 2f - 1 / 64f));
		player.updatePosition(rayOrigin.x, rayOrigin.y, rayOrigin.z);
		BlockPos pos = new BlockPos(vec);
		ItemStack stack = player.getMainHandStack();
		Item item = stack.getItem();

		// Check for entities
		final ServerWorld world = player.getServerWorld();
		List<Entity> entities = world.getNonSpectatingEntities(Entity.class, new Box(clickedPos));
		Hand hand = Hand.MAIN_HAND;
		if (!entities.isEmpty()) {
			Entity entity = entities.get(world.random.nextInt(entities.size()));
			List<ItemEntity> capturedDrops = new ArrayList<>();
			boolean success = false;
			entity.captureDrops(capturedDrops);

			// Use on entity
			if (mode == Mode.USE) {
				ActionResult cancelResult = ForgeHooks.onInteractEntity(player, entity, hand);
				if (cancelResult == ActionResult.FAIL) {
					entity.captureDrops(null);
					return;
				}
				if (cancelResult == null) {
					if (entity.interact(player, hand).isAccepted())
						success = true;
					else if (entity instanceof LivingEntity
						&& stack.useOnEntity(player, (LivingEntity) entity, hand).isAccepted())
						success = true;
				}
				if (!success && stack.isFood() && entity instanceof PlayerEntity) {
					PlayerEntity playerEntity = (PlayerEntity) entity;
					if (playerEntity.canConsume(item.getFoodComponent()
						.isAlwaysEdible())) {
						playerEntity.eatFood(world, stack);
						player.spawnedItemEffects = stack.copy();
						success = true;
					}
				}
			}

			// Punch entity
			if (mode == Mode.PUNCH) {
				player.resetLastAttackedTicks();
				player.attack(entity);
				success = true;
			}

			entity.captureDrops(null);
			capturedDrops.forEach(e -> player.inventory.offerOrDrop(world, e.getStack()));
			if (success)
				return;
		}

		// Shoot ray
		RaycastContext rayTraceContext =
			new RaycastContext(rayOrigin, rayTarget, ShapeType.OUTLINE, FluidHandling.NONE, player);
		BlockHitResult result = world.raycast(rayTraceContext);
		if (result.getBlockPos() != clickedPos)
			result = new BlockHitResult(result.getPos(), result.getSide(), clickedPos, result.isInsideBlock());
		BlockState clickedState = world.getBlockState(clickedPos);
		Direction face = result.getSide();
		if (face == null)
			face = Direction.getFacing(extensionVector.x, extensionVector.y, extensionVector.z)
				.getOpposite();

		// Left click
		if (mode == Mode.PUNCH) {
			if (!world.canPlayerModifyAt(player, clickedPos))
				return;
			if (clickedState.getOutlineShape(world, clickedPos)
				.isEmpty()) {
				player.blockBreakingProgress = null;
				return;
			}
			LeftClickBlock event = ForgeHooks.onLeftClickBlock(player, clickedPos, face);
			if (event.isCanceled())
				return;
			if (BlockHelper.extinguishFire(world, player, clickedPos, face)) // FIXME: is there an equivalent in world, as there was in 1.15?
				return;
			if (event.getUseBlock() != DENY)
				clickedState.onBlockBreakStart(world, clickedPos, player);
			if (stack.isEmpty())
				return;

			float progress = clickedState.calcBlockBreakingDelta(player, world, clickedPos) * 16;
			float before = 0;
			Pair<BlockPos, Float> blockBreakingProgress = player.blockBreakingProgress;
			if (blockBreakingProgress != null)
				before = blockBreakingProgress.getValue();
			progress += before;

			if (progress >= 1) {
				safeTryHarvestBlock(player.interactionManager, clickedPos);
				world.setBlockBreakingInfo(player.getEntityId(), clickedPos, -1);
				player.blockBreakingProgress = null;
				return;
			}
			if (progress <= 0) {
				player.blockBreakingProgress = null;
				return;
			}

			if ((int) (before * 10) != (int) (progress * 10))
				world.setBlockBreakingInfo(player.getEntityId(), clickedPos, (int) (progress * 10));
			player.blockBreakingProgress = Pair.of(clickedPos, progress);
			return;
		}

		// Right click
		ItemUsageContext itemusecontext = new ItemUsageContext(player, hand, result);
		Event.Result useBlock = DEFAULT;
		Event.Result useItem = DEFAULT;
		if (!clickedState.getOutlineShape(world, clickedPos)
			.isEmpty()) {
			RightClickBlock event = ForgeHooks.onRightClickBlock(player, hand, clickedPos, face);
			useBlock = event.getUseBlock();
			useItem = event.getUseItem();
		}

		// Item has custom active use
		if (useItem != DENY) {
			ActionResult actionresult = stack.onItemUseFirst(itemusecontext);
			if (actionresult != ActionResult.PASS)
				return;
		}

		boolean holdingSomething = !player.getMainHandStack()
			.isEmpty();
		boolean flag1 =
			!(player.isSneaking() && holdingSomething) || (stack.doesSneakBypassUse(world, clickedPos, player));

		if (clickedState.getBlock() instanceof BeehiveBlock)
			return; // Beehives assume a lot about the usage context. Crashes to side-effects

		// Use on block
		if (useBlock != DENY && flag1 && clickedState.onUse(world, player, hand, result) == ActionResult.SUCCESS)
			return;
		if (stack.isEmpty())
			return;
		if (useItem == DENY)
			return;
		if (item instanceof BlockItem && !clickedState.canReplace(new ItemPlacementContext(itemusecontext)))
			return;

		// Reposition fire placement for convenience
		if (item == Items.FLINT_AND_STEEL) {
			Direction newFace = result.getSide();
			BlockPos newPos = result.getBlockPos();
			if (!AbstractFireBlock.method_30032(world, clickedPos, newFace))
				newFace = Direction.UP;
			if (clickedState.getMaterial() == Material.AIR)
				newPos = newPos.offset(face.getOpposite());
			result = new BlockHitResult(result.getPos(), newFace, newPos, result.isInsideBlock());
			itemusecontext = new ItemUsageContext(player, hand, result);
		}

		// 'Inert' item use behaviour & block placement
		ActionResult onItemUse = stack.useOnBlock(itemusecontext);
		if (onItemUse == ActionResult.SUCCESS)
			return;
		if (item == Items.ENDER_PEARL)
			return;

		// buckets create their own ray, We use a fake wall to contain the active area
		World itemUseWorld = world;
		if (item instanceof BucketItem || item instanceof SandPaperItem)
			itemUseWorld = new ItemUseWorld(world, face, pos);

		TypedActionResult<ItemStack> onItemRightClick = item.use(itemUseWorld, player, hand);
		ItemStack resultStack = onItemRightClick.getValue();
		if (resultStack != stack || resultStack.getCount() != stack.getCount() || resultStack.getMaxUseTime() > 0 || resultStack.getDamage() != stack.getDamage()) {
			player.setStackInHand(hand, onItemRightClick.getValue());
		}

		CompoundTag tag = stack.getTag();
		if (tag != null && stack.getItem() instanceof SandPaperItem && tag.contains("Polishing"))
			player.spawnedItemEffects = ItemStack.fromTag(tag.getCompound("Polishing"));

		if (!player.getActiveItem()
			.isEmpty())
			player.setStackInHand(hand, stack.finishUsing(world, player));

		player.clearActiveItem();
	}

	private static boolean safeTryHarvestBlock(ServerPlayerInteractionManager interactionManager, BlockPos clickedPos) {
		BlockState state = interactionManager.world.getBlockState(clickedPos);
		if (!(state.getBlock() instanceof BeehiveBlock))
			return interactionManager.tryBreakBlock(clickedPos);
		else {
			harvestBeehive(interactionManager, state, clickedPos);
		}
		return true;
	}

	private static void harvestBeehive(ServerPlayerInteractionManager interactionManager, BlockState state,
		BlockPos clickedPos) {
		// Modified code from PlayerInteractionManager, Block and BeehiveBlock to handle
		// deployers breaking beehives without crash.
		ItemStack itemstack = interactionManager.player.getMainHandStack();
		ItemStack itemstack1 = itemstack.copy();

		boolean flag1 = state.canHarvestBlock(interactionManager.world, clickedPos, interactionManager.player);
		itemstack.postMine(interactionManager.world, state, clickedPos, interactionManager.player);
		if (itemstack.isEmpty() && !itemstack1.isEmpty())
			net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(interactionManager.player, itemstack1,
				Hand.MAIN_HAND);

		boolean flag = state.removedByPlayer(interactionManager.world, clickedPos, interactionManager.player, flag1,
			interactionManager.world.getFluidState(clickedPos));
		if (flag)
			state.getBlock()
				.onBroken(interactionManager.world, clickedPos, state);

		if (flag && flag1) {
			interactionManager.player.incrementStat(Stats.MINED.getOrCreateStat(state.getBlock()));
			interactionManager.player.addExhaustion(0.005F);
			BlockEntity te = interactionManager.world.getBlockEntity(clickedPos);
			ItemStack heldItem = interactionManager.player.getMainHandStack();
			Block.dropStacks(state, interactionManager.world, clickedPos, te, interactionManager.player, heldItem);

			if (!interactionManager.world.isClient && te instanceof BeehiveBlockEntity) {
				BeehiveBlockEntity beehivetileentity = (BeehiveBlockEntity) te;
				if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, heldItem) == 0) {
					interactionManager.world.updateComparators(clickedPos, state.getBlock());
				}

				Criteria.BEE_NEST_DESTROYED.test(interactionManager.player,
					state.getBlock(), heldItem, beehivetileentity.getBeeCount());
			}
		}
	}
}
