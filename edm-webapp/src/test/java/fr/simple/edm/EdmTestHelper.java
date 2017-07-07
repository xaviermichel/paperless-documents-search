package fr.simple.edm;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.service.EdmDocumentService;
import lombok.Getter;

@Component
@Getter
public class EdmTestHelper {

    @Autowired
    private ElasticsearchTestingHelper elasticsearchTestingHelper;

    @Autowired
    private EdmDocumentService edmDocumentService;

    private EdmDocumentFile docBac;
    private EdmDocumentFile docBrevet;
    private EdmDocumentFile docBacNotes;
    private EdmDocumentFile docBulletinSalaire;
    private EdmDocumentFile docLatex;

    public void destroyAndRebuildElasticContent() throws Exception {
        elasticsearchTestingHelper.deleteAllDocumentsForIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);

        docBac = new EdmDocumentFile();
        docBac.setName("Diplome du bac");
        docBac.setNodePath("/documents/1");

        docBrevet = new EdmDocumentFile();
        docBrevet.setName("Brevet");
        docBrevet.setNodePath("/documents/2");

        docBacNotes = new EdmDocumentFile();
        docBacNotes.setName("Notes du bac");
        docBacNotes.setNodePath("/documents/3");

        docBulletinSalaire = new EdmDocumentFile();
        docBulletinSalaire.setName("Bulletin de paye");
        docBulletinSalaire.setNodePath("/salaire/02.pdf");
        docBulletinSalaire.setFileContentType("application/pdf");
        docBulletinSalaire.setFileExtension("pdf");

        docLatex = new EdmDocumentFile();
        docLatex.setName("Un template de document");
        docLatex.setFileContent(Files.readAllBytes(Paths.get(this.getClass().getResource("/documents/demo_1/demo_pdf.pdf").toURI())));
        docLatex.setFileContentType("application/pdf");
        docLatex.setFileExtension("pdf");
        docLatex.setNodePath("/documents/4");

        docBac = edmDocumentService.save(docBac);
        docBrevet = edmDocumentService.save(docBrevet);
        docBacNotes = edmDocumentService.save(docBacNotes);
        docBulletinSalaire = edmDocumentService.save(docBulletinSalaire);
        docLatex = edmDocumentService.save(docLatex);

        elasticsearchTestingHelper.flushIndex(ElasticsearchTestingHelper.ES_INDEX_DOCUMENTS);
    }

}
