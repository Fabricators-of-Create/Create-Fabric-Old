package com.simibubi.create.foundation.block;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

public interface IBE<T extends BlockEntity> {

	static void report(BlockEntityException e) {
		/*TODO: FIX THIS if (AllConfigs.COMMON.logTeErrors.get())
			Create.logger.debug("BlockEntityException thrown!", e);*/
	}

	Class<T> getBlockEntityClass();

	default void withBlockEntityDo(World world, BlockPos pos, Consumer<T> action) {
		try {
			action.accept(getBlockEntity(world, pos));
		} catch (BlockEntityException ignored) {
		}
	}

	default Optional<T> getBlockEntityOptional(World world, BlockPos pos) {
		try {
			return Optional.of(getBlockEntity(world, pos));
		} catch (BlockEntityException ignored) {
		}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	default T getBlockEntity(WorldView worldView, BlockPos pos) throws BlockEntityException {
		BlockEntity blockEntity = worldView.getBlockEntity(pos);
		Class<T> expectedClass = getBlockEntityClass();

		World world = (worldView instanceof World) ? (World) worldView : null;

		if (blockEntity == null)
			throw new MissingBlockEntityException(world, pos, expectedClass);
		if (!expectedClass.isInstance(blockEntity))
			throw new InvalidBlockEntityException(world, pos, expectedClass, blockEntity.getClass());

		return (T) blockEntity;
	}

	class BlockEntityException extends Throwable {
		private static final long serialVersionUID = 1L;

		public BlockEntityException(World world, BlockPos pos, Class<?> teClass) {
			super(makeBaseMessage(world, pos, teClass));
		}

		public BlockEntityException(String message) {
			super(message);
			report(this);
		}

		static String makeBaseMessage(World world, BlockPos pos, Class<?> expectedTeClass) {
			return String.format("[%s] @(%d, %d, %d), expecting a %s", getDimensionName(world), pos.getX(), pos.getY(),
				pos.getZ(), expectedTeClass.getSimpleName());
		}

		static String getDimensionName(World world) {
			String notAvailable = "Dim N/A";
			if (world == null)
				return notAvailable;
			DimensionType dimension = world.getDimension();
			if (dimension == null)
				return notAvailable;
			Identifier registryName = world.getRegistryKey().getValue();
			if (registryName == null)
				return notAvailable;
			return registryName.toString();
		}
	}

	class MissingBlockEntityException extends BlockEntityException {
		private static final long serialVersionUID = 1L;

		public MissingBlockEntityException(World world, BlockPos pos, Class<?> teClass) {
			super("Missing BlockEntity: " + makeBaseMessage(world, pos, teClass));
		}

	}

	class InvalidBlockEntityException extends BlockEntityException {
		private static final long serialVersionUID = 1L;

		public InvalidBlockEntityException(World world, BlockPos pos, Class<?> expectedTeClass, Class<?> foundTeClass) {
			super("Wrong BlockEntity: " + makeBaseMessage(world, pos, expectedTeClass) + ", found "
				+ foundTeClass.getSimpleName());
		}
	}

}
