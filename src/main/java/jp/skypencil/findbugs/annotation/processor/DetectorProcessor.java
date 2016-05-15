package jp.skypencil.findbugs.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import jp.skypencil.findbugs.annotation.Detector;
import jp.skypencil.findbugs.annotation.FindbugsPlugin;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("jp.skypencil.findbugs.annotation.*")
public class DetectorProcessor extends AbstractProcessor {
    private final Map<String, Detector> detectors = new HashMap<>();
    private FindbugsPluginInformation pluginInfo;

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        if (! roundEnv.processingOver()) {
            processAnnotations(roundEnv);
        } else {
            writeXmlFile();
        }

        return true;
    }

    private void processAnnotations(RoundEnvironment roundEnv) {
        processFindbugsPluginInformation(roundEnv);

        FluentIterable<TypeElement> typeElements = FluentIterable
                .from(roundEnv.getElementsAnnotatedWith(Detector.class))
                .filter(TypeElement.class);
        for (TypeElement typeElement : typeElements) {
            Detector detector = typeElement.getAnnotation(Detector.class);
            if (detector.value().length == 0) {
                warnNoReports(typeElement);
            } else {
                detectors.put(typeElement.getQualifiedName().toString(),
                        detector);
            }
        }
    }

    private void writeXmlFile() {
        if (pluginInfo == null) {
            warn("No @FindbugsPlugin found");
        }
        try {
            FileObject xmlFile = processingEnv.getFiler().createResource(
                    StandardLocation.CLASS_OUTPUT, "", "findbugs.xml");
            try (Writer writer = xmlFile.openWriter()) {
                Document document = new XmlDocumentGenerator().generate(
                        detectors, pluginInfo);
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
            }
        } catch (IOException | TransformerException
                | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private void processFindbugsPluginInformation(RoundEnvironment roundEnv) {
        PackageElementToPackageName elementToName = new PackageElementToPackageName();
        Collection<PackageElement> packages = FluentIterable
                .from(roundEnv.getElementsAnnotatedWith(FindbugsPlugin.class))
                .filter(PackageElement.class).toList();
        if (packages.isEmpty()) {
            return;
        }
        for (PackageElement packageElement : packages) {
            String packageName = elementToName.apply(packageElement);
            if (pluginInfo != null) {
                warn("Duplicated @FindbugsPlugin found at following package: "
                                + packageName);
            } else {
                pluginInfo = FindbugsPluginInformation.of(
                        packageElement.getAnnotation(FindbugsPlugin.class),
                        packageName);
            }
        }
    }

    private static final String ERROR_NO_REPORTS = "No @BugPattern found";

    private void warnNoReports(TypeElement typeElement) {
        Optional<AnnotationMirror> mirror = MoreElements
                .getAnnotationMirror(typeElement, Detector.class);
        Optional<AnnotationValue> value = mirror
                .transform(new Function<AnnotationMirror, AnnotationValue>() {
                    @Override
                    public AnnotationValue apply(AnnotationMirror input) {
                        return AnnotationMirrors.getAnnotationValue(input,
                                "value");
                    }
                });
        if (mirror.isPresent() && value.isPresent()) {
            error(ERROR_NO_REPORTS, typeElement, mirror.get(), value.get());
        } else {
            error(ERROR_NO_REPORTS, typeElement);
        }
    }

    private void warn(String msg) {
        processingEnv.getMessager().printMessage(Kind.WARNING, msg);
    }

    private void error(String msg, Element element) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg, element);
    }

    private void error(String msg, Element element, AnnotationMirror annotation,
            AnnotationValue value) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg, element,
                annotation, value);
    }

    private static class PackageElementToPackageName
            implements Function<PackageElement, String> {
        @Override
        public String apply(PackageElement packageElement) {
            return packageElement.getQualifiedName().toString();
        }
    }
}
