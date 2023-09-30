package io.ajo.responscore.config.validation;

import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.Type;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.validation.annotation.ValidAttribute;
import io.ajo.responscore.validation.ConstraintViolationBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates the {@link Attribute} to ensure fields are set correctly, checks:
 *  - If {@link Type} extends {@link Type#LOOKUP} then {@link Attribute#getLookupCode()} must be set
 *  - If {@link Attribute#getLookupCode()} is set then {@link Type} must extend {@link Type#LOOKUP}
 *  - If {@link Type} extends {@link Type#COMPOSITE} then {@link Attribute#getCompositeCode()} must be set
 *  - If {@link Attribute#getCompositeCode()} is set then {@link Type} must extend {@link Type#COMPOSITE}
 *  - If {@link Attribute#getValidateItems()} is not empty, then {@link Attribute#isList()} must be {@literal true}
 *  - If {@link Validator#getField()} is set, then {@link Attribute#getType()} must be {@link Type#COMPOSITE}
 *  - If {@link Attribute#getDefaultValue()} is set, then value should be coercible to {@link Attribute#getType()}
 */
public class AttributeValidator implements ConstraintValidator<ValidAttribute, Attribute> {

    @Override
    public boolean isValid(Attribute value, ConstraintValidatorContext ctx) {
        boolean valid = true;
        // skip constraint checks if type is missing, there will be separate validation
        if (value.getType() == null) {
            return valid;
        }
        if (value.getType().extendsType(Type.LOOKUP) && StringUtils.isEmpty(value.getLookupCode())) {
            ConstraintViolationBuilder.builder(ctx)
                    .addPropertyNode("lookupCode")
                    .build("{responscore.validation.attribute_validator.no_lookup_code}");
            valid = false;
        }
        if (!StringUtils.isEmpty(value.getLookupCode()) && !value.getType().extendsType(Type.LOOKUP)) {
            ConstraintViolationBuilder.builder(ctx)
                    .addPropertyNode("type")
                    .build("{responscore.validation.attribute_validator.not_lookup_type}");
            valid = false;
        }
        if (value.getType().extendsType(Type.COMPOSITE) && StringUtils.isEmpty(value.getCompositeCode())) {
            ConstraintViolationBuilder.builder(ctx)
                    .addPropertyNode("compositeCode")
                    .build("{responscore.validation.attribute_validator.no_composite_code}");
            valid = false;
        }
        if (!StringUtils.isEmpty(value.getCompositeCode()) && !value.getType().extendsType(Type.COMPOSITE)) {
            ConstraintViolationBuilder.builder(ctx)
                    .addPropertyNode("type")
                    .build("{responscore.validation.attribute_validator.not_composite_type}");
            valid = false;
        }
        if (!value.getValidateItems().isEmpty() && !value.isList()) {
            ConstraintViolationBuilder.builder(ctx)
                    .addPropertyNode("list")
                    .build("{responscore.validation.attribute_validator.validate_items_not_list}");
            valid = false;
        }
        for (int i = 0; i < value.getValidators().size(); i++) {
            final Validator validator = value.getValidators().get(i);
            if (!StringUtils.isEmpty(validator.getField()) && !value.getType().extendsType(Type.COMPOSITE)) {
                ConstraintViolationBuilder.builder(ctx)
                        .addPropertyNode("validators")
                        .addIterableNode(i)
                        .addPropertyNode("field")
                        .build("{responscore.validation.attribute_validator.validator_field_ref_not_composite}");
                valid = false;
            }
        }
        for (int i = 0; i < value.getValidateItems().size(); i++) {
            final Validator validator = value.getValidateItems().get(i);
            if (!StringUtils.isEmpty(validator.getField()) && !value.getType().extendsType(Type.COMPOSITE)) {
                ConstraintViolationBuilder.builder(ctx)
                        .addPropertyNode("validateItems")
                        .addIterableNode(i)
                        .addPropertyNode("field")
                        .build("{responscore.validation.attribute_validator.validate_items_field_ref_not_composite}");
                valid = false;
            }
        }
        if (value.getDefaultValue() != null) {
            try {
                value.getType().coerceType(value.getDefaultValue(), value.isList());
            } catch (IllegalArgumentException ignore) {
                ConstraintViolationBuilder.builder(ctx)
                        .addPropertyNode("default")
                        .addMessageParameter("defaultValue", value.getDefaultValue().toString())
                        .addMessageParameter("type", value.getType().name())
                        .build("{responscore.validation.attribute_validator.incorrect_default_value_type}");
                valid = false;
            }
        }
        return valid;
    }

}
