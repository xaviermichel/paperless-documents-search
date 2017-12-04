package fr.simple.edm.tika;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.external.ExternalParser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.WriteOutContentHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;

// please also take a look at https://github.com/dadoonet/fscrawler/blob/master/src/main/java/fr/pilato/elasticsearch/crawler/fs/tika/TikaInstance.java

@Slf4j
@Component
@ConfigurationProperties(prefix = "edm.tika")
public class TikaInstance {

    @NotNull
    @Getter
    @Setter
    private String ocrLanguage;

    @NotNull
    @Getter
    @Setter
    private Boolean ocrPdf;

    @NotNull
    @Getter
    @Setter
    private Integer ocrIndexedChar;

    private Parser standardParser;
    private Parser ocrParser;
    private ParseContext standardContext;
    private ParseContext ocrContext;

    @PostConstruct
    private void initTika() {
        initParser();
        initContext();
    }

    private void initParser() {
        if (standardParser == null) {
            PDFParser pdfParser = new PDFParser();
            DefaultParser defaultParser = new DefaultParser();

            if (ExternalParser.check("tesseract")) {
                pdfParser.setOcrStrategy("ocr_and_text");
            } else {
                log.debug("Tesseract is not installed, so won't run OCR.");
            }

            Parser standardParsers[] = new Parser[1];
            standardParsers[0] = defaultParser;
            standardParser = new AutoDetectParser(standardParsers);

            Parser ocrParsers[] = new Parser[2];
            ocrParsers[0] = defaultParser;
            ocrParsers[1] = pdfParser;
            ocrParser = new AutoDetectParser(ocrParsers);
        }

    }

    private void initContext() {
        if (standardContext == null) {
            standardContext = new ParseContext();
            standardContext.set(Parser.class, standardParser);

            ocrContext = new ParseContext();
            ocrContext.set(Parser.class, ocrParser);
            TesseractOCRConfig config = new TesseractOCRConfig();
            config.setLanguage(ocrLanguage);
            ocrContext.set(TesseractOCRConfig.class, config);
        }
    }

    public String extractFileContent(InputStream stream, Metadata metadata) throws IOException, TikaException {
        WriteOutContentHandler handler = new WriteOutContentHandler(ocrIndexedChar);
        try {
            if (!ocrPdf) {
                standardParser.parse(stream, new BodyContentHandler(handler), metadata, standardContext);
            } else {
                ocrParser.parse(stream, new BodyContentHandler(handler), metadata, ocrContext);
            }
        } catch (SAXException e) {
            if (!handler.isWriteLimitReached(e)) {
                // This should never happen with BodyContentHandler...
                throw new TikaException("Unexpected SAX processing failure", e);
            }
        } finally {
            stream.close();
        }
        return handler.toString();
    }

}
