package com.refinedmods.refinedstorage2.network.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface InjectNetworkSecurityComponent {
    String networkId() default "default";
}
