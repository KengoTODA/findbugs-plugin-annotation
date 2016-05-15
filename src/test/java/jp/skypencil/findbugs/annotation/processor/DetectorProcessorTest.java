package jp.skypencil.findbugs.annotation.processor;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import java.nio.charset.StandardCharsets;

import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

import org.junit.Test;

import com.google.common.io.Resources;
import com.google.testing.compile.JavaFileObjects;

public class DetectorProcessorTest {
    @Test
    public void testProcessEmptyClass() throws Exception {
        assert_().about(javaSource())
                .that(JavaFileObjects
                        .forResource(Resources.getResource("EmptyClass.java")))
                .processedWith(new DetectorProcessor()).compilesWithoutError();
    }

    @Test
    public void testProcess() throws Exception {
        assert_().about(javaSource())
                .that(JavaFileObjects
                        .forResource(Resources.getResource("SampleDetector.java")))
                .processedWith(new DetectorProcessor()).compilesWithoutError().and()
                .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "", "findbugs.xml")
                .withStringContents(StandardCharsets.UTF_8, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FindbugsPlugin xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"findbugsplugin.xsd\"><Detector class=\"SampleDetector\" reports=\"SLF4J_LOGGER_SHOULD_BE_FINAL\"/><BugPattern abbrev=\"SLF4J\" category=\"CORRECTNESS\" type=\"SLF4J_LOGGER_SHOULD_BE_FINAL\"/></FindbugsPlugin>");
    }

    @Test
    public void testProcessWithAttribute() throws Exception {
        assert_().about(javaSource())
                .that(JavaFileObjects
                        .forResource(Resources.getResource("SampleDetector.java")))
                .processedWith(new DetectorProcessor()).compilesWithoutError().and()
                .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "", "findbugs.xml")
                .withStringContents(StandardCharsets.UTF_8, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FindbugsPlugin xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"findbugsplugin.xsd\"><Detector class=\"SampleDetector\" reports=\"SLF4J_LOGGER_SHOULD_BE_FINAL\"/><BugPattern abbrev=\"SLF4J\" category=\"CORRECTNESS\" type=\"SLF4J_LOGGER_SHOULD_BE_FINAL\"/></FindbugsPlugin>");
    }

    @Test
    public void testProcessWithWarning() throws Exception {
        JavaFileObject source = JavaFileObjects
                .forResource(Resources.getResource("SampleDetectorWithoutReports.java"));
        assert_().about(javaSource())
                .that(source)
                .processedWith(new DetectorProcessor()).failsToCompile()
                .withErrorContaining("No @BugPattern found").in(source).onLine(12);
    }

    @Test
    public void testProcessWithPluginInfo() {
        assert_().about(javaSource())
        .that(JavaFileObjects
                .forResource(Resources.getResource("jp/skypencil/test/package-info.java")))
        .processedWith(new DetectorProcessor()).compilesWithoutError().and()
        .generatesFileNamed(StandardLocation.CLASS_OUTPUT, "", "findbugs.xml")
        .withStringContents(StandardCharsets.UTF_8, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FindbugsPlugin xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" pluginid=\"jp.skypencil.test\" provider=\"Kengo TODA\" website=\"https://github.com/KengoTODA/findbugs-plugin-annotation\" xsi:noNamespaceSchemaLocation=\"findbugsplugin.xsd\"/>");
    }
}
