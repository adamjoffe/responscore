package io.ajo.responscore.config.validation;

import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.CompositeTypeConfig;
import io.ajo.responscore.config.Config;
import io.ajo.responscore.config.Dependent;
import io.ajo.responscore.config.LookupConfig;
import io.ajo.responscore.config.Type;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.validation.annotation.ValidConfig;
import io.ajo.responscore.validation.ConstraintViolationBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

    private final Set<LookupConfig> usedLookupConfigs = new HashSet<>();
    private final Set<CompositeTypeConfig> usedCompositeTypeConfigs = new HashSet<>();

    @Override
    public boolean isValid(Config value, ConstraintValidatorContext ctx) {
        boolean valid = true;
        final ConstraintViolationBuilder builder = ConstraintViolationBuilder.builder(ctx);
        boolean recursiveValid = recursiveCompositeIsValid(builder, value, value.getAttributes());
        // if recursive result is invalid, then propagate
        if (!recursiveValid) {
            valid = false;
        }

        int i = 0;
        // lookup config validation
        for (final LookupConfig lookupConfig : value.getLookupConfigs()) {
            // skip lookup config if code was null
            if (lookupConfig.getCode() == null) {
                continue;
            }
            if (!usedLookupConfigs.contains(lookupConfig)) {
                ConstraintViolationBuilder.builder(ctx)
                        .addPropertyNode("lookupConfigs")
                        .addIterableNode(i)
                        .addMessageParameter("lookupConfig", lookupConfig.getCode())
                        .build("{responscore.validation.config_validator.lookup_config_unused}");
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
            if (!usedCompositeTypeConfigs.contains(compositeTypeConfig)) {
                ConstraintViolationBuilder.builder(ctx)
                        .addPropertyNode("compositeTypeConfigs")
                        .addIterableNode(i)
                        .addMessageParameter("compositeTypeConfig", compositeTypeConfig.getCode())
                        .build("{responscore.validation.config_validator.composite_type_config_unused}");
                valid = false;
            }
        }

        return valid;
    }

    private boolean recursiveCompositeIsValid(ConstraintViolationBuilder ctx, Config config, Set<Attribute> attributes) {
        boolean valid = true;
        int i = 0;
        for (final Attribute attr : attributes) {
            // construct a new instance of the ctx with the new path
            final ConstraintViolationBuilder attrCtx = ConstraintViolationBuilder.from(ctx)
                    .addPropertyNode("attributes")
                    .addIterableNode(i);

            // validation for lookup attribute
            if (attr.getType().extendsType(Type.LOOKUP)) {
                final Optional<LookupConfig> oLookupConfig = config.getLookupConfigs().stream()
                        .filter(c -> c.getCode().equals(attr.getLookupCode()))
                        .findAny();
                if (oLookupConfig.isEmpty()) {
                    ConstraintViolationBuilder.from(attrCtx)
                            .addPropertyNode("lookupCode")
                            .addMessageParameter("lookupCode", attr.getLookupCode())
                            .build("{responscore.validation.config_validator.unknown_lookup_code}");
                    valid = false;
                } else {
                    // add to seen list for later validation
                    usedLookupConfigs.add(oLookupConfig.get());
                }
            }

            // validate for composite attribute
            if (attr.getType().extendsType(Type.COMPOSITE)) {
                final Optional<CompositeTypeConfig> oCompositeTypeConfig = config.getCompositeTypeConfigs().stream()
                        .filter(c -> c.getCode().equals(attr.getCompositeCode()))
                        .findAny();
                if (oCompositeTypeConfig.isEmpty()) {
                    ConstraintViolationBuilder.from(attrCtx)
                            .addPropertyNode("compositeCode")
                            .addMessageParameter("compositeCode", attr.getCompositeCode())
                            .build("{responscore.validation.config_validator.unknown_composite_code}");
                    valid = false;
                } else {
                    final CompositeTypeConfig compositeTypeConfig = oCompositeTypeConfig.get();
                    // add type to seen list for later validation
                    usedCompositeTypeConfigs.add(compositeTypeConfig);
                    for (int j = 0; j < attr.getValidators().size(); j++) {
                        final Validator validator = attr.getValidators().get(j);
                        if (!StringUtils.isEmpty(validator.getField())
                                && compositeTypeConfig.getAttributes().stream().noneMatch(a -> a.getCode().equals(validator.getField()))) {
                            ConstraintViolationBuilder.from(attrCtx)
                                    .addPropertyNode("validators")
                                    .addIterableNode(j)
                                    .addPropertyNode("field")
                                    .addMessageParameter("field", validator.getField())
                                    .addMessageParameter("compositeCode", attr.getCompositeCode())
                                    .build("{responscore.validation.config_validator.unknown_validator_composite_field}");
                            valid = false;
                        }
                    }
                    for (int j = 0; j < attr.getValidateItems().size(); j++) {
                        final Validator validator = attr.getValidateItems().get(j);
                        if (!StringUtils.isEmpty(validator.getField())
                                && compositeTypeConfig.getAttributes().stream().noneMatch(a -> a.getCode().equals(validator.getField()))) {
                            ConstraintViolationBuilder.from(attrCtx)
                                    .addPropertyNode("validateItems")
                                    .addIterableNode(j)
                                    .addPropertyNode("field")
                                    .addMessageParameter("field", validator.getField())
                                    .addMessageParameter("compositeCode", attr.getCompositeCode())
                                    .build("{responscore.validation.config_validator.unknown_validate_item_composite_file}");
                            valid = false;
                        }
                    }

                    // recurse the attributes to further validate
                    boolean recursiveValid = recursiveCompositeIsValid(attrCtx, config, compositeTypeConfig.getAttributes());
                    // if recursive result is invalid, then propagate
                    if (!recursiveValid) {
                        valid = false;
                    }
                }
            }
            if (attr.getDependencies() != null) {
                for (int j = 0; j < attr.getDependencies().size(); j++) {
                    final Dependent dependent = attr.getDependencies().get(j);
                    if (config.getAttributes().stream().noneMatch(a -> a.getCode().equals(dependent.getAttributeCode()))) {
                        ConstraintViolationBuilder.from(attrCtx)
                                .addPropertyNode("dependencies")
                                .addIterableNode(j)
                                .addPropertyNode("attributeCode")
                                .addMessageParameter("attributeCode", dependent.getAttributeCode())
                                .build("{responscore.validation.config_validator.invalid_dependent_reference}");
                        valid = false;
                    }
                }
            }
            i++;
        }
        return valid;
    }
}
