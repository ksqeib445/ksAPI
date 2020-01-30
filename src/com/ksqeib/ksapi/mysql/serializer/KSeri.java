package com.ksqeib.ksapi.mysql.serializer;

import javax.annotation.Nonnull;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 序列化专用标签
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KSeri {
    /**
     * 不能为空
     *
     * @return 序列号时在数据库储存的id
     */
    @Nonnull
    String value();
    boolean base64=false;
}
