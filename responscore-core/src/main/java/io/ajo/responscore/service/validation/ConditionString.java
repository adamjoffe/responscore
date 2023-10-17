package io.ajo.responscore.service.validation;

import io.ajo.responscore.config.Validator;

public class ConditionString {

    public static String format(Validator validator) {
            switch (validator.getType()) {
                case Min -> {
                    return " (>=" + validator.getValue().toString() + ")";
                }
                case Max -> {
                    return " (<=" + validator.getValue().toString() + ")";
                }
                case GreaterThan -> {
                    return " (>" + validator.getValue().toString() + ")";
                }
                case LessThan -> {
                    return " (<" + validator.getValue().toString() + ")";
                }
                case MinSize -> {
                    return " (MinSize=" + validator.getValue().toString() + ")";
                }
                case MaxSize -> {
                    return " (MaxSize=" + validator.getValue().toString() + ")";
                }
                default -> {
                    return "";
                }
        }
    }
}
