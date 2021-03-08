package com.simibubi.create.content.logistics.block.diodes;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import net.minecraft.block.AbstractRedstoneGateBlock;

public abstract class AbstractDiodeBlock extends AbstractRedstoneGateBlock implements IWrenchable {

	public AbstractDiodeBlock(Settings builder) {
		super(builder);
	}
	
}
