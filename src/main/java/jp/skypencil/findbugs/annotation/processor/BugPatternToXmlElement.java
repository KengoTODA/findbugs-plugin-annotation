package jp.skypencil.findbugs.annotation.processor;

import com.google.common.base.Function;

import jp.skypencil.findbugs.annotation.BugPattern;

class BugPatternToXmlElement implements Function<BugPattern, String> {
    @Override
    public String apply(BugPattern input) {
        return String.format(
                "<BugPattern type=\"%s\" abbrev=\"%s\" category=\"%s\"/>",
                input.type(), input.abbrev(), input.category());
    }
}