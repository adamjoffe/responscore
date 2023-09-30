package io.ajo.responscore.validation;


import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class ConstraintViolationBuilder {

    private final ConstraintValidatorContext ctx;
    private final List<PropertyPathSegment> segments = new ArrayList<>();

    private ConstraintViolationBuilder(ConstraintValidatorContext ctx) {
        this.ctx = ctx;
    }

    public static ConstraintViolationBuilder builder(ConstraintValidatorContext ctx) {
        return new ConstraintViolationBuilder(ctx);
    }

    /**
     * Creates a new instance of the builder from an existing instance (clone)
     * @param other builder to clone
     * @return cloned constraint builder
     */
    public static ConstraintViolationBuilder from(ConstraintViolationBuilder other) {
        final ConstraintViolationBuilder clone = builder(other.ctx);
        clone.segments.addAll(other.segments);
        return clone;
    }

    /**
     * Adds a message interpolation parameter to the constraint validation being created
     * @param parameter interpolation parameter name
     * @param value value to interpolate into the message
     * @return this instance for chaining
     */
    public ConstraintViolationBuilder addMessageParameter(String parameter, String value) {
        ((ConstraintValidatorContextImpl) ctx).addMessageParameter(parameter, value);
        return this;
    }

    /**
     * Add a property node, this is ordered and determines the resulting property path
     * @param propertyNode name of node to add
     * @return this instance for chaining
     */
    public ConstraintViolationBuilder addPropertyNode(String propertyNode) {
        segments.add(new PropertyNodeSegment(propertyNode));
        return this;
    }

    /**
     * Add an iterable node, this is ordered and determines the resulting property path
     * @param index index of the iterable
     * @return this instance for chaining
     */
    public ConstraintViolationBuilder addIterableNode(Integer index) {
        segments.add(new IterableNodeSegment(index));
        return this;
    }

    /**
     * Terminating operation
     */
    public void build(String messageTemplate) {
        final ConstraintValidatorContext.ConstraintViolationBuilder builder = ctx.buildConstraintViolationWithTemplate(messageTemplate);
        if (segments.isEmpty()) {
            builder.addConstraintViolation().disableDefaultConstraintViolation();
        } else {
            ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeCtx = segments.get(0).emitPropertyPath(builder);
            // start after initial segments, loop over remaining
            for (int i = 1; i < segments.size(); i++) {
                nodeCtx = segments.get(i).emitPropertyPath(nodeCtx);
            }
            nodeCtx.addConstraintViolation().disableDefaultConstraintViolation();
        }
    }
}
