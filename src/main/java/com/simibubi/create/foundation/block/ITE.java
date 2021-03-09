package com.simibubi.create.foundation.block;

import java.util.Optional;
import java.util.function.Consumer;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;

public interface ITE<T extends BlockEntity> {

	Class<T> getTileEntityClass();

	default void withTileEntityDo(World world, BlockPos pos, Consumer<T> action) {
		try {
			action.accept(getTileEntity(world, pos));
		} catch (TileEntityException e) {}
	}
	
	default Optional<T> getTileEntityOptional(World world, BlockPos pos) {
		try {
			return Optional.of(getTileEntity(world, pos));
		} catch (TileEntityException e) {}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	default T getTileEntity(WorldView worldIn, BlockPos pos) throws TileEntityException {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		Class<T> expectedClass = getTileEntityClass();

		World world = null;
		if (worldIn instanceof World)
			world = (World) worldIn;

		if (tileEntity == null)
			throw new MissingTileEntityException(world, pos, expectedClass);
		if (!expectedClass.isInstance(tileEntity))
			throw new InvalidTileEntityException(world, pos, expectedClass, tileEntity.getClass());

		return (T) tileEntity;
	}

	static class TileEntityException extends Throwable {
		private static final long serialVersionUID = 1L;

		public TileEntityException(World world, BlockPos pos, Class<?> teClass) {
			super(makeBaseMessage(world, pos, teClass));
		}

		public TileEntityException(String message) {
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

	static class MissingTileEntityException extends TileEntityException {
		private static final long serialVersionUID = 1L;

		public MissingTileEntityException(World world, BlockPos pos, Class<?> teClass) {
			super("Missing TileEntity: " + makeBaseMessage(world, pos, teClass));
		}

	}

	static class InvalidTileEntityException extends TileEntityException {
		private static final long serialVersionUID = 1L;

		public InvalidTileEntityException(World world, BlockPos pos, Class<?> expectedTeClass, Class<?> foundTeClass) {
			super("Wrong TileEntity: " + makeBaseMessage(world, pos, expectedTeClass) + ", found "
					+ foundTeClass.getSimpleName());
		}
	}

	static void report(TileEntityException e) {
		/*TODO: FIX THIS if (AllConfigs.COMMON.logTeErrors.get())
			Create.logger.debug("TileEntityException thrown!", e);*/
	}

}
