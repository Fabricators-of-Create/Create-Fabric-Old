package com.simibubi.create.content.contraptions.base;

import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.Create;
import com.simibubi.create.CreateClient;
import com.simibubi.create.content.contraptions.KineticNetwork;
import com.simibubi.create.content.contraptions.RotationPropagator;
import com.simibubi.create.content.contraptions.base.Rotating.SpeedLevel;
import com.simibubi.create.content.contraptions.base.Rotating.StressImpact;
import com.simibubi.create.content.contraptions.goggles.GoggleInformationProvider;
import com.simibubi.create.content.contraptions.goggles.HoveringInformationProvider;
import com.simibubi.create.foundation.block.entity.BlockEntityBehaviour;
import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.render.backend.FastRenderDispatcher;
import com.simibubi.create.foundation.render.backend.instancing.InstanceRendered;
import com.simibubi.create.foundation.utility.Lang;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.world.World;

public abstract class KineticBlockEntity extends SmartBlockEntity
	implements GoggleInformationProvider, HoveringInformationProvider, InstanceRendered {

	public @Nullable Long network;
	public @Nullable BlockPos source;
	public boolean networkDirty;
	public boolean updateSpeed;
	public boolean preventSpeedUpdate;

	protected KineticEffectHandler effects;
	protected float speed;
	protected float capacity;
	protected float stress;
	protected boolean overStressed;
	protected boolean wasMoved;

	private int flickerTally;
	private int networkSize;
	private int validationCountdown;
	private float lastStressApplied;
	private float lastCapacityProvided;

	public KineticBlockEntity(BlockEntityType<?> typeIn) {
		super(typeIn);
		effects = new KineticEffectHandler(this);
		updateSpeed = true;
	}

	@Override
	public void initialize() {
		if (!world.isClient && hasNetwork()) {
			KineticNetwork network = getOrCreateNetwork();
			if (!network.initialized)
				network.initFromTE(capacity, stress, networkSize);
			network.addSilently(this, lastCapacityProvided, lastStressApplied);
		}

		super.initialize();

		onLoad();
	}

	@Override
	public void tick() {
		if (!world.isClient && needsSpeedUpdate())
			attachKinetics();

		super.tick();
		effects.tick();

		if (world.isClient)
			return;

		if (validationCountdown-- <= 0) {
			validationCountdown = 20; //AllConfigs.SERVER.kinetics.kineticValidationFrequency.get();
			validateKinetics();
		}

		if (getFlickerScore() > 0)
			flickerTally = getFlickerScore() - 1;

		if (networkDirty) {
			if (hasNetwork())
				getOrCreateNetwork().updateNetwork();
			networkDirty = false;
		}
	}

	private void validateKinetics() {
		if (hasSource()) {
			if (!hasNetwork()) {
				removeSource();
				return;
			}

			if (!world.canSetBlock(source))
				return;

			BlockEntity tileEntity = world.getBlockEntity(source);
			KineticBlockEntity sourceTe =
				tileEntity instanceof KineticBlockEntity ? (KineticBlockEntity) tileEntity : null;
			if (sourceTe == null || sourceTe.speed == 0) {
				removeSource();
				detachKinetics();
				return;
			}

			return;
		}

		if (speed != 0) {
			if (getGeneratedSpeed() == 0)
				speed = 0;
		}
	}

	public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
		networkDirty = false;
		this.capacity = maxStress;
		this.stress = currentStress;
		this.networkSize = networkSize;
		boolean overStressed = maxStress < currentStress && StressImpact.isEnabled();

		if (overStressed != this.overStressed) {
			float prevSpeed = getSpeed();
			this.overStressed = overStressed;
			onSpeedChanged(prevSpeed);
			sendData();
		}
	}

	public float calculateAddedStressCapacity() {
		float capacity = 0; //(float) AllConfigs.SERVER.kinetics.stressValues.getCapacityOf(getStressConfigKey());
		this.lastCapacityProvided = capacity;
		return capacity;
	}

	protected Block getStressConfigKey() {
		return getCachedState().getBlock();
	}

	public float calculateStressApplied() {
		float impact = 0; //(float) AllConfigs.SERVER.kinetics.stressValues.getImpactOf(getCachedState().getBlock());
		this.lastStressApplied = impact;
		return impact;
	}

	public void onSpeedChanged(float previousSpeed) {
		boolean fromOrToZero = (previousSpeed == 0) != (getSpeed() == 0);
		boolean directionSwap = !fromOrToZero && Math.signum(previousSpeed) != Math.signum(getSpeed());
		if (fromOrToZero || directionSwap)
			flickerTally = getFlickerScore() + 5;
	}

	@Override
	public void markRemoved() {
		if (!world.isClient) {
			if (hasNetwork())
				getOrCreateNetwork().remove(this);
			detachKinetics();
		}
		super.markRemoved();
	}

	@Override
	protected void toTag(CompoundTag compound, boolean clientPacket) {
		compound.putFloat("Speed", speed);

		if (needsSpeedUpdate())
			compound.putBoolean("NeedsSpeedUpdate", true);

		if (hasSource())
			compound.put("Source", NbtHelper.fromBlockPos(source));

		if (hasNetwork()) {
			CompoundTag networkTag = new CompoundTag();
			networkTag.putLong("Id", this.network);
			networkTag.putFloat("Stress", stress);
			networkTag.putFloat("Capacity", capacity);
			networkTag.putInt("Size", networkSize);

			if (lastStressApplied != 0)
				networkTag.putFloat("AddedStress", lastStressApplied);
			if (lastCapacityProvided != 0)
				networkTag.putFloat("AddedCapacity", lastCapacityProvided);

			compound.put("Network", networkTag);
		}

		super.toTag(compound, clientPacket);
	}

	public boolean needsSpeedUpdate() {
		return updateSpeed;
	}

	@Override
	protected void fromTag(BlockState state, CompoundTag compound, boolean clientPacket) {
		boolean overStressedBefore = overStressed;
		clearKineticInformation();

		// DO NOT READ kinetic information when placed after movement
		if (wasMoved) {
			super.fromTag(state, compound, clientPacket);
			return;
		}

		speed = compound.getFloat("Speed");

		if (compound.contains("Source"))
			source = NbtHelper.toBlockPos(compound.getCompound("Source"));

		if (compound.contains("Network")) {
			CompoundTag networkTag = compound.getCompound("Network");
			network = networkTag.getLong("Id");
			stress = networkTag.getFloat("Stress");
			capacity = networkTag.getFloat("Capacity");
			networkSize = networkTag.getInt("Size");
			lastStressApplied = networkTag.getFloat("AddedStress");
			lastCapacityProvided = networkTag.getFloat("AddedCapacity");
			overStressed = capacity < stress && StressImpact.isEnabled();
		}

		super.fromTag(state, compound, clientPacket);

		if (clientPacket && overStressedBefore != overStressed && speed != 0)
			effects.triggerOverStressedEffect();

		if (clientPacket)
			FastRenderDispatcher.enqueueUpdate(this);
	}

	public float getGeneratedSpeed() {
		return 0;
	}

	public boolean isSource() {
		return getGeneratedSpeed() != 0;
	}

	public float getSpeed() {
		if (overStressed)
			return 0;
		return getTheoreticalSpeed();
	}

	public float getTheoreticalSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public boolean hasSource() {
		return source != null;
	}

	public void setSource(BlockPos source) {
		this.source = source;
		if (world == null || world.isClient)
			return;

		BlockEntity tileEntity = world.getBlockEntity(source);
		if (!(tileEntity instanceof KineticBlockEntity)) {
			removeSource();
			return;
		}

		KineticBlockEntity sourceTe = (KineticBlockEntity) tileEntity;
		setNetwork(sourceTe.network);
	}

	public void removeSource() {
		float prevSpeed = getSpeed();

		speed = 0;
		source = null;
		setNetwork(null);

		onSpeedChanged(prevSpeed);
	}

	public void setNetwork(@Nullable Long networkIn) {
		if (network == networkIn)
			return;
		if (network != null)
			getOrCreateNetwork().remove(this);

		network = networkIn;

		if (networkIn == null)
			return;

		network = networkIn;
		KineticNetwork network = getOrCreateNetwork();
		network.initialized = true;
		network.add(this);
	}

	public KineticNetwork getOrCreateNetwork() {
		return Create.torquePropagator.getOrCreateNetworkFor(this);
	}

	public boolean hasNetwork() {
		return network != null;
	}

	public void attachKinetics() {
		updateSpeed = false;
		RotationPropagator.handleAdded(world, pos, this);
	}

	public void detachKinetics() {
		RotationPropagator.handleRemoved(world, pos, this);
	}

	public boolean isSpeedRequirementFulfilled() {
		BlockState state = getCachedState();
		if (!(getCachedState().getBlock() instanceof Rotating))
			return true;
		Rotating def = (Rotating) state.getBlock();
		SpeedLevel minimumRequiredSpeedLevel = def.getMinimumRequiredSpeedLevel();
		if (minimumRequiredSpeedLevel == null)
			return true;
		if (minimumRequiredSpeedLevel == SpeedLevel.MEDIUM)
			return Math.abs(getSpeed()) >= 64; //AllConfigs.SERVER.kinetics.mediumSpeed.get();
		if (minimumRequiredSpeedLevel == SpeedLevel.FAST)
			return Math.abs(getSpeed()) >= 128; //AllConfigs.SERVER.kinetics.fastSpeed.get();
		return true;
	}

	public static void switchToBlockState(World world, BlockPos pos, BlockState state) {
		if (world.isClient)
			return;

		BlockEntity tileEntityIn = world.getBlockEntity(pos);
		BlockState currentState = world.getBlockState(pos);
		boolean isKinetic = tileEntityIn instanceof KineticBlockEntity;

		if (currentState == state)
			return;
		if (tileEntityIn == null || !isKinetic) {
			world.setBlockState(pos, state, 3);
			return;
		}

		KineticBlockEntity tileEntity = (KineticBlockEntity) tileEntityIn;
		if (state.getBlock() instanceof KineticBlock
			&& !((KineticBlock) state.getBlock()).areStatesKineticallyEquivalent(currentState, state)) {
			if (tileEntity.hasNetwork())
				tileEntity.getOrCreateNetwork()
					.remove(tileEntity);
			tileEntity.detachKinetics();
			tileEntity.removeSource();
		}

		world.setBlockState(pos, state, 3);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

	@Override
	public boolean addToTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
		boolean notFastEnough = !isSpeedRequirementFulfilled() && getSpeed() != 0;

		if (overStressed/* && AllConfigs.CLIENT.enableOverstressedTooltip.get()*/) {
			tooltip.add(componentSpacing.copy().append(Lang.translate("gui.stressometer.overstressed").formatted(GOLD)));
			Text hint = Lang.translate("gui.contraptions.network_overstressed");
			List<Text> cutString = TooltipHelper.cutTextComponent(hint, GRAY, Formatting.WHITE);
			for (int i = 0; i < cutString.size(); i++)
				tooltip.add(componentSpacing.copy().append(cutString.get(i)));
			return true;
		}

		if (notFastEnough) {
			tooltip.add(componentSpacing.copy().append(Lang.translate("tooltip.speedRequirement").formatted(GOLD)));
			Text hint = Lang.translate("gui.contraptions.not_fast_enough", I18n.translate(getCachedState().getBlock()
				.getTranslationKey()));
			List<Text> cutString = TooltipHelper.cutTextComponent(hint, GRAY, Formatting.WHITE);
			for (int i = 0; i < cutString.size(); i++)
				tooltip.add(componentSpacing.copy().append(cutString.get(i)));
			return true;
		}

		return false;
	}

	@Override
	public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
		boolean added = false;
		float stressAtBase = calculateStressApplied();

		if (calculateStressApplied() != 0 && StressImpact.isEnabled()) {
			tooltip.add(componentSpacing.copy().append(Lang.translate("gui.goggles.kinetic_stats")));
			tooltip.add(componentSpacing.copy().append(Lang.translate("tooltip.stressImpact").formatted(Formatting.GRAY)));

			float stressTotal = stressAtBase * Math.abs(getTheoreticalSpeed());

			tooltip.add(componentSpacing.copy().append(new LiteralText(" " + GoggleInformationProvider.format(stressTotal))
				.append(Lang.translate("generic.unit.stress")).append(" ").formatted(Formatting.AQUA)).append(Lang.translate("gui.goggles.at_current_speed").formatted(Formatting.DARK_GRAY)));

			added = true;
		}

		return added;

	}

	public void clearKineticInformation() {
		speed = 0;
		source = null;
		network = null;
		overStressed = false;
		stress = 0;
		capacity = 0;
		lastStressApplied = 0;
		lastCapacityProvided = 0;
	}

	public void warnOfMovement() {
		wasMoved = true;
	}

	public int getFlickerScore() {
		return flickerTally;
	}

	public static float convertToDirection(float axisSpeed, Direction d) {
		return d.getDirection() == AxisDirection.POSITIVE ? axisSpeed : -axisSpeed;
	}

	public boolean isOverStressed() {
		return overStressed;
	}

	// Custom Propagation

	/**
	 * Specify ratio of transferred rotation from this kinetic component to a
	 * specific other.
	 *
	 * @param target           other Kinetic TE to transfer to
	 * @param stateFrom        this TE's blockstate
	 * @param stateTo          other TE's blockstate
	 * @param diff             difference in position (to.pos - from.pos)
	 * @param connectedViaAxes whether these kinetic blocks are connected via mutual
	 *                         IRotate.hasShaftTowards()
	 * @param connectedViaCogs whether these kinetic blocks are connected via mutual
	 *                         IRotate.hasIntegratedCogwheel()
	 * @return factor of rotation speed from this TE to other. 0 if no rotation is
	 *         transferred, or the standard rules apply (integrated shafts/cogs)
	 */
	public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff,
		boolean connectedViaAxes, boolean connectedViaCogs) {
		return 0;
	}

	/**
	 * Specify additional locations the rotation propagator should look for
	 * potentially connected components. Neighbour list contains offset positions in
	 * all 6 directions by default.
	 *
	 * @param block
	 * @param state
	 * @param neighbours
	 * @return
	 */
	public List<BlockPos> addPropagationLocations(Rotating block, BlockState state, List<BlockPos> neighbours) {
		if (!canPropagateDiagonally(block, state))
			return neighbours;

		Axis axis = block.getRotationAxis(state);
		BlockPos.stream(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
			.forEach(offset -> {
				if (axis.choose(offset.getX(), offset.getY(), offset.getZ()) != 0)
					return;
				if (offset.getSquaredDistance(0, 0, 0, false) != BlockPos.ORIGIN.getSquaredDistance(1, 1, 0, false))
					return;
				neighbours.add(pos.add(offset));
			});
		return neighbours;
	}

	/**
	 * Specify whether this component can propagate speed to the other in any
	 * circumstance. Shaft and cogwheel connections are already handled by internal
	 * logic. Does not have to be specified on both ends, it is assumed that this
	 * relation is symmetrical.
	 *
	 * @param other
	 * @param state
	 * @param otherState
	 * @return true if this and the other component should check their propagation
	 *         factor and are not already connected via integrated cogs or shafts
	 */
	public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
		return false;
	}

	protected boolean canPropagateDiagonally(Rotating block, BlockState state) {
		return block.hasIntegratedCogwheel(world, pos, state);
	}

//	@Override
	public void onLoad() {
//		super.onLoad();
		if (world != null && world.isClient)
			CreateClient.kineticRenderer.add(this);
	}

//	@Override
	public void onChunkUnloaded() {
		if (world != null && world.isClient)
			CreateClient.kineticRenderer.remove(this);
	}

//	@Override
	public void requestModelDataUpdate() {
//		super.requestModelDataUpdate();
		if (!this.removed) {
			FastRenderDispatcher.enqueueUpdate(this);
		}
	}

	@Override
	public void onChunkLightUpdate() {
		CreateClient.kineticRenderer.onLightUpdate(this);
	}

	protected Box cachedBoundingBox;
	@Environment(EnvType.CLIENT)
	public Box getRenderBoundingBox() {
		if (cachedBoundingBox == null) {
			cachedBoundingBox = makeRenderBoundingBox();
		}
		return cachedBoundingBox;
	}

	protected Box makeRenderBoundingBox() {
		return new Box(0, 0, 0, 1, 1, 1); //super.getRenderBoundingBox();
	}
}
