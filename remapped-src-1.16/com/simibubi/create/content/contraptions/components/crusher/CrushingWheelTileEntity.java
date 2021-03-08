package com.simibubi.create.content.contraptions.components.crusher;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CrushingWheelTileEntity extends KineticTileEntity {

	public static DamageSource damageSource = new DamageSource("create.crush").setBypassesArmor()
			.setScaledWithDifficulty();

	public CrushingWheelTileEntity(BlockEntityType<? extends CrushingWheelTileEntity> type) {
		super(type);
		setLazyTickRate(20);
	}

	@Override
	public void onSpeedChanged(float prevSpeed) {
		super.onSpeedChanged(prevSpeed);
		fixControllers();
	}

	public void fixControllers() {
		for (Direction d : Iterate.directions)
			((CrushingWheelBlock) getCachedState().getBlock()).updateControllers(getCachedState(), getWorld(), getPos(),
					d);
	}

	@Override
	public Box makeRenderBoundingBox() {
		return new Box(pos).expand(1);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		fixControllers();
	}

	@SubscribeEvent
	public static void crushingIsFortunate(LootingLevelEvent event) {
		if (event.getDamageSource() != damageSource)
			return;
		event.setLootingLevel(2);
	}

	@SubscribeEvent
	public static void crushingTeleportsEntities(LivingDeathEvent event) {
		if (event.getSource() != damageSource)
			return;
		event.getEntity().setPos(event.getEntity().getX(), Math.floor(event.getEntity().getY()) - .5f, event.getEntity().getZ());
	}

}
