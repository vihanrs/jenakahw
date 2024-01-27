package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jenakahw.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{

	@Query(value = "Select r from Role r where r.name != 'Admin'")
	public List<Role> findAll();
}
