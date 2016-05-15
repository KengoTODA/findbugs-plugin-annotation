package jp.skypencil.findbugs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.common.base.Function;

/**
 * <p>An annotation to describe bug pattern reported by {@link Detector}.</p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface BugPattern {
    /**
     * <p>Type of this BugPattern. Generally formatted in UPPER_SNAKE_CASE.</p>
     */
    String type();

    /**
     * <p>Abbreviation of type of this BugPattern.</p>
     */
    String abbrev();

    /**
     * <p>Category of bug pattern. Basically this value is one of {@link PredefinedCategory} but custom category is also supported.</p>
     * 
     * @see PredefinedCategory
     */
    String category();

    boolean experimental() default false;

    static class ToXmlElement implements Function<BugPattern, String> {
        @Override
        public String apply(BugPattern input) {
            return String.format(
                    "<BugPattern type=\"%s\" abbrev=\"%s\" category=\"%s\"/>",
                    input.type(), input.abbrev(), input.category());
        }
    }

    static class ToType implements Function<BugPattern, String> {
        @Override
        public String apply(BugPattern input) {
            return input.type();
        }
    }
}
