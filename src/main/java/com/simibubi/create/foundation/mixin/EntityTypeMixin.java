package com.simibubi.create.foundation.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.foundation.mixinterface.EntityTypeExtension;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.entity.EntityType;

@Mixin(EntityType.class)
public class EntityTypeMixin implements EntityTypeExtension {
	private TriState alwaysUpdateVelocity = TriState.DEFAULT;

	@Override
	public TriState getAlwaysUpdateVelocity() {
		return alwaysUpdateVelocity;
	}

	@Override
	public void setAlwaysUpdateVelocity(TriState value) {
		alwaysUpdateVelocity = value;
	}

	@Inject(at = @At("HEAD"), method = "alwaysUpdateVelocity()Z", cancellable = true)
	public void onAlwaysUpdateVelocity(CallbackInfoReturnable<Boolean> cir) {
		if (alwaysUpdateVelocity != TriState.DEFAULT) {
			cir.setReturnValue(alwaysUpdateVelocity.get());
		}
	}
}
