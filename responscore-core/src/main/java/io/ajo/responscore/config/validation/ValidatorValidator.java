package io.ajo.responscore.config.validation;


import io.ajo.responscore.config.Validator;
import io.ajo.responscore.config.ValidatorType;
import io.ajo.responscore.config.validation.annotation.ValidValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static io.ajo.responscore.util.ValidationUtils.addMessageParameter;

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
                    ctx.disableDefaultConstraintViolation();
                    addMessageParameter(ctx, "type", value.getType().name());
                    ctx.buildConstraintViolationWithTemplate("{responscore.validation.validator_validator.no_value}")
                            .addPropertyNode("value")
                            .addConstraintViolation();
                    return false;
                }
            }
            default -> {
                if (value.getValue() != null) {
                    ctx.disableDefaultConstraintViolation();
                    addMessageParameter(ctx, "type", value.getType().name());
                    ctx.buildConstraintViolationWithTemplate("{responscore.validation.validator_validator.value_set}")
                            .addPropertyNode("value")
                            .addConstraintViolation();
                    return false;
                }
            }
        }
        return true;
    }

}
