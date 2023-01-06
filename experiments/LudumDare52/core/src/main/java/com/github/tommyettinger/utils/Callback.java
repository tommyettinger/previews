package com.github.tommyettinger.utils;

@FunctionalInterface
public interface Callback {
    void run(Object... params);
}
