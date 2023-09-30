package io.ajo.responscore.validation;

import javax.validation.ConstraintValidatorContext;

public interface PropertyPathSegment {

    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext emitPropertyPath(
        ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext ctx
    );

    ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext emitPropertyPath(
        ConstraintValidatorContext.ConstraintViolationBuilder ctx
    );

}
