package io.ajo.responscore.config;


import io.ajo.responscore.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidatorValidatorTest {

    private final javax.validation.Validator validator = ValidationUtils.getValidator();

    @Test
    @DisplayName("Invalid Validator Null Type")
    public void invalidValidatorNullType() {
        final Validator invalidValidator = Validator.builder().build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(invalidValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("must not be null", violation.getMessage());
        assertEquals("type", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Min Validator")
    public void validMinValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.Min)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Min Validator")
    public void invalidMinValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.Min)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator of type 'Min' must have 'value' field set", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Max Validator")
    public void validMaxValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.Max)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Max Validator")
    public void invalidMaxValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.Max)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator of type 'Max' must have 'value' field set", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid GreaterThan Validator")
    public void validGreaterThanValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.GreaterThan)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid GreaterThan Validator")
    public void invalidGreaterThanValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.GreaterThan)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator of type 'GreaterThan' must have 'value' field set", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid LessThan Validator")
    public void validLessThanValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.LessThan)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid LessThan Validator")
    public void invalidLessThanValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.LessThan)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator of type 'LessThan' must have 'value' field set", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid MinSize Validator")
    public void validMinSizeValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.MinSize)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid MinSize Validator")
    public void invalidMinSizeValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.MinSize)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator of type 'MinSize' must have 'value' field set", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid MaxSize Validator")
    public void validMaxSizeValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.MaxSize)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid MaxSize Validator")
    public void invalidMaxSizeValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.MaxSize)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator of type 'MaxSize' must have 'value' field set", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid NotNull Validator")
    public void validNotNullValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.NotNull)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid NotNull Validator")
    public void invalidNotNullValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.NotNull)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator not of type 'NotNull' has 'value' field set invalidly", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid NotBlank Validator")
    public void validNotBlankValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.NotBlank)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid NotBlank Validator")
    public void invalidNotBlankValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.NotBlank)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator not of type 'NotBlank' has 'value' field set invalidly", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid NotEmpty Validator")
    public void validNotEmptyValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.NotEmpty)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid NotEmpty Validator")
    public void invalidNotEmptyValidator() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.NotEmpty)
                .value(BigDecimal.ZERO)
                .build();

        final Set<ConstraintViolation<Validator>> violations = validator.validate(minValidator);

        assertEquals(1, violations.size());
        final ConstraintViolation<Validator> violation = violations.iterator().next();
        assertEquals("validator not of type 'NotEmpty' has 'value' field set invalidly", violation.getMessage());
        assertEquals("value", violation.getPropertyPath().toString());
    }
}
