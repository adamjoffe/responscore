package io.ajo.responscore.config;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Dependent {

    /**
     * Reference to the dependee {@link Attribute#getCode} which is the dependent attribute
     */
    @NotBlank
    private String attributeCode;

    /**
     * List of values for the dependee attribute which would make this dependent attribute "active"
     */
    @NotNull
    @Size(min = 1)
    @Builder.Default
    private Set<Object> values = new HashSet<>();

}
