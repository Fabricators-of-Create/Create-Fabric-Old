package com.simibubi.create.content.curiosities.symmetry;

/*public class SymmetryEffectPacket extends SimplePacketBase {

	private BlockPos mirror;
	private List<BlockPos> positions;

	public SymmetryEffectPacket(BlockPos mirror, List<BlockPos> positions) {
		this.mirror = mirror;
		this.positions = positions;
	}

	public SymmetryEffectPacket(PacketByteBuf buffer) {
		mirror = buffer.readBlockPos();
		int amt = buffer.readInt();
		positions = new ArrayList<>(amt);
		for (int i = 0; i < amt; i++) {
			positions.add(buffer.readBlockPos());
		}
	}

	public void write(PacketByteBuf buffer) {
		buffer.writeBlockPos(mirror);
		buffer.writeInt(positions.size());
		for (BlockPos blockPos : positions) {
			buffer.writeBlockPos(blockPos);
		}
	}

	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			if (MinecraftClient.getInstance().player.getPos().distanceTo(Vec3d.of(mirror)) > 100)
				return;
			for (BlockPos to : positions)
				SymmetryHandler.drawEffect(mirror, to);
		}));
		ctx.get().setPacketHandled(true);
	}

}
*/