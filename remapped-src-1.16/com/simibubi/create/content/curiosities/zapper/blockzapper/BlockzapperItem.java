package com.simibubi.create.content.curiosities.zapper.blockzapper;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.curiosities.zapper.PlacementPatterns;
import com.simibubi.create.content.curiosities.zapper.ZapperInteractionHandler;
import com.simibubi.create.content.curiosities.zapper.ZapperItem;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.gui.ScreenOpener;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.ItemDescription.Palette;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.event.ForgeEventFactory;

public class BlockzapperItem extends ZapperItem {

	public BlockzapperItem(Settings properties) {
		super(properties);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flagIn) {
		super.appendTooltip(stack, worldIn, tooltip, flagIn);
		Palette palette = Palette.Purple;
		if (Screen.hasShiftDown()) {
			ItemDescription.add(tooltip, Lang.translate("blockzapper.componentUpgrades").formatted(palette.color));

			for (Components c : Components.values()) {
				ComponentTier tier = getTier(c, stack);
				Text componentName =
					Lang.translate("blockzapper.component." + Lang.asId(c.name())).formatted(Formatting.GRAY);
				Text tierName = Lang.translate("blockzapper.componentTier." + Lang.asId(tier.name())).formatted(tier.color);
				ItemDescription.add(tooltip, new LiteralText("> ").append(componentName).append(": ").append(tierName));
			}
		}
	}

	@Override
	public void appendStacks(ItemGroup group, DefaultedList<ItemStack> items) {
		if (group != Create.baseCreativeTab && group != ItemGroup.SEARCH)
			return;
		
		ItemStack gunWithoutStuff = new ItemStack(this);
		items.add(gunWithoutStuff);

		ItemStack gunWithGoldStuff = new ItemStack(this);
		for (Components c : Components.values())
			setTier(c, ComponentTier.Brass, gunWithGoldStuff);
		items.add(gunWithGoldStuff);

		ItemStack gunWithPurpurStuff = new ItemStack(this);
		for (Components c : Components.values())
			setTier(c, ComponentTier.Chromatic, gunWithPurpurStuff);
		items.add(gunWithPurpurStuff);
	}

