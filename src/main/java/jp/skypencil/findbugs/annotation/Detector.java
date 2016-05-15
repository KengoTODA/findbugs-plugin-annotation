package jp.skypencil.findbugs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation to annotate classes which provides Findbugs Detector. Generally
 * they are implementation of {@link edu.umd.cs.findbugs.Detector} or
 * {@link edu.umd.cs.findbugs.Detector2}.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface Detector {
    /**
     * <p>
     * List of {@link BugPattern} reported by the annotated detector. Should not
     * be empty.
     * </p>
     * 
     * @see BugPattern
     */
    BugPattern[] value();

    Speed speed() default Speed.DEFAULT;

    boolean disabled() default false;
}
