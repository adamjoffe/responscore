package io.ajo.responscore.config;

import io.ajo.responscore.config.validation.annotation.ValidAttribute;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a single attribute which are combined to compose the fields on the form
 */
@Data
@Builder
@ValidAttribute
@EqualsAndHashCode(of = {"code"})
public class Attribute {

    /**
     * Unique identifiable code which identifies this attribute. This is used when referencing an attribute via other
     * parts of the config, i.e. {@link Validator} or {@link Dependent}. This code is also used as the key of the
     * attribute on the {@link io.ajo.responscore.form.Form}.
     */
    @NotBlank
    private String code;
    /**
     * Human-readable label for this attribute, this is displayed to the end user the question or ask of the attribute
     */
    @NotBlank
    private String label;
    /**
     * Optional tooltip for the end user to provide further aid to the question or ask of the attribute by the label
     */
    private String tooltip;

    /**
     * The declared type
     */
    @NotNull
    private Type type;

    /**
     * When type is {@link Type#LOOKUP}, this field sets the lookup reference for this attribute
     */
    private String lookupCode;
    /**
     * When type is {@link Type#COMPOSITE}, this field sets the composite reference for this attribute
     */
    private String compositeCode;

    /**
     * If the attribute is required for the form
     */
    private boolean required;
    /**
     * If the attribute is a list of values
     */
    private boolean list;
    /**
     * Default value for the attribute
     */
    private Object defaultValue;

    /**
     * List of {@link Validator} to validate the attribute
     */
    @Valid
    @NotNull
    @Builder.Default
    private List<Validator> validators = new ArrayList<>();
    /**
     * List of {@link Validator} to validate each item in a list
     * This is only valid when {@link this#list} is {@literal true}
     */
    @Valid
    @NotNull
    @Builder.Default
    private List<Validator> validateItems = new ArrayList<>();

    /**
     * List of dependents for this attribute. This indicates which fields and values for those fields must be
     * set in order for this field to be considered "active"
     */
    @Valid
    private List<Dependent> dependencies;

}
