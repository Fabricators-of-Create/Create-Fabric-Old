package com.simibubi.create.foundation.block;

import java.util.Optional;
import java.util.function.Consumer;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.utility.WorldHelper;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;

public interface ITE<T extends BlockEntity> {

	Class<T> getTileEntityClass();

	default void withTileEntityDo(BlockView world, BlockPos pos, Consumer<T> action) {
		try {
			action.accept(getTileEntity(world, pos));
		} catch (TileEntityException e) {}
	}
	
	default Optional<T> getTileEntityOptional(BlockView world, BlockPos pos) {
		try {
			return Optional.of(getTileEntity(world, pos));
		} catch (TileEntityException e) {}
		return Optional.empty();
	}

	@SuppressWarnings("unchecked")
	default T getTileEntity(BlockView worldIn, BlockPos pos) throws TileEntityException {
		BlockEntity tileEntity = worldIn.getBlockEntity(pos);
		Class<T> expectedClass = getTileEntityClass();

		WorldAccess world = null;
		if (worldIn instanceof WorldAccess)
			world = (WorldAccess) worldIn;

		if (tileEntity == null)
			throw new MissingTileEntityException(world, pos, expectedClass);
		if (!expectedClass.isInstance(tileEntity))
			throw new InvalidTileEntityException(world, pos, expectedClass, tileEntity.getClass());

		return (T) tileEntity;
	}

	static class TileEntityException extends Throwable {
		private static final long serialVersionUID = 1L;

		public TileEntityException(WorldAccess world, BlockPos pos, Class<?> teClass) {
			super(makeBaseMessage(world, pos, teClass));
		}

		public TileEntityException(String message) {
			super(message);
			report(this);
		}

		static String makeBaseMessage(WorldAccess world, BlockPos pos, Class<?> expectedTeClass) {
			return String.format("[%s] @(%d, %d, %d), expecting a %s", getDimensionName(world), pos.getX(), pos.getY(),
					pos.getZ(), expectedTeClass.getSimpleName());
		}

		static String getDimensionName(WorldAccess world) {
			String notAvailable = "Dim N/A";
			if (world == null)
				return notAvailable;
			Identifier registryName = WorldHelper.getDimensionID(world);
			if (registryName == null)
				return notAvailable;
			return registryName.toString();
		}
	}

	static class MissingTileEntityException extends TileEntityException {
		private static final long serialVersionUID = 1L;

		public MissingTileEntityException(WorldAccess world, BlockPos pos, Class<?> teClass) {
			super("Missing TileEntity: " + makeBaseMessage(world, pos, teClass));
		}

	}

	static class InvalidTileEntityException extends TileEntityException {
		private static final long serialVersionUID = 1L;

		public InvalidTileEntityException(WorldAccess world, BlockPos pos, Class<?> expectedTeClass, Class<?> foundTeClass) {
			super("Wrong TileEntity: " + makeBaseMessage(world, pos, expectedTeClass) + ", found "
					+ foundTeClass.getSimpleName());
		}
	}

	static void report(TileEntityException e) {
		if (AllConfigs.COMMON.logTeErrors.get())
			Create.logger.debug("TileEntityException thrown!", e);
	}

}
