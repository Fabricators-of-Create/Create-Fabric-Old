package com.simibubi.create.foundation.config.util;

import me.shedaniel.autoconfig.ConfigData;

public interface Validatable {
	void validate() throws ConfigData.ValidationException;
}
