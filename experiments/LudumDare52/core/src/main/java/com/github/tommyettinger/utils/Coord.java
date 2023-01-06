package com.github.tommyettinger.utils;

import com.badlogic.gdx.utils.Array;
import text.formic.Stringf;

public class Coord {

    private final static Array<Coord> cache = new Array<>();

    // NOTE - Coord instances should only be acquired through this method
    public static Coord at(int x, int y) {
        for (Coord coord : cache) {
            if (coord.is(x, y)) {
                return coord;
            }
        }
        Coord coord = new Coord(x, y);
        cache.add(coord);
        return coord;
    }

    public static Coord zero() {
        return Coord.at(0, 0);
    }

    private final int x;
    private final int y;

    private Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public boolean is(int x, int y) {
        return (this.x == x && this.y == y);
    }

    @Override
    public String toString() {
        return Stringf.format("(%d, %d)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coord) {
            return (this == obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

}
