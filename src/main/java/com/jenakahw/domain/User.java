package com.jenakahw.domain;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.validator.constraints.Length;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // applied as an entity class
@Table(name = "user") // map table
@Data //generate getters and setters
@NoArgsConstructor //generate default constructor
@AllArgsConstructor //generate all args constructor
public class User {
	@Id // primary key -PK
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto generated id - AI
	@Column(name = "id", unique = true) // map with database table column
	private Integer id;

	@Column(name = "user_id", unique = true)
	@NotNull
	@Length(max = 6)
	private String userId;

	@Column(name = "first_name")
	@NotNull
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "contact")
	@NotNull
	private String contact;

	@Column(name = "nic")
	@NotNull
	@Length(min = 10, max = 12)
	private String nic;

	@Column(name = "gender")
	@NotNull
	private String gender;

	@Column(name = "photo_url")
	private String photoURL;

	@Column(name = "username")
	@NotNull
	private String username;

	@Column(name = "password")
	@NotNull
	private String password;
	
	@Column(name = "email")
	@NotNull
	private String email;

	@Column(name = "isActive")
	@NotNull
	private Boolean isActive;

	@Column(name = "added_datetime")
	@NotNull
	private LocalDateTime addedDateTime;

	@ManyToOne // relationship format
	@JoinColumn(name = "user_status_id", referencedColumnName = "id") // join column condition
	private UserStatus userStatusId;
	
	@ManyToMany
	@JoinTable(name = "user_has_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles;
}
