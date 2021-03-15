package com.simibubi.create.foundation.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

@Mixin(Item.class)
public interface ItemAccessor {
	@Invoker("isIn") boolean create$isIn(ItemGroup group);
}
