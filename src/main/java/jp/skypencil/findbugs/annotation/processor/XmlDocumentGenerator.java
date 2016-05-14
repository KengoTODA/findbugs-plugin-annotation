package jp.skypencil.findbugs.annotation.processor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.auto.common.AnnotationMirrors;
import com.google.auto.common.MoreElements;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

import jp.skypencil.findbugs.annotation.BugPattern;
import jp.skypencil.findbugs.annotation.Detector;
import jp.skypencil.findbugs.annotation.Speed;

@ParametersAreNonnullByDefault
class XmlDocumentGenerator {
    Document generate(Iterable<TypeElement> typeElements, @Nullable FindbugsPluginInformation pluginInformation, Messager messager) throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = createRoot(document, pluginInformation);
        for (TypeElement typeElement : typeElements) {
            Detector detector = typeElement.getAnnotation(Detector.class);
            createDetectorElement(document, root, typeElement, detector);
            if (detector.value().length == 0) {
                warnNoReports(messager, typeElement);
            } else {
                createBugPatternElements(document, root, detector.value());
            }
        }
        // TODO custom category
        return document;
    }

    private Element createRoot(Document document, @Nullable FindbugsPluginInformation pluginInformation) {
        Element root = document.createElement("FindbugsPlugin");
        document.appendChild(root);
        if (pluginInformation != null) {
            root.setAttribute("website", pluginInformation.getWebsite());
            root.setAttribute("provider", pluginInformation.getProvider());
            root.setAttribute("pluginid", pluginInformation.getPluginId());
        }
        return root;
    }

    private static final String ERROR_NO_REPORTS = "No @BugPattern found";
    private void warnNoReports(Messager messager, TypeElement typeElement) {
        Optional<AnnotationMirror> mirror = MoreElements.getAnnotationMirror(typeElement, Detector.class);
        Optional<AnnotationValue> value =
                mirror.transform(new Function<AnnotationMirror, AnnotationValue>() {
                    @Override
                    public AnnotationValue apply(AnnotationMirror input) {
                        return AnnotationMirrors.getAnnotationValue(input, "value");
                    }
                });
        if (mirror.isPresent() && value.isPresent()) {
            messager.printMessage(Kind.ERROR, ERROR_NO_REPORTS, typeElement, mirror.get(), value.get());
        } else {
            messager.printMessage(Kind.ERROR, ERROR_NO_REPORTS, typeElement);
        }
    }

    @Nonnull
    private void createDetectorElement(Document document, Element root, TypeElement typeElement,
            Detector detector) {
        Element element = document.createElement("Detector");
        root.appendChild(element);
        String types = FluentIterable.of(detector.value())
                .transform(new BugPattern.ToType()).join(Joiner.on(","));
        element.setAttribute("class", typeElement.getQualifiedName().toString());
        if (!types.isEmpty()) {
            element.setAttribute("reports", types);
        }
        if (detector.disabled()) {
            element.setAttribute("disabled", "true");
        }
        Speed speed = detector.speed();
        if (speed != null && speed != Speed.DEFAULT) {
            element.setAttribute("speed", speed.name().toLowerCase());
        }
    }

    @Nonnull
    private void createBugPatternElements(Document document, Element root, BugPattern[] bugPatterns) {
        for (BugPattern bugPattern : bugPatterns) {
            Element element = document.createElement("BugPattern");
            root.appendChild(element);
            element.setAttribute("type", bugPattern.type());
            element.setAttribute("abbrev", bugPattern.abbrev());
            element.setAttribute("category", bugPattern.category());
        }
    }
}
