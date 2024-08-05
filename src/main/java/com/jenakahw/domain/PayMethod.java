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

@Entity //define as a entity class
@Table(name = "paymethod") //map to table
@Data // generate getters and setters
@NoArgsConstructor // generate no args constructor
@AllArgsConstructor // generate all args constructor
public class PayMethod {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private Integer id;

	@Column(name = "name")
	@NotNull
	private String name;
}