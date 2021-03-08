package com.simibubi.create.content.contraptions.components.deployer;

import static com.simibubi.create.content.contraptions.base.DirectionalKineticBlock.FACING;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.curiosities.tools.SandPaperItem;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public class DeployerTileEntity extends KineticTileEntity {

	protected State state;
	protected Mode mode;
	protected ItemStack heldItem = ItemStack.EMPTY;
	protected DeployerFakePlayer player;
	protected int timer;
	protected float reach;
	protected boolean boop = false;
	protected List<ItemStack> overflowItems = new ArrayList<>();
	protected FilteringBehaviour filtering;
	protected boolean redstoneLocked;
	private LazyOptional<IItemHandlerModifiable> invHandler;
	private ListTag deferredInventoryList;

	enum State {
		WAITING, EXPANDING, RETRACTING, DUMPING;
	}

	enum Mode {
		PUNCH, USE
	}

	public DeployerTileEntity(BlockEntityType<? extends DeployerTileEntity> type) {
		super(type);
		state = State.WAITING;
		mode = Mode.USE;
		heldItem = ItemStack.EMPTY;
		redstoneLocked = false;
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		filtering = new FilteringBehaviour(this, new DeployerFilterSlot());
		behaviours.add(filtering);
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!world.isClient) {
			player = new DeployerFakePlayer((ServerWorld) world);
			if (deferredInventoryList != null) {
				player.inventory.deserialize(deferredInventoryList);
				deferredInventoryList = null;
				heldItem = player.getMainHandStack();
				sendData();
			}
			Vec3d initialPos = VecHelper.getCenterOf(pos.offset(getCachedState().get(FACING)));
			player.updatePosition(initialPos.x, initialPos.y, initialPos.z);
		}
		invHandler = LazyOptional.of(this::createHandler);
	}

	protected void onExtract(ItemStack stack) {
		player.setStackInHand(Hand.MAIN_HAND, stack.copy());
		sendData();
		markDirty();
	}

	protected int getTimerSpeed() {
		return (int) (getSpeed() == 0 ? 0 : MathHelper.clamp(Math.abs(getSpeed() * 2), 8, 512));
	}

	@Override
	public void tick() {
		super.tick();

		if (getSpeed() == 0)
			return;
		if (!world.isClient && player != null && player.blockBreakingProgress != null) {
			if (world.isAir(player.blockBreakingProgress.getKey())) {
				world.setBlockBreakingInfo(player.getEntityId(), player.blockBreakingProgress.getKey(), -1);
				player.blockBreakingProgress = null;
			}
		}
		if (timer > 0) {
			timer -= getTimerSpeed();
			return;
		}
		if (world.isClient)
			return;

		ItemStack stack = player.getMainHandStack();
		if (state == State.WAITING) {
			if (!overflowItems.isEmpty()) {
				timer = getTimerSpeed() * 10;
				return;
			}

			boolean changed = false;
			for (int i = 0; i < player.inventory.size(); i++) {
				if (overflowItems.size() > 10)
					break;
				ItemStack item = player.inventory.getStack(i);
				if (item.isEmpty())
					continue;
				if (item != stack || !filtering.test(item)) {
					overflowItems.add(item);
					player.inventory.setStack(i, ItemStack.EMPTY);
					changed = true;
				}
			}

			if (changed) {
				sendData();
				timer = getTimerSpeed() * 10;
				return;
			}

			Direction facing = getCachedState().get(FACING);
			if (mode == Mode.USE && !DeployerHandler.shouldActivate(stack, world, pos.offset(facing, 2))) {
				timer = getTimerSpeed() * 10;
				return;
			}

			// Check for advancement conditions
			if (mode == Mode.PUNCH && !boop && startBoop(facing))
				return;

			if (redstoneLocked)
				return;

			state = State.EXPANDING;
			Vec3d movementVector = getMovementVector();
			Vec3d rayOrigin = VecHelper.getCenterOf(pos)
				.add(movementVector.multiply(3 / 2f));
			Vec3d rayTarget = VecHelper.getCenterOf(pos)
				.add(movementVector.multiply(5 / 2f));
			RaycastContext rayTraceContext =
				new RaycastContext(rayOrigin, rayTarget, ShapeType.OUTLINE, FluidHandling.NONE, player);
			BlockHitResult result = world.raycast(rayTraceContext);
			reach = (float) (.5f + Math.min(result.getPos()
				.subtract(rayOrigin)
				.length(), .75f));

			timer = 1000;
			sendData();
			return;
		}

		if (state == State.EXPANDING) {
			if (boop)
				triggerBoop();
			activate();

			state = State.RETRACTING;
			timer = 1000;
			sendData();
			return;
		}

		if (state == State.RETRACTING) {
			state = State.WAITING;
			timer = 500;
			sendData();
			return;
		}

	}

	public boolean startBoop(Direction facing) {
		if (!world.isAir(pos.offset(facing, 1)) || !world.isAir(pos.offset(facing, 2)))
			return false;
		BlockPos otherDeployer = pos.offset(facing, 4);
		if (!world.canSetBlock(otherDeployer))
			return false;
		BlockEntity otherTile = world.getBlockEntity(otherDeployer);
		if (!(otherTile instanceof DeployerTileEntity))
			return false;
		DeployerTileEntity deployerTile = (DeployerTileEntity) otherTile;
		if (world.getBlockState(otherDeployer)
			.get(FACING)
			.getOpposite() != facing || deployerTile.mode != Mode.PUNCH)
			return false;

		boop = true;
		reach = 1f;
		timer = 1000;
		state = State.EXPANDING;
		sendData();
		return true;
	}

	public void triggerBoop() {
		BlockEntity otherTile = world.getBlockEntity(pos.offset(getCachedState().get(FACING), 4));
		if (!(otherTile instanceof DeployerTileEntity))
			return;

		DeployerTileEntity deployerTile = (DeployerTileEntity) otherTile;
		if (!deployerTile.boop || deployerTile.state != State.EXPANDING)
			return;
		if (deployerTile.timer > 0)
			return;

		// everything should be met
		boop = false;
		deployerTile.boop = false;
		deployerTile.state = State.RETRACTING;
		deployerTile.timer = 1000;
		deployerTile.sendData();

		// award nearby players
		List<ServerPlayerEntity> players =
			world.getNonSpectatingEntities(ServerPlayerEntity.class, new Box(pos).expand(9));
		players.forEach(AllTriggers.DEPLOYER_BOOP::trigger);
	}

	protected void activate() {
		Vec3d movementVector = getMovementVector();
		Direction direction = getCachedState().get(FACING);
		Vec3d center = VecHelper.getCenterOf(pos);
		BlockPos clickedPos = pos.offset(direction, 2);
		player.yaw = direction.asRotation();
		player.pitch = direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0;

		DeployerHandler.activate(player, center, clickedPos, movementVector, mode);
		if (player != null)
			heldItem = player.getMainHandStack();
	}

	protected Vec3d getMovementVector() {
		if (!AllBlocks.DEPLOYER.has(getCachedState()))
			return Vec3d.ZERO;
		return Vec3d.of(getCachedState().get(FACING)
			.getVector());
	}

	@Override
	protected void fromTag(BlockState blockState, CompoundTag compound, boolean clientPacket) {
		state = NBTHelper.readEnum(compound, "State", State.class);
		mode = NBTHelper.readEnum(compound, "Mode", Mode.class);
		timer = compound.getInt("Timer");
		redstoneLocked = compound.getBoolean("Powered");

		deferredInventoryList = compound.getList("Inventory", NBT.TAG_COMPOUND);
		overflowItems = NBTHelper.readItemList(compound.getList("Overflow", NBT.TAG_COMPOUND));
		if (compound.contains("HeldItem"))
			heldItem = ItemStack.fromTag(compound.getCompound("HeldItem"));
		super.fromTag(blockState, compound, clientPacket);

		if (!clientPacket)
			return;
		reach = compound.getFloat("Reach");
		if (compound.contains("Particle")) {
			ItemStack particleStack = ItemStack.fromTag(compound.getCompound("Particle"));
			SandPaperItem.spawnParticles(VecHelper.getCenterOf(pos)
				.add(getMovementVector().multiply(2f)), particleStack, this.world);
		}
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		NBTHelper.writeEnum(compound, "Mode", mode);
		NBTHelper.writeEnum(compound, "State", state);
		compound.putInt("Timer", timer);
		compound.putBoolean("Powered", redstoneLocked);

		if (player != null) {
			compound.put("HeldItem", player.getMainHandStack()
				.serializeNBT());
			ListTag invNBT = new ListTag();
			player.inventory.serialize(invNBT);
			compound.put("Inventory", invNBT);
			compound.put("Overflow", NBTHelper.writeItemList(overflowItems));
		}

		super.write(compound, clientPacket);

		if (!clientPacket)
			return;
		compound.putFloat("Reach", reach);
		if (player == null)
			return;
		compound.put("HeldItem", player.getMainHandStack()
			.serializeNBT());
		if (player.spawnedItemEffects != null) {
			compound.put("Particle", player.spawnedItemEffects.serializeNBT());
			player.spawnedItemEffects = null;
		}
	}

	private IItemHandlerModifiable createHandler() {
		return new DeployerItemHandler(this);
	}
	
	public void redstoneUpdate() {
		if (world.isClient)
			return;
		boolean blockPowered = world.isReceivingRedstonePower(pos);
		if (blockPowered == redstoneLocked)
			return;
		redstoneLocked = blockPowered;
		sendData();
	}

	public AllBlockPartials getHandPose() {
		return mode == Mode.PUNCH ? AllBlockPartials.DEPLOYER_HAND_PUNCHING
			: heldItem.isEmpty() ? AllBlockPartials.DEPLOYER_HAND_POINTING : AllBlockPartials.DEPLOYER_HAND_HOLDING;
	}

	@Override
	public Box makeRenderBoundingBox() {
		return super.makeRenderBoundingBox().expand(3);
	}

	@Override
	public void markRemoved() {
		super.markRemoved();
		if (invHandler != null)
			invHandler.invalidate();
	}

	public void changeMode() {
		mode = mode == Mode.PUNCH ? Mode.USE : Mode.PUNCH;
		markDirty();
		sendData();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (isItemHandlerCap(cap) && invHandler != null) 
			return invHandler.cast();
		return super.getCapability(cap, side);
	}
	
	@Override
	public boolean addToTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
		if (super.addToTooltip(tooltip, isPlayerSneaking))
			return true;
		if (getSpeed() == 0)
			return false;
		if (overflowItems.isEmpty())
			return false;
		TooltipHelper.addHint(tooltip, "hint.full_deployer");
		return true;
	}

	@Override
	public boolean shouldRenderAsTE() {
		return true;
	}
}
