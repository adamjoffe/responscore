package io.ajo.responscore.form;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Form {

    @NotNull
    @Builder.Default
    private Map<String, Object> data = new HashMap<>();

}
