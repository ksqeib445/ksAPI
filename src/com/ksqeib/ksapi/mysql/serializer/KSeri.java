package com.ksqeib.ksapi.mysql.serializer;

import com.avaje.ebean.validation.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KSeri {
    @NotNull
    String value();
}
