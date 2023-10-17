package io.ajo.responscore.service;

import io.ajo.responscore.config.Config;
import io.ajo.responscore.form.Form;
import io.ajo.responscore.service.validation.FormContainer;
import io.ajo.responscore.util.ValidationUtils;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

public class FormService {
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

}
