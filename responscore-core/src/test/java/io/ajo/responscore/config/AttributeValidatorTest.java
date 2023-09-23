package io.ajo.responscore.config;

import io.ajo.responscore.util.ValidationUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Attribute Validator Test")
public class AttributeValidatorTest {

    private final javax.validation.Validator validator = ValidationUtils.getValidator();

    @Test
    @DisplayName("Valid Attribute")
    public void validAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.INTEGER)
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Attribute Null Fields")
    public void invalidAttributeNullFields() {
        final Attribute attr = Attribute.builder()
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(3, violations.size());
        final Optional<ConstraintViolation<Attribute>> oTypeViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("type")).findAny();
        assertTrue(oTypeViolation.isPresent());
        assertEquals("must not be null", oTypeViolation.get().getMessage());
        final Optional<ConstraintViolation<Attribute>> oLabelViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("label")).findAny();
        assertTrue(oLabelViolation.isPresent());
        assertEquals("must not be blank", oLabelViolation.get().getMessage());
        final Optional<ConstraintViolation<Attribute>> oCodeViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("code")).findAny();
        assertTrue(oCodeViolation.isPresent());
        assertEquals("must not be blank", oCodeViolation.get().getMessage());
    }

    @Test
    @DisplayName("Valid Lookup Attribute")
    public void validLookupAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.LOOKUP)
                .lookupCode("lookupCode")
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Lookup Attribute")
    public void invalidLookupAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.LOOKUP)
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(1, violations.size());
        final ConstraintViolation<Attribute> violation = violations.iterator().next();
        assertEquals("attribute of or extending type `LOOKUP` must have 'lookupCode' field set validly", violation.getMessage());
        assertEquals("lookupCode", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Invalid Attribute With LookupCode Set")
    public void invalidAttributeWithLookupCodeSet() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .lookupCode("lookupCode")
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(1, violations.size());
        final ConstraintViolation<Attribute> violation = violations.iterator().next();
        assertEquals("attribute has 'lookupCode' field set, but type doesn't extend `LOOKUP`", violation.getMessage());
        assertEquals("type", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Composite Attribute")
    public void validCompositeAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.COMPOSITE)
                .compositeCode("compositeCode")
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Composite Attribute")
    public void invalidCompositeAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.COMPOSITE)
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(1, violations.size());
        final ConstraintViolation<Attribute> violation = violations.iterator().next();
        assertEquals("attribute of or extending type `COMPOSITE` must have 'compositeCode' field set validly", violation.getMessage());
        assertEquals("compositeCode", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Invalid Attribute With CompositeCode Set")
    public void invalidAttributeWithCompositeCodeSet() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .compositeCode("compositeCode")
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(1, violations.size());
        final ConstraintViolation<Attribute> violation = violations.iterator().next();
        assertEquals("attribute has 'compositeCode' field set, but type doesn't extend `COMPOSITE`", violation.getMessage());
        assertEquals("type", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Validate Items List Attribute")
    public void validValidateItemsListAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .list(true)
                .validateItems(List.of(
                        Validator.builder()
                                .type(ValidatorType.NotBlank)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Validate Items List Attribute")
    public void invalidValidateItemsListAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .validateItems(List.of(
                        Validator.builder()
                                .type(ValidatorType.NotBlank)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(1, violations.size());
        final ConstraintViolation<Attribute> violation = violations.iterator().next();
        assertEquals("attribute with field 'validateItems' shouldn't have entries unless 'list' is set to `true`", violation.getMessage());
        assertEquals("list", violation.getPropertyPath().toString());
    }

    @Test
    @DisplayName("Valid Field Validator Attribute")
    public void validFieldValidatorAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.COMPOSITE)
                .compositeCode("compositeCode")
                .validators(List.of(
                        Validator.builder()
                                .type(ValidatorType.NotNull)
                                .field("fieldA")
                                .build(),
                        Validator.builder()
                                .type(ValidatorType.Min)
                                .field("fieldB")
                                .value(BigDecimal.ZERO)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Field Validator Attribute")
    public void invalidFieldValidatorAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .validators(List.of(
                        Validator.builder()
                                .type(ValidatorType.NotNull)
                                .field("fieldA")
                                .build(),
                        Validator.builder()
                                .type(ValidatorType.Min)
                                .field("fieldB")
                                .value(BigDecimal.ZERO)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(2, violations.size());
        final Optional<ConstraintViolation<Attribute>> oFieldAViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("validators[0].field")).findAny();
        assertTrue(oFieldAViolation.isPresent());
        assertEquals("attribute with validator has 'field' set must be of type `COMPOSITE`", oFieldAViolation.get().getMessage());

        final Optional<ConstraintViolation<Attribute>> oFieldBViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("validators[1].field")).findAny();
        assertTrue(oFieldBViolation.isPresent());
        assertEquals("attribute with validator has 'field' set must be of type `COMPOSITE`", oFieldBViolation.get().getMessage());
    }

    @Test
    @DisplayName("Valid Field Items Validator Attribute")
    public void validFieldItemsValidatorAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.COMPOSITE)
                .compositeCode("compositeCode")
                .list(true)
                .validateItems(List.of(
                        Validator.builder()
                                .type(ValidatorType.NotNull)
                                .field("fieldA")
                                .build(),
                        Validator.builder()
                                .type(ValidatorType.Min)
                                .field("fieldB")
                                .value(BigDecimal.ZERO)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(0, violations.size());
    }

    @Test
    @DisplayName("Invalid Field Items Validator Attribute")
    public void invalidFieldItemsValidatorAttribute() {
        final Attribute attr = Attribute.builder()
                .code("code")
                .label("label")
                .type(Type.STRING)
                .list(true)
                .validateItems(List.of(
                        Validator.builder()
                                .type(ValidatorType.NotNull)
                                .field("fieldA")
                                .build(),
                        Validator.builder()
                                .type(ValidatorType.Min)
                                .field("fieldB")
                                .value(BigDecimal.ZERO)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<Attribute>> violations = validator.validate(attr);

        assertEquals(2, violations.size());
        final Optional<ConstraintViolation<Attribute>> oFieldAViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("validateItems[0].field")).findAny();
        assertTrue(oFieldAViolation.isPresent());
        assertEquals("attribute with item validator has 'field' set must be of type `COMPOSITE`", oFieldAViolation.get().getMessage());

        final Optional<ConstraintViolation<Attribute>> oFieldBViolation = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("validateItems[1].field")).findAny();
        assertTrue(oFieldBViolation.isPresent());
        assertEquals("attribute with item validator has 'field' set must be of type `COMPOSITE`", oFieldBViolation.get().getMessage());
    }
}
