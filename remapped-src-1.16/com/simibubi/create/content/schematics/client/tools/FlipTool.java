package com.simibubi.create.content.schematics.client.tools;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.foundation.renderState.SuperRenderTypeBuffer;
import com.simibubi.create.foundation.utility.outliner.AABBOutline;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.Vec3d;

public class FlipTool extends PlacementToolBase {

	private AABBOutline outline = new AABBOutline(new Box(BlockPos.ORIGIN));

	@Override
	public void init() {
		super.init();
		renderSelectedFace = false;
	}

	@Override
	public boolean handleRightClick() {
		mirror();
		return true;
	}

	@Override
	public boolean handleMouseWheel(double delta) {
		mirror();
		return true;
	}

	@Override
	public void updateSelection() {
		super.updateSelection();
	}

	private void mirror() {
		if (schematicSelected && selectedFace.getAxis()
			.isHorizontal()) {
			schematicHandler.getTransformation()
				.flip(selectedFace.getAxis());
			schematicHandler.markDirty();
		}
	}

	@Override
	public void renderOnSchematic(MatrixStack ms, SuperRenderTypeBuffer buffer) {
		if (!schematicSelected || !selectedFace.getAxis()
			.isHorizontal()) {
			super.renderOnSchematic(ms, buffer);
			return;
		}

		Direction facing = selectedFace.rotateYClockwise();
		Box bounds = schematicHandler.getBounds();

		Vec3d directionVec = Vec3d.of(Direction.get(AxisDirection.POSITIVE, facing.getAxis())
			.getVector());
		Vec3d boundsSize = new Vec3d(bounds.getXLength(), bounds.getYLength(), bounds.getZLength());
		Vec3d vec = boundsSize.multiply(directionVec);
		bounds = bounds.shrink(vec.x, vec.y, vec.z)
			.expand(1 - directionVec.x, 1 - directionVec.y, 1 - directionVec.z);
		bounds = bounds.offset(directionVec.multiply(.5f)
			.multiply(boundsSize));
		
		outline.setBounds(bounds);
		AllSpecialTextures tex = AllSpecialTextures.CHECKERED;
		outline.getParams()
			.lineWidth(1 / 16f)
			.disableNormals()
			.colored(0xdddddd)
			.withFaceTextures(tex, tex);
		outline.render(ms, buffer);
		
		super.renderOnSchematic(ms, buffer);
	}

}
