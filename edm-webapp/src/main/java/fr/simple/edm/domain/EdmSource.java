package fr.simple.edm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.Document;

import fr.simple.edm.common.EdmNodeType;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
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
