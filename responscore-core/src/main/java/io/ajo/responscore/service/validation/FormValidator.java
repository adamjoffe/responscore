package io.ajo.responscore.service.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.CompositeTypeConfig;
import io.ajo.responscore.config.Config;
import io.ajo.responscore.config.Dependent;
import io.ajo.responscore.config.LookupConfig;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.service.validation.annotation.ValidForm;
import io.ajo.responscore.util.ObjectMapperUtils;
import io.ajo.responscore.validation.ConstraintViolationBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FormValidator implements ConstraintValidator<ValidForm, FormContainer> {

    private final ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();

    @Override
    public boolean isValid(FormContainer value, ConstraintValidatorContext ctx) {
        // Copy data of the form to mutate during validation, use object mapper to ensure deep copy
        final Map<String, Object> dataCopy = objectMapper.convertValue(value.form().getData(), new TypeReference<>() {});
        final ConstraintViolationBuilder builder = ConstraintViolationBuilder.builder(ctx).addPropertyNode("data");
        return recursiveDataValidate(value.config(), value.config().getAttributes(), dataCopy, builder);
    }


    private boolean recursiveDataValidate(
            Config config,
            Set<Attribute> attributes,
            Map<String, Object> data,
            ConstraintViolationBuilder ctx
    ) {
        boolean valid = true;
        // copy data to use only for dependency checks
        final Map<String, Object> dependencyData = objectMapper.convertValue(data, new TypeReference<>() {});
        for (final Attribute attribute : attributes) {
            final ConstraintViolationBuilder attrCtx = ConstraintViolationBuilder.from(ctx)
                    .addPropertyNode(attribute.getCode());
            if (data.containsKey(attribute.getCode())) {
                // first coerce data to ensure validation can be done
                final Object coercedData;
                try {
                    final Object attrData = data.remove(attribute.getCode());
                    coercedData = attribute.getType().coerceType(attrData, attribute.isList());
                } catch (IllegalArgumentException e) {
                    ConstraintViolationBuilder.from(attrCtx)
                            .addMessageParameter("attributeType", attribute.getType().name())
                            .build("{responscore.validation.form_validator.invalid_data_type}");
                    valid = false;
                    continue;
                }
                if (!StringUtils.isEmpty(attribute.getLookupCode())) {
                    // safe operation as config validator will ensure there is always a match
                    final LookupConfig lookupConfig = config.getLookupConfigs().stream()
                            .filter(c -> c.getCode().equals(attribute.getLookupCode()))
                            .findAny()
                            .get();
                    final boolean lookupValid = validateLookupDataWithAttribute(attribute, coercedData, lookupConfig, attrCtx);
                    if (!lookupValid) {
                        valid = false;
                    }
                } else if (!StringUtils.isEmpty(attribute.getCompositeCode())) {
                    // safe operation as config validator will ensure there is always a match
                    final CompositeTypeConfig compositeTypeConfig = config.getCompositeTypeConfigs().stream()
                            .filter(c -> c.getCode().equals(attribute.getCompositeCode()))
                            .findAny()
                            .get();
                    final boolean compositeValid = validateCompositeDataWithAttribute(config, attribute, coercedData, compositeTypeConfig, attrCtx);
                    if (!compositeValid) {
                        valid = false;
                    }
                } else {
                    final boolean dataValid = validateDataWithAttribute(attribute, coercedData, attrCtx);
                    if (!dataValid) {
                        valid = false;
                    }
                }

                // validate dependencies
                if (attribute.getDependencies() != null) {
                    for (Dependent dependent : attribute.getDependencies()) {
                        final Object dependeeValue = dependencyData.get(dependent.getAttributeCode());
                        if (dependent.getValues().stream().noneMatch(v -> v.equals(dependeeValue))) {
                            ConstraintViolationBuilder.from(attrCtx)
                                    .addMessageParameter("dependeeAttr", dependent.getAttributeCode())
                                    .build("{responscore.validation.form_validator.unmet_dependencies}");
                            valid = false;
                        }
                    }
                }
            } else {
                if (attribute.isRequired()) {
                    ConstraintViolationBuilder.from(attrCtx)
                            .build("{responscore.validation.form_validator.missing_required_data}");
                    valid = false;
                }
            }
        }

        // ensure there isn't any unknown data remaining
        if (!data.isEmpty()) {
            for (final String key : data.keySet()) {
                ConstraintViolationBuilder.from(ctx)
                        .addMessageParameter("dataKey", key)
                        .build("{responscore.validation.form_validator.unknown_data}");
            }
            valid = false;
        }

        return valid;
    }

    private boolean validateLookupDataWithAttribute(
            Attribute attribute,
            Object data,
            LookupConfig lookupConfig,
            ConstraintViolationBuilder ctx
    ) {
        boolean valid = validateDataWithAttribute(attribute, data, ctx);
        // handle list differently
        if (attribute.isList()) {
            final Collection<Object> listData = attribute.getType().coerceType(data, true);
            int i = 0;
            for (Object elemData : listData) {
                // check to see the data matches a lookup item
                if (lookupConfig.getLookupItems().stream().noneMatch(li -> li.getCode().equals(elemData))) {
                    ConstraintViolationBuilder.from(ctx)
                            .addIterableNode(i)
                            .addMessageParameter("dataValue", elemData.toString())
                            .build("{responscore.validation.form_validator.invalid_lookup_value}");
                    valid = false;
                }
                i++;
            }
        } else {
            // check to see the data matches a lookup item
            if (lookupConfig.getLookupItems().stream().noneMatch(li -> li.getCode().equals(data))) {
                ConstraintViolationBuilder.from(ctx)
                        .addMessageParameter("dataValue", data.toString())
                        .build("{responscore.validation.form_validator.invalid_lookup_value}");
                valid = false;
            }
        }
        return valid;
    }

    private boolean validateCompositeDataWithAttribute(
            Config config,
            Attribute attribute,
            Object data,
            CompositeTypeConfig compositeTypeConfig,
            ConstraintViolationBuilder ctx
    ) {
        boolean valid = validateDataWithAttribute(attribute, data, ctx);
        // handle list differently
        if (attribute.isList()) {
            final Collection<Object> listData = attribute.getType().coerceType(data, true);
            int i = 0;
            for (Object elemData : listData) {
                final ConstraintViolationBuilder elemCtx = ConstraintViolationBuilder.from(ctx).addIterableNode(i);
                final boolean recursiveValid = recursiveDataValidate(
                        config,
                        compositeTypeConfig.getAttributes(),
                        objectMapper.convertValue(elemData, new TypeReference<>() {}),
                        elemCtx
                );
                if (!recursiveValid) {
                    valid = false;
                }
                i++;
            }
        } else {
            // check to see the data matches a composite type config via recursion
            final boolean recursiveValid = recursiveDataValidate(
                    config,
                    compositeTypeConfig.getAttributes(),
                    objectMapper.convertValue(data, new TypeReference<>() {}),
                    ctx
            );
            if (!recursiveValid) {
                valid = false;
            }
        }
        return valid;
    }

    private boolean validateDataWithAttribute(Attribute attribute, Object data, ConstraintViolationBuilder ctx) {
        boolean valid = true;
        // validators
        for (final Validator v : attribute.getValidators()) {
            if (!v.validate(data)) {
                ConstraintViolationBuilder.from(ctx)
                        .addMessageParameter("validatorType", v.getType().name())
                        .addMessageParameter("validatorCondition", ConditionString.format(v))
                        .build("{responscore.validation.form_validator.invalid_data}");
                valid = false;
            }
        }

        // list validators
        if (attribute.isList()) {
            final Collection<Object> listData = attribute.getType().coerceType(data, true);
            int i = 0;
            for (final Object elementData : listData) {
                for (final Validator v : attribute.getValidateItems()) {
                    if (!v.validate(elementData)) {
                        ConstraintViolationBuilder.from(ctx)
                                .addIterableNode(i)
                                .addMessageParameter("validatorType", v.getType().name())
                                .addMessageParameter("validatorCondition", ConditionString.format(v))
                                .build("{responscore.validation.form_validator.invalid_list_data}");
                        valid = false;
                    }
                }
                i++;
            }
        }
        return valid;
    }

}
