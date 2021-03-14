package com.simibubi.create.content.contraptions.goggles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.simibubi.create.AllItems;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.components.structureMovement.DisplayAssemblyExceptionsProvider;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBox;
import com.simibubi.create.foundation.gui.GuiGameElement;
import com.simibubi.create.foundation.utility.outliner.Outline;
import com.simibubi.create.foundation.utility.outliner.Outliner;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class GoggleOverlayRenderer {

	private static final Map<Object, Outliner.OutlineEntry> outlines = CreateClient.outliner.getOutlines();

	public static void lookingAtBlocksThroughGogglesShowsTooltip(MatrixStack matrixStack, float v) {
		HitResult objectMouseOver = MinecraftClient.getInstance().crosshairTarget;
		if (!(objectMouseOver instanceof BlockHitResult))
			return;

		for (Outliner.OutlineEntry entry : outlines.values()) {
			if (!entry.isAlive())
				continue;
			Outline outline = entry.getOutline();
			if (outline instanceof ValueBox && !((ValueBox) outline).isPassive) {
				return;
			}
		}

		BlockHitResult result = (BlockHitResult) objectMouseOver;
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientWorld world = mc.world;
		BlockPos pos = result.getBlockPos();
		ItemStack headSlot = mc.player.getEquippedStack(EquipmentSlot.HEAD);
		BlockEntity te = world.getBlockEntity(pos);

		boolean wearingGoggles = headSlot.isItemEqualIgnoreDamage(AllItems.GOGGLES.getDefaultStack());

		boolean hasGoggleInformation = te instanceof GoggleInformationProvider;
		boolean hasHoveringInformation = te instanceof HoveringInformationProvider;

		boolean goggleAddedInformation = false;
		boolean hoverAddedInformation = false;

		List<Text> tooltip = new ArrayList<>();

		if (hasGoggleInformation && wearingGoggles) {
			GoggleInformationProvider gte = (GoggleInformationProvider) te;
			goggleAddedInformation = gte.addToGoggleTooltip(tooltip, mc.player.isSneaking());
		}

		if (hasHoveringInformation) {
			if (!tooltip.isEmpty())
				tooltip.add(LiteralText.EMPTY);
			HoveringInformationProvider hte = (HoveringInformationProvider) te;
			hoverAddedInformation = hte.addToTooltip(tooltip, mc.player.isSneaking());

			if (goggleAddedInformation && !hoverAddedInformation)
				tooltip.remove(tooltip.size() - 1);
		}

		if (te instanceof DisplayAssemblyExceptionsProvider) {
			boolean exceptionAdded = ((DisplayAssemblyExceptionsProvider) te).addExceptionToTooltip(tooltip);
			if (exceptionAdded) {
				hasHoveringInformation = true;
				hoverAddedInformation = true;
			}
		}

		// break early if goggle or hover returned false when present
		if ((hasGoggleInformation && !goggleAddedInformation) && (hasHoveringInformation && !hoverAddedInformation))
			return;

		// check for piston poles if goggles are worn
		/*BlockState state = world.getBlockState(pos);
		 if (wearingGoggles && AllBlocks.PISTON_EXTENSION_POLE.has(state)) {
		 Direction[] directions = Iterate.directionsInAxis(state.get(PistonExtensionPoleBlock.FACING)
		 .getAxis());
		 int poles = 1;
		 boolean pistonFound = false;
		 for (Direction dir : directions) {
		 int attachedPoles = PistonExtensionPoleBlock.PlacementHelper.get().attachedPoles(world, pos, dir);
		 poles += attachedPoles;
		 pistonFound |= world.getBlockState(pos.offset(dir, attachedPoles + 1))
		 .getBlock() instanceof MechanicalPistonBlock;
		 }

		 if (!pistonFound)
		 return;
		 if (!tooltip.isEmpty())
		 tooltip.add("");

		 tooltip.add(spacing + Lang.translate("gui.goggles.pole_length") + " " + poles);
		 }*/

		if (tooltip.isEmpty())
			return;

		RenderSystem.pushMatrix();
		Screen tooltipScreen = new TooltipScreen(null);
		tooltipScreen.init(mc, mc.getWindow()
				.getScaledWidth(),
			mc.getWindow()
				.getScaledHeight());
		int posX = tooltipScreen.width / 2 + 20; /** AllConfigs.CLIENT.overlayOffsetX.get(); TODO CONFIG*/
		int posY = tooltipScreen.height / 2 + 0; /** AllConfigs.CLIENT.overlayOffsetY.get(); TODO CONFIG*/

		tooltipScreen.renderTooltip(matrixStack, tooltip, posX, posY);

		ItemStack item = AllItems.GOGGLES.getDefaultStack();

		GuiGameElement.of(item)
			.at(posX + 10, posY - 16)
			.render();
		RenderSystem.popMatrix();
	}

	private static final class TooltipScreen extends Screen {
		private TooltipScreen(Text p_i51108_1_) {
			super(p_i51108_1_);
		}

		@Override
		public void init(MinecraftClient mc, int width, int height) {
			this.client = mc;
			this.itemRenderer = mc.getItemRenderer();
			this.textRenderer = mc.textRenderer;
			this.width = width;
			this.height = height;
		}
	}

}
