package com.mca.yourapp.service.utils;

import com.mca.yourapp.service.utils.exception.YourAppRuntimeException;

public class PreconditionUtils {
    private PreconditionUtils(){}

    public static void requireNotNull(final Object object, final String message) {
        if (object == null) {
            throw new YourAppRuntimeException(message);
        }
    }

    public static void require(final boolean condition, final String message) {
        if (!condition) {
            throw new YourAppRuntimeException(message);
        }
    }
}
