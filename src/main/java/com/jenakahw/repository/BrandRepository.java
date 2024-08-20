package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Integer>{
	
	// query for get brand by name
	@Query(value = "Select b from Brand b where b.name=?1")
	public Brand findBrandByName(String name); 

}
