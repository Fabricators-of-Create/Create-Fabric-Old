package com.simibubi.create.foundation.utility.recipe;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.recipe.Recipe;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;

/**
 * Utility for searching through a world's recipe collection. Non-dynamic
 * conditions can be split off into an initial search for caching intermediate
 * results.
 * 
 * @author simibubi
 *
 */
public class RecipeFinder {
	
	private static Cache<Object, List<Recipe<?>>> cachedSearches = CacheBuilder.newBuilder().build();

	/**
	 * Find all IRecipes matching the condition predicate. If this search is made
	 * more than once, using the same object instance as the cacheKey will retrieve
	 * the cached result from the first time.
	 * 
	 * @param cacheKey   (can be null to prevent the caching)
	 * @param world
	 * @param conditions
	 * @return A started search to continue with more specific conditions.
	 */
	public static List<Recipe<?>> get(@Nullable Object cacheKey, World world, Predicate<Recipe<?>> conditions) {
		if (cacheKey == null)
			return startSearch(world, conditions);

		try {
			return cachedSearches.get(cacheKey, () -> startSearch(world, conditions));
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}

	private static List<Recipe<?>> startSearch(World world, Predicate<? super Recipe<?>> conditions) {
		List<Recipe<?>> list = world.getRecipeManager().values().stream().filter(conditions)
				.collect(Collectors.toList());
		return list;
	}


	public static final SinglePreparationResourceReloadListener<Object> LISTENER = new SinglePreparationResourceReloadListener<Object>() {
		
		@Override
		protected Object prepare(ResourceManager p_212854_1_, Profiler p_212854_2_) {
			return new Object();
		}
		
		@Override
		protected void apply(Object p_212853_1_, ResourceManager p_212853_2_, Profiler p_212853_3_) {
			cachedSearches.invalidateAll();
		}
		
	};

}
