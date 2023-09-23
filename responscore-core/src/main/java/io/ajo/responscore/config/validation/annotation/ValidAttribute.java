package io.ajo.responscore.config.validation.annotation;

import io.ajo.responscore.config.validation.AttributeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AttributeValidator.class)
public @interface ValidAttribute {

    String message() default "{responscore.validation.valid_attribute.default}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
