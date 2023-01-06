package com.github.tommyettinger.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class VectorPool {

    private final static Pool<Vector2> vec2 = Pools.get(Vector2.class, 500);
    private final static Pool<Vector3> vec3 = Pools.get(Vector3.class, 500);

    public static Vector2 getVec2() {
        return VectorPool.vec2.obtain();
    }

    public static Vector3 getVec3() {
        return VectorPool.vec3.obtain();
    }

    public static void free(Vector2 vec2) {
        VectorPool.vec2.free(vec2);
    }

    public static void free(Vector3 vec3) {
        VectorPool.vec3.free(vec3);
    }

    public static void freeAll(Vector2... vector2s) {
        for (Vector2 vec2 : vector2s) {
            VectorPool.vec2.free(vec2);
        }
    }

    public static void freeAll(Vector3... vector3s) {
        for (Vector3 vec3 : vector3s) {
            VectorPool.vec3.free(vec3);
        }
    }

}
