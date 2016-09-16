package com.github.rlf.littlebits.async;

/**
 * Consumer (part of Java 8 - but need to be Java 7 compatible).
 */
public interface Consumer<T> {
    void accept(T data);
}