	@Override
	protected boolean activate(World world, PlayerEntity player, ItemStack stack, BlockState selectedState,
		BlockHitResult raytrace, CompoundTag data) {
		CompoundTag nbt = stack.getOrCreateTag();
		boolean replace = nbt.contains("Replace") && nbt.getBoolean("Replace");

		List<BlockPos> selectedBlocks = getSelectedBlocks(stack, world, player);
		PlacementPatterns.applyPattern(selectedBlocks, stack);
		Direction face = raytrace.getSide();

		for (BlockPos placed : selectedBlocks) {
			if (world.getBlockState(placed) == selectedState)
				continue;
			if (!selectedState.canPlaceAt(world, placed))
				continue;
			if (!player.isCreative() && !canBreak(stack, world.getBlockState(placed), world, placed,player))
				continue;
			if (!player.isCreative() && BlockHelper.findAndRemoveInInventory(selectedState, player, 1) == 0) {
				player.getItemCooldownManager()
					.set(stack.getItem(), 20);
				player.sendMessage( Lang.translate("blockzapper.empty").formatted(Formatting.RED), true);
				return false;
			}

			if (!player.isCreative() && replace)
				dropBlocks(world, player, stack, face, placed);

			BlockState state = selectedState;
			for (Direction updateDirection : Iterate.directions)
				state = state.getStateForNeighborUpdate(updateDirection,
					world.getBlockState(placed.offset(updateDirection)), world, placed, placed.offset(updateDirection));

			BlockSnapshot blocksnapshot = BlockSnapshot.create(world.getRegistryKey(), world, placed);
			FluidState FluidState = world.getFluidState(placed);
			world.setBlockState(placed, FluidState.getBlockState(), BlockFlags.UPDATE_NEIGHBORS);
			world.setBlockState(placed, state);

			if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
				blocksnapshot.restore(true, false);
				return false;
			}
			setTileData(world, placed, state, data, player);

			if (player instanceof ServerPlayerEntity && world instanceof ServerWorld) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
				Criteria.PLACED_BLOCK.trigger(serverPlayer, placed, new ItemStack(state.getBlock()));

				boolean fullyUpgraded = true;
				for (Components c : Components.values()) {
					if (getTier(c, stack) != ComponentTier.Chromatic) {
						fullyUpgraded = false;
						break;
					}
				}
				if (fullyUpgraded)
					AllTriggers.UPGRADED_ZAPPER.trigger(serverPlayer);
			}
		}
		for (BlockPos placed : selectedBlocks) {
			world.updateNeighbor(placed, selectedState.getBlock(), placed);
		}

		return true;
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (AllItems.BLOCKZAPPER.isIn(stack)) {
			CompoundTag nbt = stack.getOrCreateTag();
			if (!nbt.contains("Replace"))
				nbt.putBoolean("Replace", false);
			if (!nbt.contains("Pattern"))
				nbt.putString("Pattern", PlacementPatterns.Solid.name());
			if (!nbt.contains("SearchDiagonal"))
				nbt.putBoolean("SearchDiagonal", false);
			if (!nbt.contains("SearchMaterial"))
				nbt.putBoolean("SearchMaterial", false);
			if (!nbt.contains("SearchDistance"))
				nbt.putInt("SearchDistance", 1);
		}
	}

	@Override
	@Environment(EnvType.CLIENT)
	protected void openHandgunGUI(ItemStack handgun, boolean offhand) {
		ScreenOpener.open(new BlockzapperScreen(handgun, offhand));
	}

	public static List<BlockPos> getSelectedBlocks(ItemStack stack, World worldIn, PlayerEntity player) {
		List<BlockPos> list = new LinkedList<>();
		CompoundTag tag = stack.getTag();
		if (tag == null)
			return list;

		boolean searchDiagonals = tag.contains("SearchDiagonal") && tag.getBoolean("SearchDiagonal");
		boolean searchAcrossMaterials = tag.contains("SearchFuzzy") && tag.getBoolean("SearchFuzzy");
		boolean replace = tag.contains("Replace") && tag.getBoolean("Replace");
		int searchRange = tag.contains("SearchDistance") ? tag.getInt("SearchDistance") : 0;

		Set<BlockPos> visited = new HashSet<>();
		List<BlockPos> frontier = new LinkedList<>();

		Vec3d start = player.getPos()
			.add(0, player.getStandingEyeHeight(), 0);
		Vec3d range = player.getRotationVector()
			.multiply(ZapperInteractionHandler.getRange(stack));
		BlockHitResult raytrace = player.world
			.raycast(new RaycastContext(start, start.add(range), ShapeType.COLLIDER, FluidHandling.NONE, player));
		BlockPos pos = raytrace.getBlockPos()
			.toImmutable();

		if (pos == null)
			return list;

		BlockState state = worldIn.getBlockState(pos);
		Direction face = raytrace.getSide();
		List<BlockPos> offsets = new LinkedList<>();

		for (int x = -1; x <= 1; x++)
			for (int y = -1; y <= 1; y++)
				for (int z = -1; z <= 1; z++)
					if (Math.abs(x) + Math.abs(y) + Math.abs(z) < 2 || searchDiagonals)
						if (face.getAxis()
							.choose(x, y, z) == 0)
							offsets.add(new BlockPos(x, y, z));

		BlockPos startPos = replace ? pos : pos.offset(face);
		frontier.add(startPos);

		while (!frontier.isEmpty()) {
			BlockPos currentPos = frontier.remove(0);
			if (visited.contains(currentPos))
				continue;
			visited.add(currentPos);
			if (!currentPos.isWithinDistance(startPos, searchRange))
				continue;

			// Replace Mode
			if (replace) {
				BlockState stateToReplace = worldIn.getBlockState(currentPos);
				BlockState stateAboveStateToReplace = worldIn.getBlockState(currentPos.offset(face));

				// Criteria
				if (stateToReplace.getHardness(worldIn, currentPos) == -1)
					continue;
				if (stateToReplace.getBlock() != state.getBlock() && !searchAcrossMaterials)
					continue;
				if (stateToReplace.getMaterial()
					.isReplaceable())
					continue;
				if (stateAboveStateToReplace.isOpaque())
					continue;
				list.add(currentPos);

				// Search adjacent spaces
				for (BlockPos offset : offsets)
					frontier.add(currentPos.add(offset));
				continue;
			}

			// Place Mode
			BlockState stateToPlaceAt = worldIn.getBlockState(currentPos);
			BlockState stateToPlaceOn = worldIn.getBlockState(currentPos.offset(face.getOpposite()));

			// Criteria
			if (stateToPlaceOn.getMaterial()
				.isReplaceable())
				continue;
			if (stateToPlaceOn.getBlock() != state.getBlock() && !searchAcrossMaterials)
				continue;
			if (!stateToPlaceAt.getMaterial()
				.isReplaceable())
				continue;
			list.add(currentPos);

			// Search adjacent spaces
			for (BlockPos offset : offsets)
				frontier.add(currentPos.add(offset));
			continue;
		}

		return list;
	}

	public static boolean canBreak(ItemStack stack, BlockState state, World world, BlockPos pos,PlayerEntity player) {
		ComponentTier tier = getTier(Components.Body, stack);
		float blockHardness = state.getHardness(world, pos);
		//If we can't change the block (e.g chunk protection)
		if (!isAllowedToPlace(world,pos,player)){
			return false;
		}
		if (blockHardness == -1)
			return false;
		if (tier == ComponentTier.None)
			return blockHardness < 3;
		if (tier == ComponentTier.Brass)
			return blockHardness < 6;
		if (tier == ComponentTier.Chromatic)
			return true;

		return false;
	}

	public static boolean isAllowedToPlace(World world, BlockPos pos,PlayerEntity player){
		BlockSnapshot blocksnapshot = BlockSnapshot.create(world.getRegistryKey(), world, pos);
		if (ForgeEventFactory.onBlockPlace(player, blocksnapshot, Direction.UP)) {
			return false;
		}
		return true;
	}

	public static int getMaxAoe(ItemStack stack) {
		ComponentTier tier = getTier(Components.Amplifier, stack);
		if (tier == ComponentTier.None)
			return 2;
		if (tier == ComponentTier.Brass)
			return 4;
		if (tier == ComponentTier.Chromatic)
			return 8;

		return 0;
	}

	@Override
	protected int getCooldownDelay(ItemStack stack) {
		return getCooldown(stack);
	}

	public static int getCooldown(ItemStack stack) {
		ComponentTier tier = getTier(Components.Accelerator, stack);
		if (tier == ComponentTier.None)
			return 10;
		if (tier == ComponentTier.Brass)
			return 6;
		if (tier == ComponentTier.Chromatic)
			return 2;

		return 20;
	}

	@Override
	protected int getZappingRange(ItemStack stack) {
		ComponentTier tier = getTier(Components.Scope, stack);
		if (tier == ComponentTier.None)
			return 15;
		if (tier == ComponentTier.Brass)
			return 30;
		if (tier == ComponentTier.Chromatic)
			return 100;

		return 0;
	}

	protected static void dropBlocks(World worldIn, PlayerEntity playerIn, ItemStack item, Direction face,
		BlockPos placed) {
		BlockEntity tileentity = worldIn.getBlockState(placed)
			.hasTileEntity() ? worldIn.getBlockEntity(placed) : null;

		if (getTier(Components.Retriever, item) == ComponentTier.None) {
			Block.dropStacks(worldIn.getBlockState(placed), worldIn, placed.offset(face), tileentity);
		}

		if (getTier(Components.Retriever, item) == ComponentTier.Brass)
			Block.dropStacks(worldIn.getBlockState(placed), worldIn, playerIn.getBlockPos(), tileentity);

		if (getTier(Components.Retriever, item) == ComponentTier.Chromatic)
			for (ItemStack stack : Block.getDroppedStacks(worldIn.getBlockState(placed), (ServerWorld) worldIn, placed,
				tileentity))
				if (!playerIn.inventory.insertStack(stack))
					Block.dropStack(worldIn, placed, stack);
	}

	public static ComponentTier getTier(Components component, ItemStack stack) {
		if (!stack.hasTag() || !stack.getTag()
			.contains(component.name()))
			stack.getOrCreateTag()
				.putString(component.name(), ComponentTier.None.name());
		return NBTHelper.readEnum(stack.getTag(), component.name(), ComponentTier.class);
	}

	public static void setTier(Components component, ComponentTier tier, ItemStack stack) {
		NBTHelper.writeEnum(stack.getOrCreateTag(), component.name(), tier);
	}

	public static enum ComponentTier {
		None(Formatting.DARK_GRAY), Brass(Formatting.GOLD), Chromatic(Formatting.LIGHT_PURPLE);

		public Formatting color;

		private ComponentTier(Formatting color) {
			this.color = color;
		}

	}

	public static enum Components {
		Body, Amplifier, Accelerator, Retriever, Scope
	}

}
