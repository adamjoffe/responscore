package io.ajo.responscore.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {

    private static ObjectMapper OBJECT_MAPPER_INSTANCE;

    public static ObjectMapper getObjectMapper() {
        if (OBJECT_MAPPER_INSTANCE == null) {
            OBJECT_MAPPER_INSTANCE = new ObjectMapper();
        }
        return OBJECT_MAPPER_INSTANCE;
    }

}
