package jp.skypencil.findbugs.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.google.auto.service.AutoService;
import com.google.common.collect.FluentIterable;

import jp.skypencil.findbugs.annotation.Detector;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("jp.skypencil.findbugs.annotation.*")
public class DetectorProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return true;
        }
        FluentIterable<TypeElement> typeElements = FluentIterable
                .from(roundEnv.getElementsAnnotatedWith(Detector.class))
                .filter(TypeElement.class);
        try {
            FileObject xmlFile = processingEnv.getFiler()
                    .createResource(StandardLocation.CLASS_OUTPUT, "", "findbugs.xml");
            try (Writer writer = xmlFile.openWriter()) {
                Document document = new XmlDocumentGenerator().generate(typeElements, processingEnv.getMessager());
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Transformer transformer = tFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
            }
        } catch (IOException | TransformerException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
