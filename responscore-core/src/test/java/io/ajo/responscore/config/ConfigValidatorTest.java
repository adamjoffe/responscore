package io.ajo.responscore.config;

import io.ajo.responscore.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Config Validator Test")
public class ConfigValidatorTest {

    private final javax.validation.Validator validator = ValidationUtils.getValidator();

    @Test
    @DisplayName("Invalid Empty Config")
    public void invalidEmptyConfig() {
        final Config config = Config.builder().build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("size must be between 1 and 2147483647", violation.getMessage());
        assertEquals("attributes", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Invalid Unused Lookup Config")
    public void invalidUnusedLookupConfig() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .lookupConfigs(Set.of(
                        LookupConfig.builder()
                                .code("lookupConfigCode")
                                .lookupItems(Set.of(
                                        LookupItem.builder()
                                                .code("item1")
                                                .label("Item 1")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("lookup config 'lookupConfigCode' is not used by any attribute", violation.getMessage());
        assertEquals("lookupConfigs[0]", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Invalid Unused Composite Type Config")
    public void invalidUnusedCompositeTypeConfig() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder()
                                .code("compositeTypeConfigCode")
                                .attributes(Set.of(
                                        Attribute.builder()
                                                .code("fieldA")
                                                .label("Field A")
                                                .type(Type.STRING)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("composite type config 'compositeTypeConfigCode' is not used by any attribute", violation.getMessage());
        assertEquals("compositeTypeConfigs[0]", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Invalid Lookup Config")
    public void invalidLookupConfig() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .lookupConfigs(Set.of(
                        LookupConfig.builder().build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(2, violations.size());
        final Optional<ConstraintViolation<Config>> oCodeViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("lookupConfigs[].code")).findAny();
        assertTrue(oCodeViolation.isPresent());
        assertEquals("must not be blank", oCodeViolation.get().getMessage());
        final Optional<ConstraintViolation<Config>> oLookupItemsViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("lookupConfigs[].lookupItems")).findAny();
        assertTrue(oLookupItemsViolation.isPresent());
        assertEquals("size must be between 1 and 2147483647", oLookupItemsViolation.get().getMessage());
    }

    @Test
    @DisplayName("Invalid Lookup Item Config")
    public void invalidLookupItemConfig() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.LOOKUP)
                                .lookupCode("code")
                                .build()
                ))
                .lookupConfigs(Set.of(
                        LookupConfig.builder()
                                .code("code")
                                .lookupItems(Set.of(
                                        LookupItem.builder().build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(2, violations.size());
        final Optional<ConstraintViolation<Config>> oCodeViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("lookupConfigs[].lookupItems[].code")).findAny();
        assertTrue(oCodeViolation.isPresent());
        assertEquals("must not be blank", oCodeViolation.get().getMessage());
        final Optional<ConstraintViolation<Config>> oLabelViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("lookupConfigs[].lookupItems[].label")).findAny();
        assertTrue(oLabelViolation.isPresent());
        assertEquals("must not be blank", oLabelViolation.get().getMessage());
    }

    @Test
    @DisplayName("Invalid Composite Type Config")
    public void invalidCompositeTypeConfig() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.STRING)
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder().build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(2, violations.size());
        final Optional<ConstraintViolation<Config>> oCodeViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("compositeTypeConfigs[].code")).findAny();
        assertTrue(oCodeViolation.isPresent());
        assertEquals("must not be blank", oCodeViolation.get().getMessage());
        final Optional<ConstraintViolation<Config>> oAttributesViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("compositeTypeConfigs[].attributes")).findAny();
        assertTrue(oAttributesViolation.isPresent());
        assertEquals("size must be between 1 and 2147483647", oAttributesViolation.get().getMessage());
    }

    @Test
    @DisplayName("Valid Lookup Config")
    public void validLookupConfig() {
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
                                                .code("item1")
                                                .label("Item 1")
                                                .build(),
                                        LookupItem.builder()
                                                .code("item2")
                                                .label("Item 2")
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Attribute Lookup Code Reference")
    public void invalidAttributeLookupCodeReference() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.LOOKUP)
                                .lookupCode("lookupCode")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("attribute referenced a 'lookupCode' ('lookupCode') which isn't declared", violation.getMessage());
        assertEquals("attributes[0].lookupCode", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Composite Type Config")
    public void validCompositeTypeConfig() {
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
                                                .code("fieldA")
                                                .label("Field A")
                                                .type(Type.STRING)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Attribute Composite Code Reference")
    public void invalidAttributeCompositeCodeReference() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.COMPOSITE)
                                .compositeCode("compositeCode")
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("attribute referenced a 'compositeCode' ('compositeCode') which isn't declared", violation.getMessage());
        assertEquals("attributes[0].compositeCode", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Field Validator Composite Attribute")
    public void validFieldValidatorCompositeAttribute() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.COMPOSITE)
                                .compositeCode("compositeCode")
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.NotNull)
                                                .field("fieldA")
                                                .build()
                                ))
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder()
                                .code("compositeCode")
                                .attributes(Set.of(
                                        Attribute.builder()
                                                .code("fieldA")
                                                .label("Field A")
                                                .type(Type.STRING)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Field Validator Composite Attribute")
    public void invalidFieldValidatorCompositeAttribute() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.COMPOSITE)
                                .compositeCode("compositeCode")
                                .validators(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.NotNull)
                                                .field("fieldB")
                                                .build()
                                ))
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder()
                                .code("compositeCode")
                                .attributes(Set.of(
                                        Attribute.builder()
                                                .code("fieldA")
                                                .label("Field A")
                                                .type(Type.STRING)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("attribute validator references composite field 'fieldB' which is not part of composite type with code 'compositeCode'", violation.getMessage());
        assertEquals("attributes[0].validators[0].field", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Field Validate Item Composite Attribute")
    public void validFieldValidateItemCompositeAttribute() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.COMPOSITE)
                                .compositeCode("compositeCode")
                                .list(true)
                                .validateItems(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.NotNull)
                                                .field("fieldA")
                                                .build()
                                ))
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder()
                                .code("compositeCode")
                                .attributes(Set.of(
                                        Attribute.builder()
                                                .code("fieldA")
                                                .label("Field A")
                                                .type(Type.STRING)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Field Validate Item Composite Attribute")
    public void invalidFieldValidateItemCompositeAttribute() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("code")
                                .label("label")
                                .type(Type.COMPOSITE)
                                .compositeCode("compositeCode")
                                .list(true)
                                .validateItems(List.of(
                                        Validator.builder()
                                                .type(ValidatorType.NotNull)
                                                .field("fieldB")
                                                .build()
                                ))
                                .build()
                ))
                .compositeTypeConfigs(Set.of(
                        CompositeTypeConfig.builder()
                                .code("compositeCode")
                                .attributes(Set.of(
                                        Attribute.builder()
                                                .code("fieldA")
                                                .label("Field A")
                                                .type(Type.STRING)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("attribute validate item references composite field 'fieldB' which is not part of composite type with code 'compositeCode'", violation.getMessage());
        assertEquals("attributes[0].validateItems[0].field", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Attribute Dependency")
    public void validAttributeDependency() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("attr1")
                                .label("Attribute 1")
                                .type(Type.BOOLEAN)
                                .build(),
                        Attribute.builder()
                                .code("attr2")
                                .label("Attribute 2")
                                .type(Type.STRING)
                                .dependencies(List.of(
                                        Dependent.builder()
                                                .attributeCode("attr1")
                                                .values(Set.of(true))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Attribute Dependency")
    public void invalidAttributeDependency() {
        final Config config = Config.builder()
                .attributes(Set.of(
                        Attribute.builder()
                                .code("attr1")
                                .label("Attribute 1")
                                .type(Type.STRING)
                                .dependencies(List.of(
                                        Dependent.builder()
                                                .attributeCode("missing")
                                                .values(Set.of(true))
                                                .build()
                                ))
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Config>> violations = validator.validate(config);

        assertEquals(1, violations.size());
        final ConstraintViolation<Config> violation = violations.iterator().next();
        assertEquals("attribute with dependency reference 'attributeCode' ('missing') which doesn't exist", violation.getMessage());
        assertEquals("attributes[0].dependencies[0].attributeCode", violation.getPropertyPath().toString());
    }
}
