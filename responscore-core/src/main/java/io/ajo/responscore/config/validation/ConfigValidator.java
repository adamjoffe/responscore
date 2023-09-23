package io.ajo.responscore.config.validation;

import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.Config;
import io.ajo.responscore.config.CompositeTypeConfig;
import io.ajo.responscore.config.Dependent;
import io.ajo.responscore.config.LookupConfig;
import io.ajo.responscore.config.Type;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.validation.annotation.ValidConfig;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates the {@link Config} to ensure fields are set correctly, checks:
 *  - If {@link Attribute} extends {@link Type#LOOKUP} then {@link Attribute#getLookupCode()} must reference a {@link LookupConfig}
 *  - If {@link Attribute} extends {@link Type#COMPOSITE} then {@link Attribute#getCompositeCode()} must reference a {@link CompositeTypeConfig}
 *  - If {@link Attribute} extends {@link Type#COMPOSITE} and has {@link Validator#getField()} set, then field must be part of {@link CompositeTypeConfig}
 *  - If {@link Attribute} has {@link Attribute#getDependencies()} then {@link Dependent#getAttributeCode()} must reference another {@link Attribute#getCode}
 */
public class ConfigValidator implements ConstraintValidator<ValidConfig, Config> {

    @Override
    public boolean isValid(Config value, ConstraintValidatorContext ctx) {
        boolean valid = true;
        int i = 0;
        for (final Attribute attr : value.getAttributes()) {
            if (attr.getType().extendsType(Type.LOOKUP)
                && value.getLookupConfigs().stream().noneMatch(c -> c.getCode().equals(attr.getLookupCode()))
            ) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.unknown_lookup_code}")
                        .addPropertyNode("attributes")
                        .inIterable().atIndex(i)
                        .addPropertyNode("lookupCode")
                        .addConstraintViolation();
                valid = false;
            }
            if (attr.getType().extendsType(Type.COMPOSITE)
                && value.getCompositeTypeConfigs().stream().noneMatch(c -> c.getCode().equals(attr.getCompositeCode()))
            ) {
                ctx.disableDefaultConstraintViolation();
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.unknown_composite_code}")
                        .addPropertyNode("attributes")
                        .inIterable().atIndex(i)
                        .addPropertyNode("compositeCode")
                        .addConstraintViolation();
                valid = false;
            }
            if (attr.getDependencies() != null) {
                for (int j = 0; j < attr.getDependencies().size(); j++) {
                    final Dependent dependent = attr.getDependencies().get(j);
                    if (value.getAttributes().stream().noneMatch(a -> a.getCode().equals(dependent.getAttributeCode()))) {
                        ctx.disableDefaultConstraintViolation();
                        ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.invalid_dependent_reference}")
                                .addPropertyNode("attributes")
                                .inIterable().atIndex(i)
                                .addPropertyNode("dependencies")
                                .inIterable().atIndex(j)
                                .addPropertyNode("attributeCode")
                                .addConstraintViolation();
                        valid = false;
                    }
                }
            }
            i++;
        }
        return valid;
    }
}
