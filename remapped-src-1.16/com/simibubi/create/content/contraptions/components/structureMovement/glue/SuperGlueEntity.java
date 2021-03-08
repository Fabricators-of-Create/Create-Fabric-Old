package com.simibubi.create.content.contraptions.components.structureMovement.glue;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementTraits;
import com.simibubi.create.content.schematics.ISpecialEntityItemRequirement;
import com.simibubi.create.content.schematics.ItemRequirement;
import com.simibubi.create.content.schematics.ItemRequirement.ItemUseType;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.BlockFace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public class SuperGlueEntity extends Entity implements IEntityAdditionalSpawnData, ISpecialEntityItemRequirement {

	private int validationTimer;
	protected BlockPos hangingPosition;
	protected Direction facingDirection = Direction.SOUTH;

	public SuperGlueEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public SuperGlueEntity(World world, BlockPos pos, Direction direction) {
		this(AllEntityTypes.SUPER_GLUE.get(), world);
		hangingPosition = pos;
		facingDirection = direction;
		updateFacingWithBoundingBox();
	}

	@Override
	protected void initDataTracker() {}

	public int getWidthPixels() {
		return 12;
	}

	public int getHeightPixels() {
		return 12;
	}

	public void onBroken(@Nullable Entity breaker) {
		playSound(SoundEvents.ENTITY_SLIME_SQUISH_SMALL, 1.0F, 1.0F);
		if (onValidSurface()) {
			AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
				new GlueEffectPacket(getHangingPosition(), getFacingDirection().getOpposite(), false));
			playSound(AllSoundEvents.SLIME_ADDED.get(), 0.5F, 0.5F);
		}
	}

	public void playPlaceSound() {
		playSound(AllSoundEvents.SLIME_ADDED.get(), 0.5F, 0.75F);
	}

	protected void updateFacingWithBoundingBox() {
		Validate.notNull(getFacingDirection());
		if (getFacingDirection().getAxis()
			.isHorizontal()) {
			this.pitch = 0.0F;
			this.yaw = getFacingDirection().getHorizontal() * 90;
		} else {
			this.pitch = -90 * getFacingDirection().getDirection()
				.offset();
			this.yaw = 0.0F;
		}

		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		this.updateBoundingBox();
	}

	protected void updateBoundingBox() {
		if (this.getFacingDirection() != null) {
			double offset = 0.5 - 1 / 256d;
			double x = hangingPosition.getX() + 0.5 - facingDirection.getOffsetX() * offset;
			double y = hangingPosition.getY() + 0.5 - facingDirection.getOffsetY() * offset;
			double z = hangingPosition.getZ() + 0.5 - facingDirection.getOffsetZ() * offset;
			this.setPos(x, y, z);
			double w = getWidthPixels();
			double h = getHeightPixels();
			double l = getWidthPixels();
			Axis axis = this.getFacingDirection()
				.getAxis();
			double depth = 2 - 1 / 128f;

			switch (axis) {
			case X:
				w = depth;
				break;
			case Y:
				h = depth;
				break;
			case Z:
				l = depth;
			}

			w = w / 32.0D;
			h = h / 32.0D;
			l = l / 32.0D;
			this.setBoundingBox(new Box(x - w, y - h, z - l, x + w, y + h, z + l));
		}
	}

	@Override
	public void tick() {
		if (this.validationTimer++ == 10 && !this.world.isClient) {
			this.validationTimer = 0;
			if (isAlive() && !this.onValidSurface()) {
				remove();
				onBroken(null);
			}
		}

	}

	public boolean isVisible() {
		if (!isAlive())
			return false;
		BlockPos pos = hangingPosition;
		BlockPos pos2 = pos.offset(getFacingDirection().getOpposite());
		return isValidFace(world, pos2, getFacingDirection()) != isValidFace(world, pos,
			getFacingDirection().getOpposite());
	}

	public boolean onValidSurface() {
		BlockPos pos = hangingPosition;
		BlockPos pos2 = hangingPosition.offset(getFacingDirection().getOpposite());
		if (!world.isAreaLoaded(pos, 0) || !world.isAreaLoaded(pos2, 0))
			return true;
		if (!isValidFace(world, pos2, getFacingDirection())
			&& !isValidFace(world, pos, getFacingDirection().getOpposite()))
			return false;
		return world.getOtherEntities(this, getBoundingBox(), e -> e instanceof SuperGlueEntity)
			.isEmpty();
	}

	public static boolean isValidFace(World world, BlockPos pos, Direction direction) {
		BlockState state = world.getBlockState(pos);
		if (BlockMovementTraits.isBlockAttachedTowards(world, pos, state, direction))
			return true;
		if (!BlockMovementTraits.movementNecessary(state, world, pos))
			return false;
		if (BlockMovementTraits.notSupportive(state, direction))
			return false;
		return true;
	}

	@Override
	public boolean collides() {
		return true;
	}

	@Override
	public boolean handleAttack(Entity entity) {
		return entity instanceof PlayerEntity
			? damage(DamageSource.player((PlayerEntity) entity), 0)
			: false;
	}

	@Override
	public Direction getHorizontalFacing() {
		return this.getFacingDirection();
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source))
			return false;
		if (isAlive() && !world.isClient && isVisible()) {
			remove();
			scheduleVelocityUpdate();
			onBroken(source.getAttacker());
		}

		return true;
	}

	@Override
	public void move(MovementType typeIn, Vec3d pos) {
		if (!world.isClient && isAlive() && pos.lengthSquared() > 0.0D) {
			remove();
			onBroken(null);
		}
	}

	@Override
	public void addVelocity(double x, double y, double z) {
		if (!world.isClient && isAlive() && x * x + y * y + z * z > 0.0D) {
			remove();
			onBroken(null);
		}
	}

	@Override
	protected float getEyeHeight(EntityPose poseIn, EntityDimensions sizeIn) {
		return 0.0F;
	}

	@Override
	public ItemStack getPickedResult(HitResult target) {
		return AllItems.SUPER_GLUE.asStack();
	}

	@Override
	public void pushAwayFrom(Entity entityIn) {
		super.pushAwayFrom(entityIn);
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			triggerPlaceBlock(player, hand);
		});
		return ActionResult.CONSUME;
	}

	@Environment(EnvType.CLIENT)
	private void triggerPlaceBlock(PlayerEntity player, Hand hand) {
		if (!(player instanceof ClientPlayerEntity))
			return;
		if (!(player.world instanceof ClientWorld))
			return;

		ClientPlayerEntity cPlayer = (ClientPlayerEntity) player;
		MinecraftClient mc = MinecraftClient.getInstance();
		HitResult ray =
			cPlayer.raycast(mc.interactionManager.getReachDistance(), AnimationTickHolder.getPartialTicks(), false);

		if (!(ray instanceof BlockHitResult))
			return;
		if (ray.getType() == Type.MISS)
			return;
		BlockHitResult blockRay = (BlockHitResult) ray;
		BlockFace rayFace = new BlockFace(blockRay.getBlockPos(), blockRay.getSide());
		BlockFace hangingFace = new BlockFace(getHangingPosition(), getFacingDirection().getOpposite());
		if (!rayFace.isEquivalent(hangingFace))
			return;

		for (Hand handIn : Hand.values()) {
			ItemStack itemstack = cPlayer.getStackInHand(handIn);
			int countBefore = itemstack.getCount();
			ActionResult actionResultType =
				mc.interactionManager.interactBlock(cPlayer, (ClientWorld) cPlayer.world, handIn, blockRay);
			if (actionResultType != ActionResult.SUCCESS)
				return;

			cPlayer.swingHand(handIn);
			if (!itemstack.isEmpty() && (itemstack.getCount() != countBefore || mc.interactionManager.hasCreativeInventory()))
				mc.gameRenderer.firstPersonRenderer.resetEquipProgress(handIn);
			return;
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compound) {
		compound.putByte("Facing", (byte) this.getFacingDirection()
			.getId());
		BlockPos blockpos = this.getHangingPosition();
		compound.putInt("TileX", blockpos.getX());
		compound.putInt("TileY", blockpos.getY());
		compound.putInt("TileZ", blockpos.getZ());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compound) {
		this.hangingPosition =
			new BlockPos(compound.getInt("TileX"), compound.getInt("TileY"), compound.getInt("TileZ"));
		this.facingDirection = Direction.byId(compound.getByte("Facing"));
		updateFacingWithBoundingBox();
	}

	@Override
	public ItemEntity dropStack(ItemStack stack, float yOffset) {
		float xOffset = (float) this.getFacingDirection()
			.getOffsetX() * 0.15F;
		float zOffset = (float) this.getFacingDirection()
			.getOffsetZ() * 0.15F;
		ItemEntity itementity =
			new ItemEntity(this.world, this.getX() + xOffset, this.getY() + yOffset, this.getZ() + zOffset, stack);
		itementity.setToDefaultPickupDelay();
		this.world.spawnEntity(itementity);
		return itementity;
	}

	@Override
	protected boolean shouldSetPositionOnLoad() {
		return false;
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		hangingPosition = new BlockPos(x, y, z);
		updateBoundingBox();
		velocityDirty = true;
	}

	@Override
	public float applyRotation(BlockRotation transformRotation) {
		if (this.getFacingDirection()
			.getAxis() != Direction.Axis.Y) {
			switch (transformRotation) {
			case CLOCKWISE_180:
				facingDirection = facingDirection.getOpposite();
				break;
			case COUNTERCLOCKWISE_90:
				facingDirection = facingDirection.rotateYCounterclockwise();
				break;
			case CLOCKWISE_90:
				facingDirection = facingDirection.rotateYClockwise();
			default:
				break;
			}
		}

		float f = MathHelper.wrapDegrees(this.yaw);
		switch (transformRotation) {
		case CLOCKWISE_180:
			return f + 180.0F;
		case COUNTERCLOCKWISE_90:
			return f + 90.0F;
		case CLOCKWISE_90:
			return f + 270.0F;
		default:
			return f;
		}
	}

	public BlockPos getHangingPosition() {
		return this.hangingPosition;
	}

	@Override
	public float applyMirror(BlockMirror transformMirror) {
		return this.applyRotation(transformMirror.getRotation(this.getFacingDirection()));
	}

	public Direction getAttachedDirection(BlockPos pos) {
		return !pos.equals(hangingPosition) ? getFacingDirection() : getFacingDirection().getOpposite();
	}

	@Override
	public void onStruckByLightning(ServerWorld world, LightningEntity lightningBolt) {}

	@Override
	public void calculateDimensions() {}

	public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
		@SuppressWarnings("unchecked")
		EntityType.Builder<SuperGlueEntity> entityBuilder = (EntityType.Builder<SuperGlueEntity>) builder;
		return entityBuilder;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketByteBuf buffer) {
		CompoundTag compound = new CompoundTag();
		writeCustomDataToTag(compound);
		buffer.writeCompoundTag(compound);
	}

	@Override
	public void readSpawnData(PacketByteBuf additionalData) {
		readCustomDataFromTag(additionalData.readCompoundTag());
	}

	public Direction getFacingDirection() {
		return facingDirection;
	}

	@Override
	public ItemRequirement getRequiredItems() {
		return new ItemRequirement(ItemUseType.DAMAGE, AllItems.SUPER_GLUE.get());
	}

	@Override
	public boolean canAvoidTraps() {
		return true;
	}
}
