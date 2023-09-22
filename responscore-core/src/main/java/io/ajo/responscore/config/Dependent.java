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

    @NotBlank
    private String attributeCode;

    @NotNull
    @Size(min = 1)
    @Builder.Default
    private Set<Object> values = new HashSet<>();

}
