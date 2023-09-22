package io.ajo.responscore.config;

import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum Type {

    // Primitive Type
    STRING(new TypeReference<String>() {}),
    DATE(new TypeReference<Date>() {}),
    LOOKUP(new TypeReference<String>() {}),
    DECIMAL(new TypeReference<BigDecimal>() {}),
    INTEGER(new TypeReference<BigInteger>() {}),
    BOOLEAN(new TypeReference<Boolean>() {}),

    // Composite Base Type
    COMPOSITE(new TypeReference<Map<String, Object>>() {}),

    // Extended Types
    URL(STRING),
    PERCENTAGE(DECIMAL);

    private static final Map<Class<?>, Type> javaTypeMapping = new HashMap<>();
    static {
        // STRING maps
        javaTypeMapping.put(String.class, STRING);
        javaTypeMapping.put(Enum.class, STRING);
        // DATE maps
        javaTypeMapping.put(Date.class, DATE);
        // DECIMAL maps
        javaTypeMapping.put(BigDecimal.class, DECIMAL);
        javaTypeMapping.put(Double.class, DECIMAL);
        // INTEGER maps
        javaTypeMapping.put(BigInteger.class, INTEGER);
        javaTypeMapping.put(Integer.class, INTEGER);
        // BOOLEAN maps
        javaTypeMapping.put(Boolean.class, BOOLEAN);;
        // COMPOSITE maps
        javaTypeMapping.put(Map.class, COMPOSITE);
    }

    private final TypeReference<?> typeReference;
    private final Type parent;

    Type(TypeReference<?> typeReference) {
        this.typeReference = typeReference;
        this.parent = null;
    }

    Type(Type parent) {
        this.typeReference = parent.typeReference;
        this.parent = parent;
    }

    /**
     * Checks to see if {@link this} extends the provided type
     * @param type parent type to compare extension
     * @return {@literal true} if {@link this} extends given type, or {@literal false}
     */
    public boolean extendsType(Type type) {
        Type loopType = this;
        while (loopType != null) {
            if (loopType == type) {
                return true;
            }
            loopType = loopType.parent;
        }
        return false;
    }

    public static Optional<Type> getTypeForJavaType(Class<?> clz) {
        return Optional.ofNullable(javaTypeMapping.get(clz));
    }

}
