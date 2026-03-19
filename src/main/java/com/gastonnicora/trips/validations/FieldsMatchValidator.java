package com.gastonnicora.trips.validations;

import java.lang.reflect.Field;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldsMatchValidator implements ConstraintValidator<FieldsMatch, Object> {

    private String field;
    private String fieldMatch;

    @Override
    public void initialize(FieldsMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        try {

            Field firstField = value.getClass().getDeclaredField(field);
            Field secondField = value.getClass().getDeclaredField(fieldMatch);

            firstField.setAccessible(true);
            secondField.setAccessible(true);

            Object firstValue = firstField.get(value);
            Object secondValue = secondField.get(value);

            if(firstValue == null || secondValue == null){
                return false;
            }

            return firstValue.equals(secondValue);

        } catch (Exception e) {
            return false;
        }
    }
}
