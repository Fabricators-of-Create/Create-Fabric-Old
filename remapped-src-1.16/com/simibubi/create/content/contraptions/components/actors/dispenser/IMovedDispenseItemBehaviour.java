package com.simibubi.create.content.contraptions.components.actors.dispenser;

import java.util.Random;

import com.simibubi.create.content.contraptions.components.structureMovement.MovementContext;

import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public interface IMovedDispenseItemBehaviour {

	static void initSpawneggs() {
		final IMovedDispenseItemBehaviour spawnEggDispenseBehaviour = new MovedDefaultDispenseItemBehaviour() {
			@Override
			protected ItemStack dispenseStack(ItemStack itemStack, MovementContext context, BlockPos pos,
				Vec3d facing) {
				if (!(itemStack.getItem() instanceof SpawnEggItem))
					return super.dispenseStack(itemStack, context, pos, facing);
				if (context.world instanceof ServerWorld) {
					EntityType<?> entityType = ((SpawnEggItem) itemStack.getItem()).getEntityType(itemStack.getTag());
					Entity spawnedEntity = entityType.spawnFromItemStack((ServerWorld) context.world, itemStack, null,
						pos.add(facing.x + .7, facing.y + .7, facing.z + .7), SpawnReason.DISPENSER, facing.y < .5,
						false);
					if (spawnedEntity != null)
						spawnedEntity.setVelocity(context.motion.multiply(2));
				}
				itemStack.decrement(1);
				return itemStack;
			}
		};

		for (SpawnEggItem spawneggitem : SpawnEggItem.getAll())
			DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(spawneggitem, spawnEggDispenseBehaviour);
	}

	static void init() {
		MovedProjectileDispenserBehaviour movedPotionDispenseItemBehaviour = new MovedProjectileDispenserBehaviour() {
			@Override
			protected ProjectileEntity getProjectileEntity(World world, double x, double y, double z,
				ItemStack itemStack) {
				return Util.make(new PotionEntity(world, x, y, z), (p_218411_1_) -> p_218411_1_.setItem(itemStack));
			}

			protected float getProjectileInaccuracy() {
				return super.getProjectileInaccuracy() * 0.5F;
			}

			protected float getProjectileVelocity() {
				return super.getProjectileVelocity() * .5F;
			}
		};

		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.SPLASH_POTION,
			movedPotionDispenseItemBehaviour);
		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.LINGERING_POTION,
			movedPotionDispenseItemBehaviour);

		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.TNT,
			new MovedDefaultDispenseItemBehaviour() {
				@Override
				protected ItemStack dispenseStack(ItemStack itemStack, MovementContext context, BlockPos pos,
					Vec3d facing) {
					double x = pos.getX() + facing.x * .7 + .5;
					double y = pos.getY() + facing.y * .7 + .5;
					double z = pos.getZ() + facing.z * .7 + .5;
					TntEntity tntentity = new TntEntity(context.world, x, y, z, null);
					tntentity.addVelocity(context.motion.x, context.motion.y, context.motion.z);
					context.world.spawnEntity(tntentity);
					context.world.playSound(null, tntentity.getX(), tntentity.getY(), tntentity.getZ(),
						SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
					itemStack.decrement(1);
					return itemStack;
				}
			});

		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.FIREWORK_ROCKET,
			new MovedDefaultDispenseItemBehaviour() {
				@Override
				protected ItemStack dispenseStack(ItemStack itemStack, MovementContext context, BlockPos pos,
					Vec3d facing) {
					double x = pos.getX() + facing.x * .7 + .5;
					double y = pos.getY() + facing.y * .7 + .5;
					double z = pos.getZ() + facing.z * .7 + .5;
					FireworkRocketEntity fireworkrocketentity =
						new FireworkRocketEntity(context.world, itemStack, x, y, z, true);
					fireworkrocketentity.setVelocity(facing.x, facing.y, facing.z, 0.5F, 1.0F);
					context.world.spawnEntity(fireworkrocketentity);
					itemStack.decrement(1);
					return itemStack;
				}

				@Override
				protected void playDispenseSound(WorldAccess world, BlockPos pos) {
					world.syncWorldEvent(1004, pos, 0);
				}
			});

		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.FIRE_CHARGE,
			new MovedDefaultDispenseItemBehaviour() {
				@Override
				protected void playDispenseSound(WorldAccess world, BlockPos pos) {
					world.syncWorldEvent(1018, pos, 0);
				}

				@Override
				protected ItemStack dispenseStack(ItemStack itemStack, MovementContext context, BlockPos pos,
					Vec3d facing) {
					Random random = context.world.random;
					double x = pos.getX() + facing.x * .7 + .5;
					double y = pos.getY() + facing.y * .7 + .5;
					double z = pos.getZ() + facing.z * .7 + .5;
					context.world.spawnEntity(Util.make(
						new SmallFireballEntity(context.world, x, y, z,
							random.nextGaussian() * 0.05D + facing.x + context.motion.x,
							random.nextGaussian() * 0.05D + facing.y + context.motion.y,
							random.nextGaussian() * 0.05D + facing.z + context.motion.z),
						(p_229425_1_) -> p_229425_1_.setItem(itemStack)));
					itemStack.decrement(1);
					return itemStack;
				}
			});

		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.GLASS_BOTTLE,
			new MovedOptionalDispenseBehaviour() {
				@Override
				protected ItemStack dispenseStack(ItemStack itemStack, MovementContext context, BlockPos pos,
					Vec3d facing) {
					this.successful = false;
					BlockPos interactAt = pos.offset(getClosestFacingDirection(facing));
					BlockState state = context.world.getBlockState(interactAt);
					Block block = state.getBlock();

					if (block.isIn(BlockTags.BEEHIVES) && state.get(BeehiveBlock.HONEY_LEVEL) >= 5) { 
						((BeehiveBlock) block).takeHoney(context.world, state, interactAt, null,
							BeehiveBlockEntity.BeeState.BEE_RELEASED);
						this.successful = true;
						return placeItemInInventory(itemStack, new ItemStack(Items.HONEY_BOTTLE), context, pos,
							facing);
					} else if (context.world.getFluidState(interactAt)
						.isIn(FluidTags.WATER)) {
						this.successful = true;
						return placeItemInInventory(itemStack,
							PotionUtil.setPotion(new ItemStack(Items.POTION), Potions.WATER), context, pos,
							facing);
					} else {
						return super.dispenseStack(itemStack, context, pos, facing);
					}
				}
			});

		DispenserMovementBehaviour.registerMovedDispenseItemBehaviour(Items.BUCKET,
			new MovedDefaultDispenseItemBehaviour() {
				@Override
				protected ItemStack dispenseStack(ItemStack itemStack, MovementContext context, BlockPos pos,
					Vec3d facing) {
					BlockPos interactAt = pos.offset(getClosestFacingDirection(facing));
					BlockState state = context.world.getBlockState(interactAt);
					Block block = state.getBlock();
					if (block instanceof FluidDrainable) {
						Fluid fluid = ((FluidDrainable) block).tryDrainFluid(context.world, interactAt, state);
						if (fluid instanceof FlowableFluid)
							return placeItemInInventory(itemStack, new ItemStack(fluid.getBucketItem()), context, pos,
								facing);
					}
					return super.dispenseStack(itemStack, context, pos, facing);
				}
			});
	}

	ItemStack dispense(ItemStack itemStack, MovementContext context, BlockPos pos);
}
