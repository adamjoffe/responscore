package io.ajo.responscore.service;

import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.CompositeTypeConfig;
import io.ajo.responscore.config.Config;
import io.ajo.responscore.config.Dependent;
import io.ajo.responscore.config.LookupConfig;
import io.ajo.responscore.config.LookupItem;
import io.ajo.responscore.config.Type;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.ValidatorType;
import io.ajo.responscore.form.Form;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Form Service Test")
public class FormServiceTest {

    private final FormService formService = new FormService();

    @Test
    @DisplayName("Valid Empty Form")
    public void validEmptyForm() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .build();

        final Form form = Form.builder().build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Required Field")
    public void invalidRequiredField() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .required(true)
                                .build()
                ))
                .build();

        final Form form = Form.builder().build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code", violation.getPropertyPath().toString());
        assertEquals("no data provided for attribute which is required", violation.getMessage());
    }

    @Test
    @DisplayName("Invalid Unknown Data")
    public void invalidUnknownData() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("unknown", ""))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data", violation.getPropertyPath().toString());
        assertEquals("data present (key='unknown') which is not part of the config schema", violation.getMessage());
    }

    @Test
    @DisplayName("Valid Data Coercion")
    public void validDataCoercion() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", 21))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Data Coercion")
    public void invalidDataCoercion() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.INTEGER)
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", "one"))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code", violation.getPropertyPath().toString());
        assertEquals("data type is invalid and not coercible to type 'INTEGER'", violation.getMessage());
    }

    @Test
    @DisplayName("Valid Lookup Value")
    public void validLookupValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.LOOKUP)
                                .lookupCode("lookupCode")
                                .build()
                ))
                .lookupConfigs(Set.of(
                        LookupConfig.builder()
                                .code("lookupCode")
                                .lookupItems(Set.of(
                                        LookupItem.builder()
                                                .code("lookup1")
                                                .label("Lookup 1")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", "lookup1"))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Lookup Value")
    public void invalidLookupValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.LOOKUP)
                                .lookupCode("lookupCode")
                                .build()
                ))
                .lookupConfigs(Set.of(
                        LookupConfig.builder()
                                .code("lookupCode")
                                .lookupItems(Set.of(
                                        LookupItem.builder()
                                                .code("lookup1")
                                                .label("Lookup 1")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", "not_lookup"))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code", violation.getPropertyPath().toString());
        assertEquals("data value ('not_lookup') doesn't match any known lookup item code", violation.getMessage());
    }

    @Test
    @DisplayName("Valid Min Value")
    public void validMinValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.INTEGER)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.Min)
                                                .value(BigDecimal.TEN)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", 11))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Min Value")
    public void invalidMinValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.INTEGER)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.Min)
                                                .value(BigDecimal.TEN)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", 1))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code", violation.getPropertyPath().toString());
        assertEquals("data is invalid by validator 'Min' (>=10)", violation.getMessage());
    }

    @Test
    @DisplayName("Valid NotBlank Value")
    public void validNotBlankValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.NotBlank)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", "eleven"))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid NotBlank Value")
    public void invalidNotBlankValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.NotBlank)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", ""))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code", violation.getPropertyPath().toString());
        assertEquals("data is invalid by validator 'NotBlank'", violation.getMessage());
    }

    @Test
    @DisplayName("Valid GreaterThan Value")
    public void validGreaterThanValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.INTEGER)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.GreaterThan)
                                                .value(BigDecimal.ONE)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", 2))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid GreaterThan Value")
    public void invalidGreaterThanValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.GreaterThan)
                                                .value(BigDecimal.ONE)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", 1))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code", violation.getPropertyPath().toString());
        assertEquals("data is invalid by validator 'GreaterThan' (>1)", violation.getMessage());
    }

    @Test
    @DisplayName("Valid Max List Values")
    public void validMaxListValues() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.INTEGER)
                                .list(true)
                                .validateItems(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.Max)
                                                .value(BigDecimal.valueOf(5))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", List.of(2, 3, 1)))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Max List Values")
    public void invalidMaxListValues() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.INTEGER)
                                .list(true)
                                .validateItems(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.Max)
                                                .value(BigDecimal.valueOf(5))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of("code", List.of(2, 6, 1)))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code[1]", violation.getPropertyPath().toString());
        assertEquals("data item in list is invalid by validator 'Max' (<=5)", violation.getMessage());
    }

    @Test
    @DisplayName("Valid Dependency Met")
    public void validDependencyMet() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("c1")
                                .label("c1")
                                .type(Type.BOOLEAN)
                                .build(),
                        Attribute.builder()
                                .code("c2")
                                .label("c2")
                                .type(Type.STRING)
                                .dependencies(List.of(
                                        Dependent.builder()
                                                .attributeCode("c1")
                                                .values(Set.of(true))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of(
                        "c1", true,
                        "c2", "accepted"
                ))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Dependency Met")
    public void invalidDependencyMet() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("c1")
                                .label("c1")
                                .type(Type.BOOLEAN)
                                .build(),
                        Attribute.builder()
                                .code("c2")
                                .label("c2")
                                .type(Type.STRING)
                                .dependencies(List.of(
                                        Dependent.builder()
                                                .attributeCode("c1")
                                                .values(Set.of(true))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of(
                        "c1", false,
                        "c2", "accepted"
                ))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.c2", violation.getPropertyPath().toString());
        assertEquals("data provided for attribute with unmet dependency on dependee attribute 'c1'", violation.getMessage());
    }

    @Test
    @DisplayName("Invalid Nested Composite Value")
    public void invalidNestedCompositeValue() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.COMPOSITE)
                                .compositeCode("compositeCode")
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder()
                                .code("compositeCode")
                                .attributes(Set.of(
                                        Attribute.builder()
                                                .code("c1")
                                                .label("c1")
                                                .type(Type.STRING)
                                                .build(),
                                        Attribute.builder()
                                                .code("c2")
                                                .label("c2")
                                                .type(Type.INTEGER)
                                                .validators(List.of(
                                                        Validator.builder()
                                                                .type(ValidatorType.Min)
                                                                .value(BigDecimal.TEN)
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of(
                        "code", Map.of(
                                "c1", "abc",
                                "c2", 9
                        )
                ))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(1, violations.size());
        final ConstraintViolation<Object> violation = violations.iterator().next();
        assertEquals("data.code.c2", violation.getPropertyPath().toString());
        assertEquals("data is invalid by validator 'Min' (>=10)", violation.getMessage());
    }

    @Test
    @DisplayName("Invalid Multiple Errors")
    public void invalidMultipleErrors() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("c1")
                                .label("c1")
                                .type(Type.LOOKUP)
                                .lookupCode("lookupCode")
                                .list(true)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.MaxSize)
                                                .value(BigDecimal.TWO)
                                                .build()
                                ))
                                .build(),
                        Attribute.builder()
                                .code("c2")
                                .label("c2")
                                .type(Type.INTEGER)
                                .list(true)
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.MinSize)
                                                .value(BigDecimal.TWO)
                                                .build()
                                ))
                                .validateItems(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.GreaterThan)
                                                .value(BigDecimal.TEN)
                                                .build()
                                ))
                                .build(),
                        Attribute.builder()
                                .code("c3")
                                .label("c3")
                                .type(Type.STRING)
                                .required(true)
                                .build()
                ))
                .lookupConfigs(Set.of(
                        LookupConfig.builder()
                                .code("lookupCode")
                                .lookupItems(Set.of(
                                        LookupItem.builder()
                                                .code("l1")
                                                .label("l1")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Form form = Form.builder()
                .data(Map.of(
                        "c1", List.of("l1", "l1", "unknown"),
                        "c2", List.of(12, 9)
                ))
                .build();

        final Set<ConstraintViolation<Object>> violations = formService.validateFormWithConfig(config, form);

        assertEquals(4, violations.size());

        final Optional<ConstraintViolation<Object>> oUnknownLookupValueViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("data.c1[2]")).findAny();
        assertTrue(oUnknownLookupValueViolation.isPresent());
        assertEquals("data value ('unknown') doesn't match any known lookup item code", oUnknownLookupValueViolation.get().getMessage());

        final Optional<ConstraintViolation<Object>> oMissingRequiredDataViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("data.c3")).findAny();
        assertTrue(oMissingRequiredDataViolation.isPresent());
        assertEquals("no data provided for attribute which is required", oMissingRequiredDataViolation.get().getMessage());

        final Optional<ConstraintViolation<Object>> oMaxSizeViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("data.c1")).findAny();
        assertTrue(oMaxSizeViolation.isPresent());
        assertEquals("data is invalid by validator 'MaxSize' (MaxSize=2)", oMaxSizeViolation.get().getMessage());

        final Optional<ConstraintViolation<Object>> oGreaterThanViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("data.c2[1]")).findAny();
        assertTrue(oGreaterThanViolation.isPresent());
        assertEquals("data item in list is invalid by validator 'GreaterThan' (>10)", oGreaterThanViolation.get().getMessage());
    }

}
