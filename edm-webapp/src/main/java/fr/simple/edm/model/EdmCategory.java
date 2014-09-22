package fr.simple.edm.model;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "documents", type = "category", shards = 1, replicas = 0)
public class EdmCategory extends EdmNode {

	private String description;

	public EdmCategory() {
		super(EdmNodeType.CATEGORY);
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
