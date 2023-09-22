package io.ajo.responscore.config;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@EqualsAndHashCode(of = {"code"})
public class LookupItem {

    /**
     * Unique identifiable code which identifies this lookup item. This is used when selecting the value of the lookup
     * in the {@link io.ajo.responscore.form.Form}
     */
    @NotBlank
    private String code;
    /**
     * Human-readable label for this lookup item, this is displayed to the end user as a value for this lookup item
     */
    @NotBlank
    private String label;

}
