package io.ajo.responscore.form;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Form {

    private Map<String, Object> data;

}
