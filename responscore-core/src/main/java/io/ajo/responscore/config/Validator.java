package io.ajo.responscore.config;

import io.ajo.responscore.config.validation.annotation.ValidValidator;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Validator of which one or more can be attached to any {@link Attribute} to constrain the field
 */
@Data
@Builder
@ValidValidator
public class Validator {

    /**
     * Map of validators to validation function implementations to call during validation
     */
    private static final Map<ValidatorType, ValidatorFunction> VALIDATORS_MAP = new HashMap<>();
    static {
        VALIDATORS_MAP.put(ValidatorType.NotNull, (v, o) ->
                o != null
        );
        VALIDATORS_MAP.put(ValidatorType.NotBlank, (v, o) ->
                o instanceof CharSequence && !StringUtils.isBlank(((CharSequence) o).toString())
        );
        VALIDATORS_MAP.put(ValidatorType.Min, (v, o) ->
                o instanceof Number && v.value.compareTo(BigDecimal.valueOf(((Number) o).doubleValue())) <= 0
        );
        VALIDATORS_MAP.put(ValidatorType.Max, (v, o) ->
                o instanceof Number && v.value.compareTo(BigDecimal.valueOf(((Number) o).doubleValue())) >= 0
        );
        VALIDATORS_MAP.put(ValidatorType.GreaterThan, (v, o) ->
                o instanceof Number && v.value.compareTo(BigDecimal.valueOf(((Number) o).doubleValue())) < 0
        );
        VALIDATORS_MAP.put(ValidatorType.LessThan, (v, o) ->
                o instanceof Number && v.value.compareTo(BigDecimal.valueOf(((Number) o).doubleValue())) > 0
        );
        VALIDATORS_MAP.put(ValidatorType.NotEmpty, (v, o) ->
                !ObjectUtils.isEmpty(o)
        );
        VALIDATORS_MAP.put(ValidatorType.MinSize, (v, o) -> {
            if (o instanceof CharSequence) {
                return ((CharSequence) o).length() >= v.value.doubleValue();
            }
            if (o.getClass().isArray()) {
                return Array.getLength(o) >= v.value.doubleValue();
            }
            if (o instanceof Collection) {
                return ((Collection<?>) o).size() >= v.value.doubleValue();
            }
            if (o instanceof Map) {
                return ((Map<?, ?>) o).keySet().size() >= v.value.doubleValue();
            }
            return false;
        });
        VALIDATORS_MAP.put(ValidatorType.MaxSize, (v, o) -> {
            if (o instanceof CharSequence) {
                return ((CharSequence) o).length() <= v.value.doubleValue();
            }
            if (o.getClass().isArray()) {
                return Array.getLength(o) <= v.value.doubleValue();
            }
            if (o instanceof Collection) {
                return ((Collection<?>) o).size() <= v.value.doubleValue();
            }
            if (o instanceof Map) {
                return ((Map<?, ?>) o).keySet().size() <= v.value.doubleValue();
            }
            return false;
        });
    }

    @NotNull
    private ValidatorType type;

    /**
     * Used in validators requiring a comparison value
     * Only valid for {@link ValidatorType#Min}, {@link ValidatorType#Max}, {@link ValidatorType#GreaterThan},
     * {@link ValidatorType#LessThan}, {@link ValidatorType#MinSize} and {@link ValidatorType#MaxSize}
     */
    private BigDecimal value;

    /**
     * Used for specifying specific field that is within a {@link Type#COMPOSITE} type value. Can use dot-notation
     * Only valid for {@link Type#COMPOSITE}
     */
    private String field;

    public boolean validate(final Object data) {
        return VALIDATORS_MAP.get(type).validate(this, data);
    }

    @FunctionalInterface
    private interface ValidatorFunction {
        boolean validate(Validator validator, Object value);
    }

}
