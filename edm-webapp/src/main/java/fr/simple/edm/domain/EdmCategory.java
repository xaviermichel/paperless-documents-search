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
@Document(indexName = "documents", type = "category", shards = 1, replicas = 0)
public class EdmCategory extends EdmNode {

    private String description;

    public EdmCategory() {
        super(EdmNodeType.CATEGORY);
    }
}
