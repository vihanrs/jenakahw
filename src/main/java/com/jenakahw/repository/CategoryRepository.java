package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{

	// query for categorry by name
	@Query(value = "Select c from Category c where c.name =?1")
	public Category findByName(String name);
}
