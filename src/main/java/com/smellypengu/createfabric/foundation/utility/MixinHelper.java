package com.smellypengu.createfabric.foundation.utility;

public final class MixinHelper {
    /**
     * A simple utility method that casts an object to a type.
     * <p>
     * This is intended to use with accessor Mixins.
     * </p>
     *
     * @param in  the object to cast
     * @param <T> the type to cast to
     * @return the casted object
     */
    @SuppressWarnings("unchecked")
    public static <T> T cast(Object in) {
        return (T) in;
    }

    private MixinHelper() {
    }
}
