package jp.skypencil.findbugs.annotation.processor;

import com.google.common.base.Function;

import jp.skypencil.findbugs.annotation.BugPattern;

class BugPatternToType implements Function<BugPattern, String> {
    @Override
    public String apply(BugPattern input) {
        return input.type();
    }
}