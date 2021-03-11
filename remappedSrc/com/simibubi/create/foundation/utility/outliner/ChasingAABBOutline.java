package com.simibubi.create.foundation.utility.outliner;

import com.simibubi.create.foundation.renderState.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class ChasingAABBOutline extends AABBOutline {

	Box targetBB;
	Box prevBB;

	public ChasingAABBOutline(Box bb) {
		super(bb);
		prevBB = bb.expand(0);
		targetBB = bb.expand(0);
	}

	public void target(Box target) {
		targetBB = target;
	}

	@Override
	public void tick() {
		prevBB = bb;
		setBounds(interpolateBBs(bb, targetBB, .5f));
	}

	@Override
	public void render(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		renderBB(ms, buffer, interpolateBBs(prevBB, bb, AnimationTickHolder.getPartialTicks()));
	}

	private static Box interpolateBBs(Box current, Box target, float pt) {
		return new Box(MathHelper.lerp(pt, current.minX, target.minX),
			MathHelper.lerp(pt, current.minY, target.minY), MathHelper.lerp(pt, current.minZ, target.minZ),
			MathHelper.lerp(pt, current.maxX, target.maxX), MathHelper.lerp(pt, current.maxY, target.maxY),
			MathHelper.lerp(pt, current.maxZ, target.maxZ));
	}

}
