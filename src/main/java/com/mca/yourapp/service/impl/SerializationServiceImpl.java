package com.mca.yourapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mca.yourapp.service.LogService;
import com.mca.yourapp.service.SerializationService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SerializationServiceImpl implements SerializationService {
    @Autowired
    private LogService logService;

    public <T> T deserialize(final String data, final Class<T> targetClass) {
        final ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(data, targetClass);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            logService.log(e);
            return null;
        }
    }

    @Override
    public <T> List<T> deserializeList(final String data, final Class<T> targetClass) {
        if (data == null || !data.startsWith("[")) {
            return List.of();
        }
        final JSONArray jsonArray = new JSONArray(data);

        final List<T> deserializedObjects = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            final T deserializedObject = deserialize(jsonArray.getJSONObject(i).toString(), targetClass);
            deserializedObjects.add(deserializedObject);
        }

        return deserializedObjects;
    }
}
