package fr.simple.edm.model;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Document(indexName = "documents", type = "source", shards = 1, replicas = 0)
public class EdmSource extends EdmNode {
	
	private String description;

	public EdmSource() {
		super(EdmNodeType.SOURCE);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
