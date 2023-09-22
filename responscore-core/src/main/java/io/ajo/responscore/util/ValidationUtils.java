package io.ajo.responscore.util;

import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidationUtils {

    private static Validator VALIDATOR_INSTANCE;

    /**
     * Get the application instance of the validator
     * @return {@link Validator} with default config
     */
    public static Validator getValidator() {
        if (VALIDATOR_INSTANCE == null) {
            try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
                VALIDATOR_INSTANCE = factory.getValidator();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return VALIDATOR_INSTANCE;
    }

    /**
     * Adds a message interpolation parameter to the constraint validation being created
     * @param ctx {@link ConstraintValidatorContext} which should be of type {@link ConstraintValidatorContextImpl}
     * @param name interpolation parameter name
     * @param value value to interpolate into the message
     */
    public static void addMessageParameter(ConstraintValidatorContext ctx, String name, String value) {
        ((ConstraintValidatorContextImpl) ctx).addMessageParameter(name, value);
    }
}
