package fr.simple.edm.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@Document(indexName = "documents", type = "category")
public class EdmCategory extends EdmNode implements Serializable {

    private String description;

    public EdmCategory() {
        super(EdmNodeType.CATEGORY);
    }
}
