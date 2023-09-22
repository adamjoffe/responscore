package io.ajo.responscore.config;

import io.ajo.responscore.config.validation.annotation.ValidConfig;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Configuration which defines the schema, with typing and validation, for which the data of a
 * {@link io.ajo.responscore.form.Form} must adhere to
 */
@Data
@Builder
@ValidConfig
public class Config {

    /**
     * Set of unique configs for fields of type {@link Type#LOOKUP}
     */
    @Valid
    @NotNull
    @Builder.Default
    private final Set<LookupConfig> lookupConfigs = new HashSet<>();

    /**
     * Set of unique configs to describe composite types for fields of type {@link Type#COMPOSITE}
     */
    @Valid
    @NotNull
    @Builder.Default
    private final Set<CompositeTypeConfig> compositeTypeConfigs = new HashSet<>();

    /**
     * Ordered list of attributes which a {@link io.ajo.responscore.form.Form} using this config should adhere to
     */
    @Valid
    @NotNull
    @Size(min = 1)
    @Builder.Default
    private final Set<Attribute> attributes = new LinkedHashSet<>();

}
