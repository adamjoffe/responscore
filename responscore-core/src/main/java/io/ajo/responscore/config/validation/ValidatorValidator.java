package io.ajo.responscore.config.validation;


import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.ValidatorType;
import io.ajo.responscore.config.validation.annotation.ValidValidator;
import io.ajo.responscore.validation.ConstraintViolationBuilder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validates the {@link Validator} to ensure fields are set correctly based on the {@link ValidatorType} used.
 * Following checks:
 *  - {@code value} field is set when {@code type} is, Min, Max, GreaterTHan, LessThan, otherwise fail if field set
 *
 */
public class ValidatorValidator implements ConstraintValidator<ValidValidator, Validator> {

    @Override
    public boolean isValid(Validator value, ConstraintValidatorContext ctx) {
        // skip constraint checks if type is missing, there will be separate validation
        if (value.getType() == null) {
            return true;
        }
        switch (value.getType()) {
            case Min, Max, GreaterThan, LessThan, MinSize, MaxSize -> {
                if (value.getValue() == null) {
                    ConstraintViolationBuilder.builder(ctx)
                            .addPropertyNode("value")
                            .addMessageParameter("type", value.getType().name())
                            .build("{responscore.validation.validator_validator.no_value}");
                    return false;
                }
            }
            default -> {
                if (value.getValue() != null) {
                    ConstraintViolationBuilder.builder(ctx)
                            .addPropertyNode("value")
                            .addMessageParameter("type", value.getType().name())
                            .build("{responscore.validation.validator_validator.value_set}");
                    return false;
                }
            }
        }
        return true;
    }

}
