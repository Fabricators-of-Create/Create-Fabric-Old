package com.smellypengu.createfabric.foundation.render;

import com.smellypengu.createfabric.AllBlockPartials;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.BlockState;

public class Compartment<T> {
    public static final Compartment<BlockState> GENERIC_TILE = new Compartment<>();
    public static final Compartment<AllBlockPartials> PARTIAL = new Compartment<>();
    public static final Compartment<Pair<Direction, AllBlockPartials>> DIRECTIONAL_PARTIAL = new Compartment<>();
}
