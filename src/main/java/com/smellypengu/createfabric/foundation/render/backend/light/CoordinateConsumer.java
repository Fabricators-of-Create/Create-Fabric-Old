package com.smellypengu.createfabric.foundation.render.backend.light;

@FunctionalInterface
public interface CoordinateConsumer {
    void consume(int x, int y, int z);
}
