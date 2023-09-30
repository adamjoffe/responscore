package io.ajo.responscore.validation;

import javax.validation.ConstraintValidatorContext;
import java.util.List;

public record IterableNodeSegment(Integer index) implements PropertyPathSegment {
    @Override
    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext emitPropertyPath(
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext ctx
    ) {
        return ctx.addPropertyNode(null).inIterable().atIndex(index).addPropertyNode(null);
    }

    @Override
    public ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext emitPropertyPath(
            ConstraintValidatorContext.ConstraintViolationBuilder ctx
    ) {
        return ctx.addPropertyNode(null).inContainer(List.class, index);
    }
}
