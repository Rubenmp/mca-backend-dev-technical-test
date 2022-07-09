package com.mca.yourapp.service;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;


public interface SerializationService {
    /**
     * Transform json based string {@param data} into class {@param targetClass}
     * <p>
     * Preconditions:
     * - {@param targetClass} must implement no argument constructor
     * - {@param data} classes with nested dto(s) are not supported
     *
     * @param data        string to be transformed
     * @param targetClass class of the object to be returned
     * @return deserialized object of class {@param targetClass}
     */
    <T> T deserialize(@Nullable final String data, @NonNull final Class<T> targetClass);

    <T> List<T> deserializeList(@Nullable final String data, @NonNull final Class<T> targetClass);
}
