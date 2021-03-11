package com.smellypengu.registrate.util.nullness;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

/**
 * An alternative to {@link -} which works on type parameters (J8 feature).
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NotNull
public @interface NonnullType {
}