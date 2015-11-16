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
@Document(indexName = "documents", type = "source")
public class EdmSource extends EdmNode implements Serializable {

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
