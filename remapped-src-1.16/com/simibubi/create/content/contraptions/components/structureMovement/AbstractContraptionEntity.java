package com.simibubi.create.content.contraptions.components.structureMovement;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.MutablePair;
import com.simibubi.create.AllMovementBehaviours;
import com.simibubi.create.content.contraptions.components.actors.SeatEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.glue.SuperGlueEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.MountedContraption;
import com.simibubi.create.content.contraptions.components.structureMovement.sync.ContraptionSeatMappingPacket;
import com.simibubi.create.foundation.collision.Matrix3d;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.structure.Structure.StructureBlockInfo;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.network.PacketDistributor;

public abstract class AbstractContraptionEntity extends Entity implements IEntityAdditionalSpawnData {

	private static final TrackedData<Boolean> STALLED =
		DataTracker.registerData(AbstractContraptionEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public final Map<Entity, MutableInt> collidingEntities;

	protected Contraption contraption;
	protected boolean initialized;
	private boolean prevPosInvalid;
	private boolean ticking;

	public AbstractContraptionEntity(EntityType<?> entityTypeIn, World worldIn) {
		super(entityTypeIn, worldIn);
		prevPosInvalid = true;
		collidingEntities = new IdentityHashMap<>();
	}

	protected void setContraption(Contraption contraption) {
		this.contraption = contraption;
		if (contraption == null)
			return;
		if (world.isClient)
			return;
		contraption.onEntityCreated(this);
	}
	
	public boolean supportsTerrainCollision() {
		return contraption instanceof TranslatingContraption;
	}

	protected void contraptionInitialize() {
		contraption.onEntityInitialize(world, this);
		initialized = true;
	}

	public boolean collisionEnabled() {
		return true;
	}

	public void addSittingPassenger(Entity passenger, int seatIndex) {
		passenger.startRiding(this, true);
		if (world.isClient)
			return;
		contraption.getSeatMapping()
			.put(passenger.getUuid(), seatIndex);
		AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
			new ContraptionSeatMappingPacket(getEntityId(), contraption.getSeatMapping()));
	}

