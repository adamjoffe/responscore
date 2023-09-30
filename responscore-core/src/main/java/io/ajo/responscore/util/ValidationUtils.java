package io.ajo.responscore.util;

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
}
