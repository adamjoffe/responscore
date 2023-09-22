package io.ajo.responscore.config.validation;

import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.Type;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.validation.annotation.ValidAttribute;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates the {@link Attribute} to ensure fields are set correctly, checks:
 *  - If {@link Type} extends {@link Type#LOOKUP} then {@link Attribute#getLookupCode()} must be set
 *  - If {@link Type} extends {@link Type#COMPOSITE} then {@link Attribute#getCompositeCode()} must be set
 *  - If {@link Attribute#getValidateItems()} is not empty, then {@link Attribute#isList()} must be {@literal true}
 *  - If {@link Validator#getField()} is set, then {@link Attribute#getType()} must be {@link Type#COMPOSITE}
 */
public class AttributeValidator implements ConstraintValidator<ValidAttribute, Attribute> {


    @Override
    public boolean isValid(Attribute value, ConstraintValidatorContext ctx) {
        boolean valid = true;
        if (value.getType().extendsType(Type.LOOKUP) && StringUtils.isEmpty(value.getLookupCode())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("{responscore.validation.attribute_validator.no_lookup_code}")
                    .addPropertyNode("lookupCode")
                    .addConstraintViolation();
            valid = false;
        }
        if (value.getType().extendsType(Type.COMPOSITE) && StringUtils.isEmpty(value.getCompositeCode())) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("{responscore.validation.attribute_validator.no_composite_code}")
                    .addPropertyNode("compositeCode")
                    .addConstraintViolation();
            valid = false;
        }
        if (!value.getValidateItems().isEmpty() && !value.isList()) {
            ctx.disableDefaultConstraintViolation();
            ctx.buildConstraintViolationWithTemplate("{responscore.validation.attribute_validator.validate_items_not_list")
                    .addPropertyNode("list")
                    .addConstraintViolation();
            valid = false;
        }
        for (int i = 0; i < value.getValidators().size(); i++) {
            final Validator validator = value.getValidators().get(i);
            if (!StringUtils.isEmpty(validator.getField()) && !value.getType().extendsType(Type.COMPOSITE)) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.attribute_validator.validator_field_ref_not_composite}")
                        .addPropertyNode("validators")
                        .inIterable().atIndex(i)
                        .addPropertyNode("field")
                        .addConstraintViolation();
                valid = false;
            }
        }
        for (int i = 0; i < value.getValidateItems().size(); i++) {
            final Validator validator = value.getValidateItems().get(i);
            if (!StringUtils.isEmpty(validator.getField()) && !value.getType().extendsType(Type.COMPOSITE)) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.attribute_validator.validate_items_field_ref_not_composite}")
                        .addPropertyNode("validateItems")
                        .inIterable().atIndex(i)
                        .addPropertyNode("field")
                        .addConstraintViolation();
                valid = false;
            }
        }
        return valid;
    }

}
