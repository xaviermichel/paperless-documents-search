package fr.simple.edm.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EdmCategoryDto implements Serializable {

	private String id;

	private String name;

	private String description;

	private String color;

	private String backgroundColor;
}
