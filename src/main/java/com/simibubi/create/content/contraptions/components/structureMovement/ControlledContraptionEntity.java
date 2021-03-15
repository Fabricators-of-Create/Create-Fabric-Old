package com.simibubi.create.content.contraptions.components.structureMovement;

import static com.simibubi.create.foundation.utility.AngleHelper.angleLerp;

import com.simibubi.create.AllEntityTypes;
import com.simibubi.create.content.contraptions.components.structureMovement.bearing.BearingContraption;
import com.simibubi.create.foundation.utility.CNBTHelper;
import com.simibubi.create.foundation.utility.MatrixStacker;
import com.simibubi.create.foundation.utility.VecHelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Ex: Pistons, bearings <br>
 * Controlled Contraption Entities can rotate around one axis and translate.
 * <br>
 * They are bound to an {@link ControlContraption}
 */
public class ControlledContraptionEntity extends AbstractContraptionEntity {

	protected BlockPos controllerPos;
	protected Direction.Axis rotationAxis;
	protected float prevAngle;
	protected float angle;

	public ControlledContraptionEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	public static ControlledContraptionEntity create(World world, ControlContraption controller,
													 Contraption contraption) {
		ControlledContraptionEntity entity =
			new ControlledContraptionEntity(AllEntityTypes.CONTROLLED_CONTRAPTION, world);
		entity.controllerPos = controller.getBlockPosition();
		entity.setContraption(contraption);
		return entity;
	}

	@Override
	public Vec3d getContactPointMotion(Vec3d globalContactPoint) {
		if (contraption instanceof TranslatingContraption)
			return getVelocity();
		return super.getContactPointMotion(globalContactPoint);
	}

	@Override
	protected void setContraption(Contraption contraption) {
		super.setContraption(contraption);
		if (contraption instanceof BearingContraption)
			rotationAxis = ((BearingContraption) contraption).getFacing().getAxis();
	}

	@Override
	protected void readAdditional(CompoundTag compound, boolean spawnPacket) {
		super.readAdditional(compound, spawnPacket);
		controllerPos = NbtHelper.toBlockPos(compound.getCompound("Controller"));
		if (compound.contains("Axis"))
			rotationAxis = CNBTHelper.readEnum(compound, "Axis", Direction.Axis.class);
		angle = compound.getFloat("Angle");
	}

	@Override
	protected void writeAdditional(CompoundTag compound, boolean spawnPacket) {
		super.writeAdditional(compound, spawnPacket);
		compound.put("Controller", NbtHelper.fromBlockPos(controllerPos));
		if (rotationAxis != null)
			CNBTHelper.writeEnum(compound, "Axis", rotationAxis);
		compound.putFloat("Angle", angle);
	}

	@Override
	public ContraptionRotationState getRotationState() {
		ContraptionRotationState crs = new ContraptionRotationState();
		if (rotationAxis == Direction.Axis.X)
			crs.xRotation = angle;
		if (rotationAxis == Direction.Axis.Y)
			crs.yRotation = angle;
		if (rotationAxis == Direction.Axis.Z)
			crs.zRotation = angle;
		return crs;
	}

	@Override
	public Vec3d applyRotation(Vec3d localPos, float partialTicks) {
		localPos = VecHelper.rotate(localPos, getAngle(partialTicks), rotationAxis);
		return localPos;
	}

	@Override
	public Vec3d reverseRotation(Vec3d localPos, float partialTicks) {
		localPos = VecHelper.rotate(localPos, -getAngle(partialTicks), rotationAxis);
		return localPos;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public float getAngle(float partialTicks) {
		return partialTicks == 1.0F ? angle : angleLerp(partialTicks, prevAngle, angle);
	}

	public Direction.Axis getRotationAxis() {
		return rotationAxis;
	}

	public void setRotationAxis(Direction.Axis rotationAxis) {
		this.rotationAxis = rotationAxis;
	}

	@Override
	public void requestTeleport(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yw, float pt, int inc, boolean t) {
	}

	protected void tickContraption() {
		prevAngle = angle;
		tickActors();

		if (controllerPos == null)
			return;
		if (!world.canSetBlock(controllerPos))
			return;
		ControlContraption controller = getController();
		if (controller == null) {
			remove();
			return;
		}
		if (!controller.isAttachedTo(this)) {
			controller.attach(this);
			if (world.isClient)
				updatePosition(getX(), getY(), getZ());
		}

		Vec3d motion = getVelocity();
		move(motion.x, motion.y, motion.z);
		if (ContraptionCollider.collideBlocks(this))
			getController().collided();
	}

	@Override
	protected boolean shouldActorTrigger(MovementContext context, Structure.StructureBlockInfo blockInfo, MovementBehaviour actor,
										 Vec3d actorPosition, BlockPos gridPosition) {
		if (super.shouldActorTrigger(context, blockInfo, actor, actorPosition, gridPosition))
			return true;

		// Special activation timer for actors in the center of a bearing contraption
		if (!(contraption instanceof BearingContraption))
	 		return false;
	 	BearingContraption bc = (BearingContraption) contraption;
	 	Direction facing = bc.getFacing();
	 	Vec3d activeAreaOffset = actor.getActiveAreaOffset(context);
		if (!activeAreaOffset.multiply(VecHelper.axisAlingedPlaneOf(new Vec3d(facing.getUnitVector()))).equals(Vec3d.ZERO))
		 	return false;
	 	if (!VecHelper.onSameAxis(blockInfo.pos, (BlockPos) BlockPos.ZERO, facing.getAxis()))
			return false;
		context.motion = new Vec3d(facing.getUnitVector()).multiply(angle - prevAngle);
	 	context.relativeMotion = context.motion;
	 	int timer = context.data.getInt("StationaryTimer");
		if (timer > 0) {
			context.data.putInt("StationaryTimer", timer - 1);
			return false;
	 	}

	 	context.data.putInt("StationaryTimer", 20);
		return true;
	}

	protected ControlContraption getController() {
		if (controllerPos == null)
			return null;
		if (!world.canSetBlock(controllerPos))
			return null;
		BlockEntity te = world.getBlockEntity(controllerPos);
		if (!(te instanceof ControlContraption))
			return null;
		return (ControlContraption) te;
	}

	@Override
	protected StructureTransform makeStructureTransform() {
		BlockPos offset = new BlockPos(getAnchorVec().add(.5, .5, .5));
		float xRot = rotationAxis == Direction.Axis.X ? angle : 0;
		float yRot = rotationAxis == Direction.Axis.Y ? angle : 0;
		float zRot = rotationAxis == Direction.Axis.Z ? angle : 0;
		return new StructureTransform(offset, xRot, yRot, zRot);
	}

	@Override
	protected void onContraptionStalled() {
		ControlContraption controller = getController();
		if (controller != null)
			controller.onStall();
		super.onContraptionStalled();
	}

	@Override
	protected float getStalledAngle() {
		return angle;
	}

	@Override
	protected void handleStallInformation(float x, float y, float z, float angle) {
		setPos(x, y, z);
		this.angle = angle;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void doLocalTransforms(float partialTicks, MatrixStack[] matrixStacks) {
		float angle = getAngle(partialTicks);
		Direction.Axis axis = getRotationAxis();

		for (MatrixStack stack : matrixStacks)
			MatrixStacker.of(stack)
				.nudge(getEntityId())
				.centre()
				.rotate(angle, axis)
				.unCentre();
	}
}
