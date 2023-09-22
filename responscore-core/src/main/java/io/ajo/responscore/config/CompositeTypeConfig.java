package io.ajo.responscore.config;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = {"code"})
public class CompositeTypeConfig {

    @NotBlank
    private String code;

    @Valid
    @NotNull
    @Size(min = 1)
    @Builder.Default
    private Set<Attribute> attributes = new HashSet<>();

}
