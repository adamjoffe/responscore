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
import io.ajo.responscore.util.ObjectMapperUtils;
import io.ajo.responscore.util.ValidationUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormService {

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

        // Copy data of the form to mutate during validation, use object mapper to ensure deep copy
        final Map<String, Object> dataCopy = objectMapper.convertValue(form.getData(), new TypeReference<>() {});
        return recursiveDataValidate(config, config.getAttributes(), dataCopy);
    }

    private Set<ConstraintViolation<Object>> recursiveDataValidate(
        Config config,
        Set<Attribute> attributes,
        Map<String, Object> data
    ) {
        final Set<ConstraintViolation<Object>> dataViolations = new HashSet<>();
        for (final Attribute attribute : attributes) {
            if (data.containsKey(attribute.getCode())) {
                // first coerce data to ensure validation can be done
                final Object coercedData;
                try {
                    final Object attrData = data.remove(attribute.getCode());
                    coercedData = attribute.getType().coerceType(attrData, attribute.isList());
                } catch (IllegalArgumentException e) {
                    // TODO: generate constraint violation
                    continue;
                }
                if (!StringUtils.isEmpty(attribute.getLookupCode())) {
                    // safe operation as config validator will ensure there is always a match
                    final LookupConfig lookupConfig = config.getLookupConfigs().stream()
                            .filter(c -> c.getCode().equals(attribute.getLookupCode()))
                            .findAny()
                            .get();
                    dataViolations.addAll(validateLookupDataWithAttribute(attribute, coercedData, lookupConfig));
                } else if (!StringUtils.isEmpty(attribute.getCompositeCode())) {
                    // safe operation as config validator will ensure there is always a match
                    final CompositeTypeConfig compositeTypeConfig = config.getCompositeTypeConfigs().stream()
                            .filter(c -> c.getCode().equals(attribute.getCompositeCode()))
                            .findAny()
                            .get();
                    dataViolations.addAll(validateCompositeDataWithAttribute(config, attribute, coercedData, compositeTypeConfig));
                } else {
                    dataViolations.addAll(validateDataWithAttribute(attribute, coercedData));
                }

                // validate dependencies
                for (Dependent dependent : attribute.getDependencies()) {
                    
                }
            } else {
                if (attribute.isRequired()) {
                    // TODO: generate constraint violation
                }
            }
        }

        // ensure there isn't any unknown data remaining
        if (!data.isEmpty()) {
            // TODO: generate constraint violation
        }

        return dataViolations;
    }

    private Set<ConstraintViolation<Object>> validateLookupDataWithAttribute(
            Attribute attribute,
            Object data,
            LookupConfig lookupConfig
    ) {
        final Set<ConstraintViolation<Object>> violations = validateDataWithAttribute(attribute, data);
        // check to see the data matches a lookup item
        if (lookupConfig.getLookupItems().stream().noneMatch(i -> i.getCode().equals(data))) {
            // TODO: generate constraint violation
        }
        return violations;
    }

    private Set<ConstraintViolation<Object>> validateCompositeDataWithAttribute(
            Config config,
            Attribute attribute,
            Object data,
            CompositeTypeConfig compositeTypeConfig
    ) {
        final Set<ConstraintViolation<Object>> violations = validateDataWithAttribute(attribute, data);
        // check to see the data matches a composite type config via recursion
        violations.addAll(recursiveDataValidate(
                config,
                compositeTypeConfig.getAttributes(),
                objectMapper.convertValue(data, new TypeReference<>() {})
        ));
        return violations;
    }

    private Set<ConstraintViolation<Object>> validateDataWithAttribute(Attribute attribute, Object data) {
        final Set<ConstraintViolation<Object>> violations = new HashSet<>();
        // validators
        for (final Validator v : attribute.getValidators()) {
            if (!v.validate(data)) {
                // TODO: generate constraint violation
            }
        }

        // list validators
        if (attribute.isList()) {
            final Collection<Object> listData = attribute.getType().coerceType(data, true);
            for (final Object elementData : listData) {
                for (final Validator v : attribute.getValidateItems()) {
                    if (!v.validate(elementData)) {
                        // TODO: generate constraint violation
                    }
                }
            }
        }
        return violations;
    }
}
