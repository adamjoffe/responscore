package io.ajo.responscore.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ajo.responscore.config.Attribute;
import io.ajo.responscore.config.CompositeTypeConfig;
import io.ajo.responscore.config.Config;
import io.ajo.responscore.config.Dependent;
import io.ajo.responscore.config.LookupConfig;
import io.ajo.responscore.config.Validator;
import io.ajo.responscore.form.Form;
import io.ajo.responscore.service.validation.annotation.ValidForm;
import io.ajo.responscore.util.ObjectMapperUtils;
import io.ajo.responscore.util.ValidationUtils;
import io.ajo.responscore.validation.ConstraintViolationBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormService implements ConstraintValidator<ValidForm, FormService.FormContainer> {

    private final ObjectMapper objectMapper = ObjectMapperUtils.getObjectMapper();
    private final javax.validation.Validator validator = ValidationUtils.getValidator();

    public Set<ConstraintViolation<Object>> validateFormWithConfig(Config config, Form form) {

        // validate parameters are valid objects
        final Set<ConstraintViolation<Object>> paramViolations = new HashSet<>();
        paramViolations.addAll(validator.validate(config));
        paramViolations.addAll(validator.validate(form));
        if (!paramViolations.isEmpty()) {
            return paramViolations;
        }

        final FormContainer container = new FormContainer(config, form);
        paramViolations.addAll(validator.validate(container));

        return paramViolations;
    }

    @Override
    public boolean isValid(FormContainer value, ConstraintValidatorContext ctx) {
        // Copy data of the form to mutate during validation, use object mapper to ensure deep copy
        final Map<String, Object> dataCopy = objectMapper.convertValue(value.form.getData(), new TypeReference<>() {});
        final ConstraintViolationBuilder builder = ConstraintViolationBuilder.builder(ctx).addPropertyNode("data");
        return recursiveDataValidate(value.config, value.config.getAttributes(), dataCopy, builder);
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
                for (Dependent dependent : attribute.getDependencies()) {
                    final Object dependeeValue = dependencyData.get(dependent.getAttributeCode());
                    if (dependent.getValues().stream().noneMatch(v -> v.equals(dependeeValue))) {
                        ConstraintViolationBuilder.from(attrCtx)
                                .addMessageParameter("dependeeAttr", dependent.getAttributeCode())
                                .build("{responscore.validation.form_validator.unmet_dependencies}");
                        valid = false;
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
            ConstraintViolationBuilder.from(ctx)
                    .build("{responscore.validation.form_validator.unknown_data}");
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
        // check to see the data matches a lookup item
        if (lookupConfig.getLookupItems().stream().noneMatch(i -> i.getCode().equals(data))) {
            ConstraintViolationBuilder.from(ctx)
                    .addMessageParameter("dataValue", data.toString())
                    .build("{responscore.validation.form_validator.invalid_lookup_value}");
            valid = false;
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
        return valid;
    }

    private boolean validateDataWithAttribute(Attribute attribute, Object data, ConstraintViolationBuilder ctx) {
        boolean valid = true;
        // validators
        for (final Validator v : attribute.getValidators()) {
            if (!v.validate(data)) {
                ConstraintViolationBuilder.from(ctx)
                        .addMessageParameter("validatorType", v.getType().name())
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
                                .build("{responscore.validation.form_validator.invalid_list_data}");
                        valid = false;
                    }
                }
                i++;
            }
        }
        return valid;
    }

    // wrap the config and form together to use validation framework
    @ValidForm
    protected record FormContainer(
       Config config,
       Form form
    ) {}

}
