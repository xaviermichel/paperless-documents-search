package fr.simple.edm.model.tmp;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "tmp_documents", type = "tmp_directory", shards = 1, replicas = 0)
public class TmpEdmDirectory extends TmpEdmNode {
	
	private String description;

	public TmpEdmDirectory() {
		super(EdmNodeType.DIRECTORY);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
