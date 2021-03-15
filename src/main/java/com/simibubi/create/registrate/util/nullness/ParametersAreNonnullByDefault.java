package com.simibubi.create.registrate.util.nullness;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jetbrains.annotations.NotNull;

/**
 * This annotation can be applied to a package, class or method to indicate that
 * the method parameters in that element are nonnull by default unless there is:
 * <ul>
 * <li>An explicit nullness annotation
 * <li>The method overrides a method in a superclass (in which case the
 * annotation of the corresponding parameter in the superclass applies)
 * <li>There is a default parameter annotation
 * applied to a more tightly nested element.
 * </ul>
 *
 * @see NotNull
 */
@Documented
@NotNull
@TypeQualifierDefault(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface ParametersAreNonnullByDefault {
}