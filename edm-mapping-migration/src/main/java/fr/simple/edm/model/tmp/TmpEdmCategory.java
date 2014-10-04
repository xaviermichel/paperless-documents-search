package fr.simple.edm.model.tmp;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "tmp_documents", type = "tmp_category", shards = 1, replicas = 0)
public class TmpEdmCategory extends TmpEdmNode {

	private String description;

	public TmpEdmCategory() {
		super(EdmNodeType.CATEGORY);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
