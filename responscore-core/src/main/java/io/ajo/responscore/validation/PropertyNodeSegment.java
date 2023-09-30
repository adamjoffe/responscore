package io.ajo.responscore.validation;

import javax.validation.ConstraintValidatorContext;

public record PropertyNodeSegment(String propertyNode) implements PropertyPathSegment {

    @Override
    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext emitPropertyPath(
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext ctx
    ) {
        return ctx.addPropertyNode(propertyNode);
    }

    @Override
    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext emitPropertyPath(
            ConstraintValidatorContext.ConstraintViolationBuilder ctx
    ) {
        return ctx.addPropertyNode(propertyNode);
    }
}
