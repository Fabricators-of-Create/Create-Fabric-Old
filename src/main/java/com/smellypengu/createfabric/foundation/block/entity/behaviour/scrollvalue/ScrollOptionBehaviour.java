package com.smellypengu.createfabric.foundation.block.entity.behaviour.scrollvalue;

import com.smellypengu.createfabric.foundation.block.entity.SmartBlockEntity;
import com.smellypengu.createfabric.foundation.block.entity.behaviour.ValueBoxTransform;

public class ScrollOptionBehaviour<E extends Enum<E> & NamedIconOptions> extends ScrollValueBehaviour {

	private E[] options;

	public ScrollOptionBehaviour(Class<E> enum_, String label, SmartBlockEntity te, ValueBoxTransform slot) {
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
