package com.tgac.functional.reflection;
import lombok.Getter;

import java.lang.reflect.Type;
/**
 * @author TGa
 */

// see: https://gafter.blogspot.com/2006/12/super-type-tokens.html
public abstract class TypeRef<T> {
    @Getter
    Type type;

    protected TypeRef() {
        type = Types.getParametrizedType(getClass())
                .map(t -> t.getActualTypeArguments()[0])
                .getOrElseThrow(() -> new IllegalArgumentException("Generic with no explicit type"));
    }
}
