package com.example.demo.Services;

import com.example.demo.Entities.Sex;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GenericFilterUtility {

    public static <T> List<T> filter(List<T> items, Map<String, Object> criteria) {
        return items.stream()
                .filter(item -> {
                    boolean matches = true;
                    for (Map.Entry<String, Object> entry : criteria.entrySet()) {
                        String fieldName = entry.getKey();
                        Object fieldValue = entry.getValue();
                        try {
                            String baseFieldName = fieldName.replace("Min", "").replace("Max", "");
                            Field field = item.getClass().getDeclaredField(baseFieldName);
                            field.setAccessible(true);
                            Object value = field.get(item);

                            if (fieldValue instanceof String) {
                                fieldValue = tryParse((String) fieldValue);
                            }
                            switch (fieldValue.getClass().getSimpleName()) {
                                case "BigDecimal" -> {
                                    BigDecimal criterionValue = (BigDecimal) fieldValue;
                                    BigDecimal itemValue = (BigDecimal) value;
                                    if (fieldName.endsWith("Min")) {
                                        matches &= itemValue.compareTo(criterionValue) >= 0;
                                    } else if (fieldName.endsWith("Max")) {
                                        matches &= itemValue.compareTo(criterionValue) <= 0; } }
                                case "Integer" -> {
                                    Integer criterionValue = (Integer) fieldValue;
                                    Integer itemValue = (Integer) value;
                                    if (fieldName.endsWith("Min")) {
                                        matches &= itemValue.compareTo(criterionValue) >= 0;
                                    } else if (fieldName.endsWith("Max")) {
                                        matches &= itemValue.compareTo(criterionValue) <= 0; } }
                                case "LocalTime" -> {
                                    LocalTime criterionValue = (LocalTime) fieldValue;
                                    LocalTime itemValue = (LocalTime) value;
                                    if (fieldName.endsWith("Min")) {
                                        matches &= !itemValue.isBefore(criterionValue);
                                    } else if (fieldName.endsWith("Max")) {
                                        matches &= !itemValue.isAfter(criterionValue); } }
                                case "LocalDate" -> {
                                    LocalDate criterionValue = (LocalDate) fieldValue;
                                    LocalDate itemValue = (LocalDate) value;
                                    if (fieldName.endsWith("Min")) {
                                        matches &= !itemValue.isBefore(criterionValue);
                                    } else if (fieldName.endsWith("Max")) {
                                        matches &= !itemValue.isAfter(criterionValue); } }
                                case "Boolean" -> matches &= fieldValue.equals(value);
                                case "Sex" -> matches &= fieldValue.equals(Enum.valueOf(Sex.class, (String) value));
                                case "String" -> {
                                    if (fieldName.equals("workingDay")) {
                                        List<String> masterWorkingDays = Arrays.asList(((String) value)
                                                .split(","));
                                        List<String> criteriaWorkingDays = Arrays.asList(((String) fieldValue)
                                                .split(","));
                                        matches &= criteriaWorkingDays.stream().anyMatch(masterWorkingDays::contains);
                                    } else { matches &= ((String) value).contains((String) fieldValue); } }}
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            matches = false;
                        }
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }

    private static Object tryParse(String value) {
        value = value.trim();
        try {return LocalTime.parse(value);}
        catch (Exception e1) {
            try {return LocalDate.parse(value);}
            catch (Exception e2) {
                try {return Integer.valueOf(value);}
                catch (Exception e3) {
                    try {return new BigDecimal(value);}
                    catch (Exception e4) {
                        try {return Enum.valueOf(Sex.class, value);}
                        catch (Exception e5) {
                            return value;}
                    }
                }
            }
        }
    }

}
