package io.ajo.responscore.config;


import io.ajo.responscore.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Validator Validator Test")
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

    @Test
    @DisplayName("Test NotNull Validator Logic")
    public void testNotNullValidatorLogic() {
        final Validator notNullValidator = Validator.builder().type(ValidatorType.NotNull).build();
        assertTrue(notNullValidator.validate(1));
        assertFalse(notNullValidator.validate(null));
    }

    @Test
    @DisplayName("Test NotBlank Validator Logic")
    public void testNotBlankValidatorLogic() {
        final Validator notBlankValidator = Validator.builder().type(ValidatorType.NotBlank).build();
        assertTrue(notBlankValidator.validate("abc"));
        assertFalse(notBlankValidator.validate(""));
        assertFalse(notBlankValidator.validate(null));
        assertFalse(notBlankValidator.validate(1));
    }

    @Test
    @DisplayName("Test Min Validator Logic")
    public void testMinValidatorLogic() {
        final Validator minValidator = Validator.builder()
                .type(ValidatorType.Min)
                .value(BigDecimal.TEN)
                .build();
        assertTrue(minValidator.validate(15));
        assertTrue(minValidator.validate(BigDecimal.valueOf(105.21)));
        assertFalse(minValidator.validate(1));
        assertFalse(minValidator.validate(null));
        assertFalse(minValidator.validate("abc"));
    }

    @Test
    @DisplayName("Test Max Validator Logic")
    public void testMaxValidatorLogic() {
        final Validator maxValidator = Validator.builder()
                .type(ValidatorType.Max)
                .value(BigDecimal.TEN)
                .build();
        assertTrue(maxValidator.validate(5));
        assertTrue(maxValidator.validate(2.12));
        assertFalse(maxValidator.validate(16));
        assertFalse(maxValidator.validate(null));
        assertFalse(maxValidator.validate("abc"));
    }

    @Test
    @DisplayName("Test GreaterThan Validator Logic")
    public void testGreaterThanValidatorLogic() {
        final Validator greaterThanValidator = Validator.builder()
                .type(ValidatorType.GreaterThan)
                .value(BigDecimal.TEN)
                .build();
        assertTrue(greaterThanValidator.validate(11));
        assertTrue(greaterThanValidator.validate(10.0001));
        assertFalse(greaterThanValidator.validate(10));
        assertFalse(greaterThanValidator.validate(null));
        assertFalse(greaterThanValidator.validate("abc"));
    }

    @Test
    @DisplayName("Test LessThan Validator Logic")
    public void testLessThanValidatorLogic() {
        final Validator lessThanValidator = Validator.builder()
                .type(ValidatorType.LessThan)
                .value(BigDecimal.TEN)
                .build();
        assertTrue(lessThanValidator.validate(9));
        assertTrue(lessThanValidator.validate(9.9999));
        assertFalse(lessThanValidator.validate(10));
        assertFalse(lessThanValidator.validate(null));
        assertFalse(lessThanValidator.validate("abc"));
    }

    @Test
    @DisplayName("Test NotEmpty Validator Logic")
    public void testNotEmptyValidatorLogic() {
        final Validator notEmptyValidator = Validator.builder().type(ValidatorType.NotEmpty).build();
        assertTrue(notEmptyValidator.validate("abc"));
        assertTrue(notEmptyValidator.validate(List.of(1, 2, 3)));
        assertTrue(notEmptyValidator.validate(Optional.of(1)));
        assertTrue(notEmptyValidator.validate(Map.of("key", "value")));
        assertFalse(notEmptyValidator.validate(""));
        assertFalse(notEmptyValidator.validate(List.of()));
        assertFalse(notEmptyValidator.validate(Map.of()));
    }

    @Test
    @DisplayName("Test MinSize Validator Logic")
    public void testMinSizeValidatorLogic() {
        final Validator minSizeValidator = Validator.builder()
                .type(ValidatorType.MinSize)
                .value(BigDecimal.TWO)
                .build();
        assertTrue(minSizeValidator.validate("abc"));
        assertTrue(minSizeValidator.validate(List.of(1, 2)));
        assertTrue(minSizeValidator.validate(Map.of("key1", "value1", "key2", "value2")));
        assertFalse(minSizeValidator.validate("a"));
        assertFalse(minSizeValidator.validate(List.of(1)));
        assertFalse(minSizeValidator.validate(Map.of("key1", "value1")));
    }

    @Test
    @DisplayName("Test MaxSize Validator Logic")
    public void testMaxSizeValidatorLogic() {
        final Validator maxSizeValidator = Validator.builder()
                .type(ValidatorType.MaxSize)
                .value(BigDecimal.TWO)
                .build();
        assertTrue(maxSizeValidator.validate("ab"));
        assertTrue(maxSizeValidator.validate(List.of(1, 2)));
        assertTrue(maxSizeValidator.validate(Map.of("key1", "value1", "key2", "value2")));
        assertFalse(maxSizeValidator.validate("abc"));
        assertFalse(maxSizeValidator.validate(List.of(1, 2, 3)));
        assertFalse(maxSizeValidator.validate(Map.of("k1", "v1", "k2", "v2", "k3", "v3")));
    }

    @Test
    @DisplayName("Valid Default Value")
    public void validDefaultValue() {
        final Attribute strAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .defaultValue(102)
                .build();

        final Set<ConstraintViolation<Attribute>> strViolations = validator.validate(strAttr);
        assertEquals(0, strViolations.size());

        final Attribute dateAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.DATE)
                .defaultValue("2023-09-23T19:48:20+07:00")
                .build();

        final Set<ConstraintViolation<Attribute>> dateViolations = validator.validate(dateAttr);
        assertEquals(0, dateViolations.size());

        final Attribute decAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.DECIMAL)
                .defaultValue("101.010")
                .build();

        final Set<ConstraintViolation<Attribute>> decViolations = validator.validate(decAttr);
        assertEquals(0, decViolations.size());

        final Attribute intAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.INTEGER)
                .defaultValue("1")
                .build();

        final Set<ConstraintViolation<Attribute>> intViolations = validator.validate(intAttr);
        assertEquals(0, intViolations.size());

        final Attribute boolAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.BOOLEAN)
                .defaultValue("true")
                .build();

        final Set<ConstraintViolation<Attribute>> boolViolations = validator.validate(boolAttr);
        assertEquals(0, boolViolations.size());
    }

    @Test
    @DisplayName("Invalid Default Value")
    public void invalidDefaultValue() {
        final Attribute strAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .defaultValue(Map.of("k1", "v1"))
                .build();

        final Set<ConstraintViolation<Attribute>> strViolations = validator.validate(strAttr);
        assertEquals(1, strViolations.size());
        final ConstraintViolation<Attribute> strViolation = strViolations.iterator().next();
        assertEquals("attribute with default value ('{k1=v1}') is of wrong type for attribute type 'STRING'", strViolation.getMessage());
        assertEquals("default", strViolation.getPropertyPath().toString());

        final Attribute dateAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.DATE)
                .defaultValue("6 January 2021")
                .build();

        final Set<ConstraintViolation<Attribute>> dateViolations = validator.validate(dateAttr);
        assertEquals(1, dateViolations.size());
        final ConstraintViolation<Attribute> dateViolation = dateViolations.iterator().next();
        assertEquals("attribute with default value ('6 January 2021') is of wrong type for attribute type 'DATE'", dateViolation.getMessage());
        assertEquals("default", dateViolation.getPropertyPath().toString());

        final Attribute decAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.DECIMAL)
                .defaultValue("ten point zero seven")
                .build();

        final Set<ConstraintViolation<Attribute>> decViolations = validator.validate(decAttr);
        assertEquals(1, decViolations.size());
        final ConstraintViolation<Attribute> decViolation = decViolations.iterator().next();
        assertEquals("attribute with default value ('ten point zero seven') is of wrong type for attribute type 'DECIMAL'", decViolation.getMessage());
        assertEquals("default", decViolation.getPropertyPath().toString());

        final Attribute intAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.INTEGER)
                .defaultValue("abc")
                .build();

        final Set<ConstraintViolation<Attribute>> intViolations = validator.validate(intAttr);
        assertEquals(1, intViolations.size());
        final ConstraintViolation<Attribute> intViolation = intViolations.iterator().next();
        assertEquals("attribute with default value ('abc') is of wrong type for attribute type 'INTEGER'", intViolation.getMessage());
        assertEquals("default", intViolation.getPropertyPath().toString());

        final Attribute boolAttr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.BOOLEAN)
                .defaultValue("definitely")
                .build();

        final Set<ConstraintViolation<Attribute>> boolViolations = validator.validate(boolAttr);
        assertEquals(1, boolViolations.size());
        final ConstraintViolation<Attribute> boolViolation = boolViolations.iterator().next();
        assertEquals("attribute with default value ('definitely') is of wrong type for attribute type 'BOOLEAN'", boolViolation.getMessage());
        assertEquals("default", boolViolation.getPropertyPath().toString());
    }
}
