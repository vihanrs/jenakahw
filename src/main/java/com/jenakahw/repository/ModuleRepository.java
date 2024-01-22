package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer>{
	
	//get modules list by given role without set privileges
	@Query("select m from Module m where m.id not in (select p.module.id from Privilege p where p.role.id=?1)")
	public List<Module> getModulesByRoleWithoutPrivileges(Integer roleId);

}
