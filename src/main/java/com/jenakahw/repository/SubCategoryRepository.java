package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.SubCategory;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

	@Query(value = "Select sc from SubCategory sc where sc.categoryId.id =?1")
	public List<SubCategory> findByCategory(Integer categoryId);

	// query for categorry by name
	@Query(value = "Select sc from SubCategory sc where sc.name =?1")
	public SubCategory findByName(String name);

}
