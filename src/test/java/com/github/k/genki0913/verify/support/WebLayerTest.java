package com.github.k.genki0913.verify.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.annotation.AliasFor;

import com.github.k.genki0913.verify.config.AppValidationProperties;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebMvcTest
@EnableConfigurationProperties(AppValidationProperties.class)
public @interface WebLayerTest {

    /**
     * テスト対象のコントローラークラスを指定できるように、@WebMvcTest の value 属性と同期させる
     */
    @AliasFor(annotation = WebMvcTest.class, attribute = "value")
    Class<?>[] value() default {};
}
