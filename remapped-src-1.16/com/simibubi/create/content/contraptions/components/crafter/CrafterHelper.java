package com.simibubi.create.content.contraptions.components.crafter;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class CrafterHelper {

	public static MechanicalCrafterTileEntity getCrafter(BlockRenderView reader, BlockPos pos) {
		BlockEntity te = reader.getBlockEntity(pos);
		if (!(te instanceof MechanicalCrafterTileEntity))
			return null;
		return (MechanicalCrafterTileEntity) te;
	}

	public static ConnectedInputHandler.ConnectedInput getInput(BlockRenderView reader, BlockPos pos) {
		MechanicalCrafterTileEntity crafter = getCrafter(reader, pos);
		return crafter == null ? null : crafter.input;
	}

}
