package jp.skypencil.findbugs.annotation.processor;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

import jp.skypencil.findbugs.annotation.BugPattern;
import jp.skypencil.findbugs.annotation.Detector;
import jp.skypencil.findbugs.annotation.Speed;

@ParametersAreNonnullByDefault
class XmlDocumentGenerator {
    Document generate(Map<String, Detector> detectors,
            @Nullable FindbugsPluginInformation pluginInformation) throws ParserConfigurationException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = createRoot(document, pluginInformation);
        for (Map.Entry<String, Detector> qualifiedNameAndDetector : detectors
                .entrySet()) {
            String qualifiedName = qualifiedNameAndDetector.getKey();
            Detector detector = qualifiedNameAndDetector.getValue();
            createDetectorElement(document, root, qualifiedName, detector);
            createBugPatternElements(document, root, detector.value());
        }
        // TODO custom category
        return document;
    }

    private Element createRoot(Document document,
            @Nullable FindbugsPluginInformation pluginInformation) {
        Element root = document.createElement("FindbugsPlugin");
        document.appendChild(root);
        if (pluginInformation != null) {
            root.setAttribute("website", pluginInformation.getWebsite());
            root.setAttribute("provider", pluginInformation.getProvider());
            root.setAttribute("pluginid", pluginInformation.getPluginId());
        }
        return root;
    }

    @Nonnull
    private void createDetectorElement(Document document, Element root,
            String qualifiedName, Detector detector) {
        Element element = document.createElement("Detector");
        root.appendChild(element);
        String types = FluentIterable.of(detector.value())
                .transform(new BugPatternToType()).join(Joiner.on(","));
        element.setAttribute("class", qualifiedName);
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
    private void createBugPatternElements(Document document, Element root,
            BugPattern[] bugPatterns) {
        for (BugPattern bugPattern : bugPatterns) {
            Element element = document.createElement("BugPattern");
            root.appendChild(element);
            element.setAttribute("type", bugPattern.type());
            element.setAttribute("abbrev", bugPattern.abbrev());
            element.setAttribute("category", bugPattern.category());
        }
    }
}