	@Override
	protected void removePassenger(Entity passenger) {
		Vec3d transformedVector = getPassengerPosition(passenger, 1);
		super.removePassenger(passenger);
		if (world.isClient)
			return;
		if (transformedVector != null)
			passenger.getPersistentData()
				.put("ContraptionDismountLocation", VecHelper.writeNBT(transformedVector));
		contraption.getSeatMapping()
			.remove(passenger.getUuid());
		AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
			new ContraptionSeatMappingPacket(getEntityId(), contraption.getSeatMapping()));
	}

	@Override
	public void updatePassengerPosition(Entity passenger, PositionUpdater callback) {
		if (!hasPassenger(passenger))
			return;
		Vec3d transformedVector = getPassengerPosition(passenger, 1);
		if (transformedVector == null)
			return;
		callback.accept(passenger, transformedVector.x, transformedVector.y, transformedVector.z);
	}

	protected Vec3d getPassengerPosition(Entity passenger, float partialTicks) {
		UUID id = passenger.getUuid();
		if (passenger instanceof OrientedContraptionEntity) {
			BlockPos localPos = contraption.getBearingPosOf(id);
			if (localPos != null)
				return toGlobalVector(VecHelper.getCenterOf(localPos), partialTicks)
					.add(VecHelper.getCenterOf(BlockPos.ORIGIN))
					.subtract(.5f, 1, .5f);
		}

		Box bb = passenger.getBoundingBox();
		double ySize = bb.getYLength();
		BlockPos seat = contraption.getSeatOf(id);
		if (seat == null)
			return null;
		Vec3d transformedVector =
			toGlobalVector(Vec3d.of(seat).add(.5, passenger.getHeightOffset() + ySize - .15f, .5), partialTicks)
				.add(VecHelper.getCenterOf(BlockPos.ORIGIN))
				.subtract(0.5, ySize, 0.5);
		return transformedVector;
	}

	@Override
	protected boolean canAddPassenger(Entity p_184219_1_) {
		if (p_184219_1_ instanceof OrientedContraptionEntity)
			return true;
		return contraption.getSeatMapping()
			.size() < contraption.getSeats()
				.size();
	}

	public boolean handlePlayerInteraction(PlayerEntity player, BlockPos localPos, Direction side,
		Hand interactionHand) {
		int indexOfSeat = contraption.getSeats()
			.indexOf(localPos);
		if (indexOfSeat == -1)
			return false;

		// Eject potential existing passenger
		Entity toDismount = null;
		for (Entry<UUID, Integer> entry : contraption.getSeatMapping()
			.entrySet()) {
			if (entry.getValue() != indexOfSeat)
				continue;
			for (Entity entity : getPassengerList()) {
				if (!entry.getKey()
					.equals(entity.getUuid()))
					continue;
				if (entity instanceof PlayerEntity)
					return false;
				toDismount = entity;
			}
		}

		if (toDismount != null && !world.isClient) {
			Vec3d transformedVector = getPassengerPosition(toDismount, 1);
			toDismount.stopRiding();
			if (transformedVector != null)
				toDismount.requestTeleport(transformedVector.x, transformedVector.y, transformedVector.z);
		}

		if (world.isClient)
			return true;
		addSittingPassenger(player, indexOfSeat);
		return true;
	}

	public Vec3d toGlobalVector(Vec3d localVec, float partialTicks) {
		Vec3d rotationOffset = VecHelper.getCenterOf(BlockPos.ORIGIN);
		localVec = localVec.subtract(rotationOffset);
		localVec = applyRotation(localVec, partialTicks);
		localVec = localVec.add(rotationOffset)
			.add(getAnchorVec());
		return localVec;
	}

	public Vec3d toLocalVector(Vec3d globalVec, float partialTicks) {
		Vec3d rotationOffset = VecHelper.getCenterOf(BlockPos.ORIGIN);
		globalVec = globalVec.subtract(getAnchorVec())
			.subtract(rotationOffset);
		globalVec = reverseRotation(globalVec, partialTicks);
		globalVec = globalVec.add(rotationOffset);
		return globalVec;
	}

	@Override
	public final void tick() {
		if (contraption == null) {
			remove();
			return;
		}

		for (Iterator<Entry<Entity, MutableInt>> iterator = collidingEntities.entrySet()
			.iterator(); iterator.hasNext();)
			if (iterator.next()
				.getValue()
				.incrementAndGet() > 3)
				iterator.remove();

		prevX = getX();
		prevY = getY();
		prevZ = getZ();
		prevPosInvalid = false;

		if (!initialized)
			contraptionInitialize();
		contraption.onEntityTick(world);
		tickContraption();
		super.tick();
	}

	protected abstract void tickContraption();

	public abstract Vec3d applyRotation(Vec3d localPos, float partialTicks);

	public abstract Vec3d reverseRotation(Vec3d localPos, float partialTicks);

	public void tickActors() {
		boolean stalledPreviously = contraption.stalled;

		if (!world.isClient)
			contraption.stalled = false;

		ticking = true;
		for (MutablePair<StructureBlockInfo, MovementContext> pair : contraption.getActors()) {
			MovementContext context = pair.right;
			StructureBlockInfo blockInfo = pair.left;
			MovementBehaviour actor = AllMovementBehaviours.of(blockInfo.state);

			Vec3d actorPosition = toGlobalVector(VecHelper.getCenterOf(blockInfo.pos)
				.add(actor.getActiveAreaOffset(context)), 1);
			BlockPos gridPosition = new BlockPos(actorPosition);
			boolean newPosVisited =
				!context.stall && shouldActorTrigger(context, blockInfo, actor, actorPosition, gridPosition);

			context.rotation = v -> applyRotation(v, 1);
			context.position = actorPosition;

			Vec3d oldMotion = context.motion;
			if (!actor.isActive(context))
				continue;
			if (newPosVisited && !context.stall) {
				actor.visitNewPosition(context, gridPosition);
				if (!isAlive())
					break;
				context.firstMovement = false;
			}
			if (!oldMotion.equals(context.motion)) {
				actor.onSpeedChanged(context, oldMotion, context.motion);
				if (!isAlive())
					break;
			}
			actor.tick(context);
			if (!isAlive())
				break;
			contraption.stalled |= context.stall;
		}
		if (!isAlive()) {
			contraption.stop(world);
			return;
		}
		ticking = false;

		for (Entity entity : getPassengerList()) {
			if (!(entity instanceof OrientedContraptionEntity))
				continue;
			if (!contraption.stabilizedSubContraptions.containsKey(entity.getUuid()))
				continue;
			OrientedContraptionEntity orientedCE = (OrientedContraptionEntity) entity;
			if (orientedCE.contraption != null && orientedCE.contraption.stalled) {
				contraption.stalled = true;
				break;
			}
		}

		if (!world.isClient) {
			if (!stalledPreviously && contraption.stalled)
				onContraptionStalled();
			dataTracker.set(STALLED, contraption.stalled);
			return;
		}

		contraption.stalled = isStalled();
	}

	protected void onContraptionStalled() {
		AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
			new ContraptionStallPacket(getEntityId(), getX(), getY(), getZ(), getStalledAngle()));
	}

	protected boolean shouldActorTrigger(MovementContext context, StructureBlockInfo blockInfo, MovementBehaviour actor,
		Vec3d actorPosition, BlockPos gridPosition) {
		Vec3d previousPosition = context.position;
		if (previousPosition == null)
			return false;

		context.motion = actorPosition.subtract(previousPosition);
		Vec3d relativeMotion = context.motion;
		relativeMotion = reverseRotation(relativeMotion, 1);
		context.relativeMotion = relativeMotion;
		return !new BlockPos(previousPosition).equals(gridPosition)
			|| context.relativeMotion.length() > 0 && context.firstMovement;
	}

	public void move(double x, double y, double z) {
		updatePosition(getX() + x, getY() + y, getZ() + z);
	}

	public Vec3d getAnchorVec() {
		return getPos();
	}

	public float getYawOffset() {
		return 0;
	}

	@Override
	public void updatePosition(double x, double y, double z) {
		super.updatePosition(x, y, z);
		if (contraption == null)
			return;
		Box cbox = contraption.bounds;
		if (cbox == null)
			return;
		Vec3d actualVec = getAnchorVec();
		setBoundingBox(cbox.offset(actualVec));
	}

	public static float yawFromVector(Vec3d vec) {
		return (float) ((3 * Math.PI / 2 + Math.atan2(vec.z, vec.x)) / Math.PI * 180);
	}

	public static float pitchFromVector(Vec3d vec) {
		return (float) ((Math.acos(vec.y)) / Math.PI * 180);
	}

	public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
		@SuppressWarnings("unchecked")
		EntityType.Builder<AbstractContraptionEntity> entityBuilder =
			(EntityType.Builder<AbstractContraptionEntity>) builder;
		return entityBuilder.setDimensions(1, 1);
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(STALLED, false);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	@Override
	public void writeSpawnData(PacketByteBuf buffer) {
		CompoundTag compound = new CompoundTag();
		writeAdditional(compound, true);
		buffer.writeCompoundTag(compound);
	}
	
	@Override
	protected final void writeCustomDataToTag(CompoundTag compound) {
		writeAdditional(compound, false);
	}
	
	protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
		if (contraption != null)
			compound.put("Contraption", contraption.writeNBT(spawnPacket));
		compound.putBoolean("Stalled", isStalled());
		compound.putBoolean("Initialized", initialized);
	}

	@Override
	public void readSpawnData(PacketByteBuf additionalData) {
		readAdditional(additionalData.readCompoundTag(), true);
	}
	
	@Override
	protected final void readCustomDataFromTag(CompoundTag compound) {
		readAdditional(compound, false);
	}
	
	protected void readAdditional(CompoundTag compound, boolean spawnData) {
		initialized = compound.getBoolean("Initialized");
		contraption = Contraption.fromNBT(world, compound.getCompound("Contraption"), spawnData);
		contraption.entity = this;
		dataTracker.set(STALLED, compound.getBoolean("Stalled"));
	}

	public void disassemble() {
		if (!isAlive())
			return;
		if (contraption == null)
			return;

		remove();

		StructureTransform transform = makeStructureTransform();
		AllPackets.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
			new ContraptionDisassemblyPacket(this.getEntityId(), transform));

		contraption.addBlocksToWorld(world, transform);
		contraption.addPassengersToWorld(world, transform, getPassengerList());

		for (Entity entity : getPassengerList()) {
			if (!(entity instanceof OrientedContraptionEntity))
				continue;
			UUID id = entity.getUuid();
			if (!contraption.stabilizedSubContraptions.containsKey(id))
				continue;
			BlockPos transformed = transform.apply(contraption.stabilizedSubContraptions.get(id)
				.getConnectedPos());
			entity.updatePosition(transformed.getX(), transformed.getY(), transformed.getZ());
			((AbstractContraptionEntity) entity).disassemble();
		}

		removeAllPassengers();
		moveCollidedEntitiesOnDisassembly(transform);
	}

	private void moveCollidedEntitiesOnDisassembly(StructureTransform transform) {
		for (Entity entity : collidingEntities.keySet()) {
			Vec3d localVec = toLocalVector(entity.getPos(), 0);
			Vec3d transformed = transform.apply(localVec);
			if (world.isClient)
				entity.updatePosition(transformed.x, transformed.y + 1 / 16f, transformed.z);
			else
				entity.requestTeleport(transformed.x, transformed.y + 1 / 16f, transformed.z);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void remove(boolean keepData) {
		if (!world.isClient && !removed && contraption != null) {
			if (!ticking)
				contraption.stop(world);
		}
		super.remove(keepData);
	}

	protected abstract StructureTransform makeStructureTransform();

	@Override
	public void kill() {
		removeAllPassengers();
		super.kill();
	}

	@Override
	protected void destroy() {
		removeAllPassengers();
		super.destroy();
	}

	@Override
	public void onRemovedFromWorld() {
		super.onRemovedFromWorld();
		if (world != null && world.isClient)
			return;
		getPassengerList().forEach(Entity::remove);
	}

	@Override
	protected void onSwimmingStart() {}

	public Contraption getContraption() {
		return contraption;
	}

	public boolean isStalled() {
		return dataTracker.get(STALLED);
	}

	@Environment(EnvType.CLIENT)
	static void handleStallPacket(ContraptionStallPacket packet) {
		Entity entity = MinecraftClient.getInstance().world.getEntityById(packet.entityID);
		if (!(entity instanceof AbstractContraptionEntity))
			return;
		AbstractContraptionEntity ce = (AbstractContraptionEntity) entity;
		ce.handleStallInformation(packet.x, packet.y, packet.z, packet.angle);
	}

	@Environment(EnvType.CLIENT)
	static void handleDisassemblyPacket(ContraptionDisassemblyPacket packet) {
  		Entity entity = MinecraftClient.getInstance().world.getEntityById(packet.entityID);
		if (!(entity instanceof AbstractContraptionEntity))
			return;
		AbstractContraptionEntity ce = (AbstractContraptionEntity) entity;
		ce.moveCollidedEntitiesOnDisassembly(packet.transform);
	}

	protected abstract float getStalledAngle();

	protected abstract void handleStallInformation(float x, float y, float z, float angle);

	@Override
	@SuppressWarnings("deprecation")
	public CompoundTag toTag(CompoundTag nbt) {
		Vec3d vec = getPos();
		List<Entity> passengers = getPassengerList();

		for (Entity entity : passengers) {
			// setPos has world accessing side-effects when removed == false
			entity.removed = true;

			// Gather passengers into same chunk when saving
			Vec3d prevVec = entity.getPos();
			entity.setPos(vec.x, prevVec.y, vec.z);

			// Super requires all passengers to not be removed in order to write them to the
			// tag
			entity.removed = false;
		}

		CompoundTag tag = super.toTag(nbt);
		return tag;
	}

	@Override
	// Make sure nothing can move contraptions out of the way
	public void setVelocity(Vec3d motionIn) {}

	@Override
	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.IGNORE;
	}

	public void setContraptionMotion(Vec3d vec) {
		super.setVelocity(vec);
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	public Vec3d getPrevPositionVec() {
		return prevPosInvalid ? getPos() : new Vec3d(prevX, prevY, prevZ);
	}

	public abstract ContraptionRotationState getRotationState();

	public Vec3d getContactPointMotion(Vec3d globalContactPoint) {
		if (prevPosInvalid)
			return Vec3d.ZERO;
		Vec3d contactPoint = toGlobalVector(toLocalVector(globalContactPoint, 0), 1);
		return contactPoint.subtract(globalContactPoint)
			.add(getPos().subtract(getPrevPositionVec()));
	}

	public boolean canCollideWith(Entity e) {
		if (e instanceof PlayerEntity && e.isSpectator())
			return false;
		if (e.noClip)
			return false;
		if (e instanceof AbstractDecorationEntity)
			return false;
		if (e instanceof AbstractMinecartEntity)
			return !(contraption instanceof MountedContraption);
		if (e instanceof SuperGlueEntity)
			return false;
		if (e instanceof SeatEntity)
			return false;
		if (e instanceof ProjectileEntity)
			return false;
		if (e.getVehicle() != null)
			return false;

		Entity riding = this.getVehicle();
		while (riding != null) {
			if (riding == e)
				return false;
			riding = riding.getVehicle();
		}

		return e.getPistonBehavior() == PistonBehavior.NORMAL;
	}

	@Override
	public boolean hasPlayerRider() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	public abstract void doLocalTransforms(float partialTicks, MatrixStack[] matrixStacks);

	public static class ContraptionRotationState {
		public static final ContraptionRotationState NONE = new ContraptionRotationState();

		float xRotation = 0;
		float yRotation = 0;
		float zRotation = 0;
		float secondYRotation = 0;
		Matrix3d matrix;

		public Matrix3d asMatrix() {
			if (matrix != null)
				return matrix;

			matrix = new Matrix3d().asIdentity();
			if (xRotation != 0)
				matrix.multiply(new Matrix3d().asXRotation(AngleHelper.rad(-xRotation)));
			if (yRotation != 0)
				matrix.multiply(new Matrix3d().asYRotation(AngleHelper.rad(yRotation)));
			if (zRotation != 0)
				matrix.multiply(new Matrix3d().asZRotation(AngleHelper.rad(-zRotation)));
			return matrix;
		}

		public boolean hasVerticalRotation() {
			return xRotation != 0 || zRotation != 0;
		}

		public float getYawOffset() {
			return secondYRotation;
		}

	}

}
