package com.simibubi.create.foundation.block.entity.behaviour.scrollvalue;

import com.simibubi.create.foundation.block.entity.SmartBlockEntity;
import com.simibubi.create.foundation.block.entity.behaviour.ValueBoxTransform;

import net.minecraft.text.Text;

public class ScrollOptionBehaviour<E extends Enum<E> & NamedIconOptions> extends ScrollValueBehaviour {
	private E[] options;

	public ScrollOptionBehaviour(Class<E> enum_, Text label, SmartBlockEntity te, ValueBoxTransform slot) {
		super(label, te, slot);
		options = enum_.getEnumConstants();
		between(0, options.length - 1);
		withStepFunction((c) -> -1);
	}

	NamedIconOptions getIconForSelected() {
		return get();
	}
	
	public E get() {
		return options[scrollableValue];
	}
}
