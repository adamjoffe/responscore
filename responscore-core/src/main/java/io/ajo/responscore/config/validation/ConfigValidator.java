package io.ajo.responscore.config.validation;

import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.CompositeTypeConfig;
import io.ajo.responscore.config.Config;
import io.ajo.responscore.config.Dependent;
import io.ajo.responscore.config.LookupConfig;
import io.ajo.responscore.config.Type;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.validation.annotation.ValidConfig;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static io.ajo.responscore.util.ValidationUtils.addMessageParameter;

/**
 * Validates the {@link Config} to ensure fields are set correctly, checks:
 *  - If {@link Attribute} extends {@link Type#LOOKUP} then {@link Attribute#getLookupCode()} must reference a {@link LookupConfig}
 *  - If {@link LookupConfig} is defined but not used by any {@link Attribute}
 *  - If {@link Attribute} extends {@link Type#COMPOSITE} then {@link Attribute#getCompositeCode()} must reference a {@link CompositeTypeConfig}
 *  - If {@link Attribute} extends {@link Type#COMPOSITE} and has {@link Validator#getField()} set, then field must be part of {@link CompositeTypeConfig}
 *  - If {@link CompositeTypeConfig} is defined but not used by any {@link Attribute}
 *  - If {@link Attribute} has {@link Attribute#getDependencies()} then {@link Dependent#getAttributeCode()} must reference another {@link Attribute#getCode}
 */
public class ConfigValidator implements ConstraintValidator<ValidConfig, Config> {

    @Override
    public boolean isValid(Config value, ConstraintValidatorContext ctx) {
        boolean valid = true;
        int i = 0;
        // lookup config validation
        for (final LookupConfig lookupConfig : value.getLookupConfigs()) {
            // skip lookup config if code was null
            if (lookupConfig.getCode() == null) {
                continue;
            }
            if (value.getAttributes().stream().noneMatch(a -> lookupConfig.getCode().equals(a.getLookupCode()))) {
                ctx.disableDefaultConstraintViolation();
                addMessageParameter(ctx, "lookupConfig", lookupConfig.getCode());
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.lookup_config_unused}")
                        .addPropertyNode("lookupConfigs")
                        .addPropertyNode(null)
                        .inIterable().atIndex(i)
                        .addConstraintViolation();
                valid = false;
            }
            i++;
        }

        // composite type config validation
        i = 0;
        for (final CompositeTypeConfig compositeTypeConfig : value.getCompositeTypeConfigs()) {
            // skip composite type config if code was null
            if (compositeTypeConfig.getCode() == null) {
                continue;
            }
            if (value.getAttributes().stream().noneMatch(a -> compositeTypeConfig.getCode().equals(a.getCompositeCode()))) {
                ctx.disableDefaultConstraintViolation();
                addMessageParameter(ctx, "compositeTypeConfig", compositeTypeConfig.getCode());
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.composite_type_config_unused}")
                        .addPropertyNode("compositeTypeConfigs")
                        .addPropertyNode(null)
                        .inIterable().atIndex(i)
                        .addConstraintViolation();
                valid = false;
            }
        }

        // attribute validation
        i = 0;
        for (final Attribute attr : value.getAttributes()) {
            if (attr.getType().extendsType(Type.LOOKUP)
                && value.getLookupConfigs().stream().noneMatch(c -> c.getCode().equals(attr.getLookupCode()))
            ) {
                ctx.disableDefaultConstraintViolation();
                addMessageParameter(ctx, "lookupCode", attr.getLookupCode());
                ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.unknown_lookup_code}")
                        .addPropertyNode("attributes")
                        .addPropertyNode(null)
                        .inIterable().atIndex(i)
                        .addPropertyNode("lookupCode")
                        .addConstraintViolation();
                valid = false;
            }
            if (attr.getType().extendsType(Type.COMPOSITE)) {
                if (value.getCompositeTypeConfigs().stream().noneMatch(c -> c.getCode().equals(attr.getCompositeCode()))) {
                    ctx.disableDefaultConstraintViolation();
                    addMessageParameter(ctx, "compositeCode", attr.getCompositeCode());
                    ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.unknown_composite_code}")
                            .addPropertyNode("attributes")
                            .addPropertyNode(null)
                            .inIterable().atIndex(i)
                            .addPropertyNode("compositeCode")
                            .addConstraintViolation();
                    valid = false;
                } else {
                    final CompositeTypeConfig compositeTypeConfig = value.getCompositeTypeConfigs().stream()
                            .filter(c -> c.getCode().equals(attr.getCompositeCode())).findAny().get();
                    for (int j = 0; j < attr.getValidators().size(); j++) {
                        final Validator validator = attr.getValidators().get(j);
                        if (!StringUtils.isEmpty(validator.getField())
                                && compositeTypeConfig.getAttributes().stream().noneMatch(a -> a.getCode().equals(validator.getField()))) {
                            ctx.disableDefaultConstraintViolation();
                            addMessageParameter(ctx, "field", validator.getField());
                            addMessageParameter(ctx, "compositeCode", attr.getCompositeCode());
                            ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.unknown_validator_composite_field}")
                                    .addPropertyNode("attributes")
                                    .addPropertyNode(null)
                                    .inIterable().atIndex(i)
                                    .addPropertyNode("validators")
                                    .addPropertyNode(null)
                                    .inIterable().atIndex(j)
                                    .addPropertyNode("field")
                                    .addConstraintViolation();
                            valid = false;
                        }
                    }
                    for (int j = 0; j < attr.getValidateItems().size(); j++) {
                        final Validator validator = attr.getValidateItems().get(j);
                        if (!StringUtils.isEmpty(validator.getField())
                                && compositeTypeConfig.getAttributes().stream().noneMatch(a -> a.getCode().equals(validator.getField()))) {
                            ctx.disableDefaultConstraintViolation();
                            addMessageParameter(ctx, "field", validator.getField());
                            addMessageParameter(ctx, "compositeCode", attr.getCompositeCode());
                            ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.unknown_validate_item_composite_file}")
                                    .addPropertyNode("attributes")
                                    .addPropertyNode(null)
                                    .inIterable().atIndex(i)
                                    .addPropertyNode("validateItems")
                                    .addPropertyNode(null)
                                    .inIterable().atIndex(j)
                                    .addPropertyNode("field")
                                    .addConstraintViolation();
                            valid = false;
                        }
                    }
                }
            }
            if (attr.getDependencies() != null) {
                for (int j = 0; j < attr.getDependencies().size(); j++) {
                    final Dependent dependent = attr.getDependencies().get(j);
                    if (value.getAttributes().stream().noneMatch(a -> a.getCode().equals(dependent.getAttributeCode()))) {
                        ctx.disableDefaultConstraintViolation();
                        addMessageParameter(ctx, "attributeCode", dependent.getAttributeCode());
                        ctx.buildConstraintViolationWithTemplate("{responscore.validation.config_validator.invalid_dependent_reference}")
                                .addPropertyNode("attributes")
                                .addPropertyNode(null)
                                .inIterable().atIndex(i)
                                .addPropertyNode("dependencies")
                                .addPropertyNode(null)
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
