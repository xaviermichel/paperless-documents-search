package fr.simple.edm.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.simple.edm.model.EdmCategory;
import fr.simple.edm.model.EdmDocumentFile;
import fr.simple.edm.model.EdmSource;
import fr.simple.edm.model.tmp.TmpEdmSource;
import fr.simple.edm.model.tmp.TmpEdmDocument;
import fr.simple.edm.model.tmp.TmpEdmCategory;
import fr.simple.edm.repository.EdmCategoryRepository;
import fr.simple.edm.repository.EdmDocumentRepository;
import fr.simple.edm.repository.EdmSourceRepository;
import fr.simple.edm.repository.tmp.TmpEdmSourceRepository;
import fr.simple.edm.repository.tmp.TmpEdmDocumentRepository;
import fr.simple.edm.repository.tmp.TmpEdmCategoryRepository;

@Controller
public class EdmAdministrationController {

    private final Logger logger = LoggerFactory.getLogger(EdmAdministrationController.class);
    
    @Inject
    private EdmCategoryRepository edmCategoryRepository;
    
    @Inject
    private TmpEdmCategoryRepository tmpEdmCategoryRepository;
    
    @Inject 
    private EdmSourceRepository edmSourceRepository;
    
    @Inject 
    private TmpEdmSourceRepository tmpEdmSourceRepository;
    
    @Inject
    private EdmDocumentRepository edmDocumentRepository;
    
    @Inject
    private TmpEdmDocumentRepository tmpEdmDocumentRepository;
    
    /**
     * Why migrate ? To update mapping. A better way with 0 downtime is describe here : http://www.elasticsearch.org/blog/changing-mapping-with-zero-downtime/ 
     * You may see message like : Caused by: org.elasticsearch.index.mapper.MergeMappingException: Merge failed with failures
     * 
     * How to use migration ?
     * 
     * 1. clear old tmp index.
     * 
     *     curl -XDELETE 'http://127.0.0.1:9253/tmp_documents' 
     * 
     * 2. restart simple-edm (will recreate an empty tmp_documents)
     * 
     * 3. copy documents to tmp index
     * 
     *     curl -XGET 'http://127.0.0.1:8053/admin/migration/main-to-tmp'
     * 
     * 4. tail logs to see complete message ;)
     * 
     * 5. drop main index
     * 
     *     curl -XDELETE 'http://127.0.0.1:9253/documents'
     * 
     * 6. restart simple-edm (will recreate index "documents" with the new mapping)
     * 
     * 7. restore data
     * 
     *     curl -XGET 'http://127.0.0.1:8053/admin/migration/tmp-to-main'
     * 
     * 8. That's all, folks !
     */
    
    @RequestMapping("/admin/migration/main-to-tmp")
    @ResponseBody
    String mainToTmp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                logger.info("main-to-tmp starts !");
                
                // copy lib
                for (EdmCategory edmLibrary : edmCategoryRepository.findAll()) {
                    TmpEdmCategory tmpEdmLibrary = new TmpEdmCategory();
                    BeanUtils.copyProperties(edmLibrary, tmpEdmLibrary);
                    tmpEdmCategoryRepository.save(tmpEdmLibrary);
                }
                
                // copy rep
                for (EdmSource edmDirectory : edmSourceRepository.findAll()) {
                    TmpEdmSource tmpEdmDirectory = new TmpEdmSource();
                    BeanUtils.copyProperties(edmDirectory, tmpEdmDirectory);
                    tmpEdmSourceRepository.save(tmpEdmDirectory);
                }
                
                // copy doc
                for (EdmDocumentFile edmDocument : edmDocumentRepository.findAll()) {
                    TmpEdmDocument tmpEdmDocument = new TmpEdmDocument();
                    BeanUtils.copyProperties(edmDocument, tmpEdmDocument);
                    tmpEdmDocumentRepository.save(tmpEdmDocument);
                }
                
                logger.info("main-to-tmp is over !");
            }
        }).start();
        return "Copying main data to tmp...";
    }

}
