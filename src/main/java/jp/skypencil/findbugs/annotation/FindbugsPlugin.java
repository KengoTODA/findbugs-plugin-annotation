package jp.skypencil.findbugs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation to annotate package, to describe that your project is to build
 * Findbugs Plugin. Parameters will be used to generate {@code findbugs.xml}.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.PACKAGE })
public @interface FindbugsPlugin {
    /**
     * <p>
     * URL of website which is related with your Findbugs plugin.
     * </p>
     * 
     * <p>
     * e.g. {@code https://github.com/KengoTODA/findbugs-slf4j/}
     * </p>
     */
    String website();

    /**
     * <p>
     * Provider of this findbugs plugin.
     * </p>
     */
    String provider();

    /**
     * <p>
     * ID of your plugin. Package name will be used by default, you can use this
     * parameter to specify it explicitly.
     * </p>
     */
    String pluginid() default "";
}
