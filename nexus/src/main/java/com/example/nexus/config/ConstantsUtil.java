package com.example.nexus.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConstantsUtil {
    @SneakyThrows
    public static List<String> getAllConstants(Class<?> clazz) {
        final var constants = new ArrayList<String>();

        for(Field field: clazz.getDeclaredFields()) {
            if(field.getType().equals(String.class)) {
                constants.add((String) field.get(null));
            }
        }

        return constants;
    }
}