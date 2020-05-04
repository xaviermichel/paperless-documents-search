package fr.simple.edm.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Document(indexName = "category", type = "category")
public class EdmCategory implements Serializable {

	private String id;

	private String name;

	private String description;

	private String color;

	private String backgroundColor;
}
