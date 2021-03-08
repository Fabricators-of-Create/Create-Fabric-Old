package com.simibubi.create.content.contraptions.components.actors;

import com.simibubi.create.AllEntityTypes;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;

public class SeatEntity extends Entity implements IEntityAdditionalSpawnData {

	public SeatEntity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
		super(p_i48580_1_, p_i48580_2_);
	}

	public SeatEntity(World world, BlockPos pos) {
		this(AllEntityTypes.SEAT.get(), world);
		noClip = true;
	}

	public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
		@SuppressWarnings("unchecked")
		EntityType.Builder<SeatEntity> entityBuilder = (EntityType.Builder<SeatEntity>) builder;
		return entityBuilder.setDimensions(0.25f, 0.35f);
	}

	@Override
	public Box getBoundingBox() {
		return super.getBoundingBox();
	}
	
	@Override
	public void setPos(double x, double y, double z) {
		super.setPos(x, y, z);
		Box bb = getBoundingBox();
		Vec3d diff = new Vec3d(x, y, z).subtract(bb.getCenter());
		setBoundingBox(bb.offset(diff));
	}

	@Override
	public void setVelocity(Vec3d p_213317_1_) {}

	@Override
	public void tick() {
		if (world.isClient) 
			return;
		boolean blockPresent = world.getBlockState(getBlockPos())
			.getBlock() instanceof SeatBlock;
		if (hasPassengers() && blockPresent)
			return;
		this.remove();
	}

	@Override
	protected boolean canStartRiding(Entity p_184228_1_) {
		return true;
	}

	@Override
	protected void removePassenger(Entity entity) {
		super.removePassenger(entity);
		Vec3d pos = entity.getPos();
		entity.updatePosition(pos.x, pos.y + 0.85f, pos.z);
	}

	@Override
	protected void initDataTracker() {}

	@Override
	protected void readCustomDataFromTag(CompoundTag p_70037_1_) {}

	@Override
	protected void writeCustomDataToTag(CompoundTag p_213281_1_) {}

	@Override
	public Packet<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public static class Render extends EntityRenderer<SeatEntity> {

		public Render(EntityRenderDispatcher p_i46179_1_) {
			super(p_i46179_1_);
		}

		@Override
		public boolean shouldRender(SeatEntity p_225626_1_, Frustum p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_) {
			return false;
		}

		@Override
		public Identifier getEntityTexture(SeatEntity p_110775_1_) {
			return null;
		}
	}

	@Override
	public void writeSpawnData(PacketByteBuf buffer) {}

	@Override
	public void readSpawnData(PacketByteBuf additionalData) {}
}
