package jp.skypencil.findbugs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.common.base.Function;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
public @interface BugPattern {
    String type();

    String abbrev();

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
