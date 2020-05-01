package fr.simple.edm.tika;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.simple.edm.DocumentIngestApplication;
import fr.simple.edm.domain.EdmDocumentFileDto;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DocumentIngestApplication.class)
@WebAppConfiguration
@ComponentScan(basePackages = { "fr.simple.edm" })
@ActiveProfiles("test")
public class EdmOcrDocExtractorTest {

	@Autowired
	EdmOcrDocExtractor edmOcrDocExtractor;

	private static EdmDocumentFileDto documentFromBinaryFile(Path path) throws IOException {
		EdmDocumentFileDto edmDocumentFileDto = new EdmDocumentFileDto();
		edmDocumentFileDto.setFileExtension(FilenameUtils.getExtension(path.toString()));
		edmDocumentFileDto.setBinaryFileContent(Files.readAllBytes(path));
		edmDocumentFileDto.setFileContentType(Files.probeContentType(path));
		return edmDocumentFileDto;
	}

	@Test
	public void textFileContentShouldBeRead() throws IOException, URISyntaxException {
		// given
		Path binaryPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-binaries/demo.txt").toURI());
		Path contentPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-contents/demo.txt.txt").toURI());
		EdmDocumentFileDto edmDocumentFileDto = documentFromBinaryFile(binaryPath);

		// when
		edmOcrDocExtractor.extractFileContent(edmDocumentFileDto);

		// then
		assertThat(edmDocumentFileDto.getFileContent()).isEqualTo(Files.readString(contentPath).trim());
	}

	@Test
	public void pdfFileContentShouldBeRead() throws IOException, URISyntaxException {
		// given
		Path binaryPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-binaries/demo.pdf").toURI());
		Path contentPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-contents/demo.pdf.txt").toURI());
		EdmDocumentFileDto edmDocumentFileDto = documentFromBinaryFile(binaryPath);

		// when
		edmOcrDocExtractor.extractFileContent(edmDocumentFileDto);

		// then
		assertThat(edmDocumentFileDto.getFileContent()).isEqualTo(Files.readString(contentPath).trim());
	}

	@Test
	public void docxFileContentShouldBeRead() throws IOException, URISyntaxException {
		// given
		Path binaryPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-binaries/demo.docx").toURI());
		Path contentPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-contents/demo.docx.txt").toURI());
		EdmDocumentFileDto edmDocumentFileDto = documentFromBinaryFile(binaryPath);

		// when
		edmOcrDocExtractor.extractFileContent(edmDocumentFileDto);

		// then
		assertThat(edmDocumentFileDto.getFileContent()).isEqualTo(Files.readString(contentPath).trim());
	}

	@Test // for OCR you need to install tesseract (see readme)
	public void imageFileContentShouldBeRead() throws IOException, URISyntaxException {
		// given
		Path binaryPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-binaries/demo.png").toURI());
		Path contentPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-contents/demo.png.txt").toURI());
		EdmDocumentFileDto edmDocumentFileDto = documentFromBinaryFile(binaryPath);

		// when
		edmOcrDocExtractor.extractFileContent(edmDocumentFileDto);

		// then
		assertThat(edmDocumentFileDto.getFileContent()).isEqualTo(Files.readString(contentPath).trim());
	}

	@Test
	public void documentAuthorShouldBeExtracted() throws IOException, URISyntaxException {
		// given
		Path binaryPath = Paths.get(EdmOcrDocExtractorTest.class.getResource("/documents-binaries/demo.docx").toURI());
		EdmDocumentFileDto edmDocumentFileDto = documentFromBinaryFile(binaryPath);

		// when
		edmOcrDocExtractor.extractFileContent(edmDocumentFileDto);

		// then
		assertThat(edmDocumentFileDto.getFileAuthor()).isEqualTo("Michel Xavier");
	}


}
