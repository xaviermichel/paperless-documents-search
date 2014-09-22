package fr.simple.edm.model.tmp;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "tmp_documents", type = "tmp_library", shards = 1, replicas = 0)
public class TmpEdmLibrary extends TmpEdmNode {

	private String description;

	public TmpEdmLibrary() {
		super(EdmNodeType.LIBRARY);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
