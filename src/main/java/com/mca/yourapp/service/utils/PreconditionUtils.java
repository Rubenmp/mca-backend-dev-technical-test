package com.mca.yourapp.service.utils;

public class PreconditionUtils {
    private PreconditionUtils(){}

    public static void requireNotNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
