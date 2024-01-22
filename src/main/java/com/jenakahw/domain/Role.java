package com.jenakahw.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // applied as an entity class
@Table(name = "role") // map table
@Data //generate getters and setters
@NoArgsConstructor //generate default constructor
@AllArgsConstructor //generate all args constructor
public class Role {
	@Id //primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) //auto generated id - AI
	@Column(name = "id", unique = true) //map with database table column
	private Integer id;
	
	@Column(name = "name")
	@NotNull
	private String name;
}
