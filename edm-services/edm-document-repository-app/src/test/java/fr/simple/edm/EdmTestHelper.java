package fr.simple.edm;

import java.nio.file.Files;
import java.nio.file.Paths;

import fr.simple.edm.domain.EdmCategory;
import fr.simple.edm.domain.EdmDocumentFile;
import fr.simple.edm.service.EdmCategoryService;
import fr.simple.edm.service.EdmDocumentService;
import lombok.Getter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class EdmTestHelper {

	@Autowired
	private ElasticsearchTestingHelper elasticsearchTestingHelper;

	@Autowired
	private EdmDocumentService edmDocumentService;

	@Autowired
	private EdmCategoryService edmCategoryService;

	private EdmCategory category1;

	private EdmCategory category2;

	private EdmDocumentFile docBac;

	private EdmDocumentFile docBrevet;

	private EdmDocumentFile docBacNotes;

	private EdmDocumentFile docBulletinSalaire;

	private EdmDocumentFile docLatex;

	private EdmDocumentFile docForOcr;

	private EdmDocumentFile docSomeBill;

	public void destroyAndRebuildElasticContent() throws Exception {
		elasticsearchTestingHelper.deleteAllDocuments();

		category1 = EdmCategory.builder().name("category 1").build();
		category1 = edmCategoryService.save(category1);

		category2 = EdmCategory.builder().name("category 2").build();
		category2 = edmCategoryService.save(category2);

		docBac = new EdmDocumentFile();
		docBac.setName("Diplome du bac");
		docBac.setNodePath("/documents/1");
		docBac.setCategoryId(category1.getId());

		docBrevet = new EdmDocumentFile();
		docBrevet.setName("Brevet");
		docBrevet.setNodePath("/documents/2");
		docBrevet.setCategoryId(category1.getId());

		docBacNotes = new EdmDocumentFile();
		docBacNotes.setName("Notes du bac");
		docBacNotes.setNodePath("/documents/3");
		docBacNotes.setCategoryId(category1.getId());

		docBulletinSalaire = new EdmDocumentFile();
		docBulletinSalaire.setName("Bulletin de paye");
		docBulletinSalaire.setNodePath("/salaire/02.pdf");
		docBulletinSalaire.setFileContentType("application/pdf");
		docBulletinSalaire.setFileExtension("pdf");
		docBulletinSalaire.setCategoryId(category2.getId());

		docLatex = new EdmDocumentFile();
		docLatex.setName("Un template de document");
		docLatex.setFileContent("Demo utilisation latex pour la rédaction de documents");
		docLatex.setFileAuthor("titouan");
		docLatex.setFileContentType("application/pdf");
		docLatex.setFileExtension("pdf");
		docLatex.setNodePath("/documents/4");

		docForOcr = new EdmDocumentFile();
		docForOcr.setFileContent("bonjour");
		docForOcr.setFileContentType("application/png");
		docForOcr.setFileExtension("png");
		docForOcr.setNodePath("/documents/demo.png");

		docSomeBill = new EdmDocumentFile();
		docSomeBill.setFileContent("facture de téléphone");
		docSomeBill.setFileContentType("application/pdf");
		docSomeBill.setFileExtension("pdf");
		docSomeBill.setNodePath("/documents/demo.pdf");

		docBac = edmDocumentService.save(docBac);
		docBrevet = edmDocumentService.save(docBrevet);
		docBacNotes = edmDocumentService.save(docBacNotes);
		docBulletinSalaire = edmDocumentService.save(docBulletinSalaire);
		docLatex = edmDocumentService.save(docLatex);
		docForOcr = edmDocumentService.save(docForOcr);
		docSomeBill = edmDocumentService.save(docSomeBill);

		elasticsearchTestingHelper.flushIndexes();
	}

}
