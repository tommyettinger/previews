package com.github.tommyettinger.utils;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import text.formic.Stringf;

public class RectI implements Pool.Poolable {

    public static Pool<RectI> pool = Pools.get(RectI.class);

    public int x;
    public int y;
    public int w;
    public int h;

    public RectI() {}

    public RectI(RectI other) {
        set(other);
    }

    public RectI(int x, int y, int w, int h) {
        set(x, y, w, h);
    }

    public static RectI zero() {
        return new RectI(0, 0, 0, 0);
    }

    public static RectI at(RectI other) {
        return new RectI(other);
    }

    public static RectI at(int x, int y, int w, int h) {
        return new RectI(x, y, w, h);
    }

    public RectI set(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        return this;
    }

    public RectI set(RectI other) {
        return set(other.x, other.y, other.w, other.h);
    }

    public RectI setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public RectI setSize(int w, int h) {
        this.w = w;
        this.h = h;
        return this;
    }

    public boolean overlaps(RectI other) {
        return x < other.x + other.w
            && other.x < x + w
            && y < other.y + other.h
            && other.y < y + h;
    }

    public int left()   { return x; }
    public int right()  { return x + w; }
    public int top()    { return y + h; }
    public int bottom() { return y; }

    @Override
    public void reset() {
        x = 0;
        y = 0;
        w = 0;
        h = 0;
    }

    public boolean contains(int x, int y) {
        return (x >= this.x && x <= this.x + this.w
             && y >= this.y && y <= this.y + this.h);
    }

    public boolean contains(RectI rect) {
        return (rect.left()   >= left()
             && rect.right()  <= right()
             && rect.bottom() >= bottom()
             && rect.top()    <= top());
    }

    public String toString() {
        return Stringf.format("RectI(%d, %d, %d, %d)", x, y, w, h);
    }

}
