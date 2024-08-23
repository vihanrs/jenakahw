package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.Module;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer>{
	
	//get modules list by given role without set privileges
	@Query(value = "select m from Module m where m.id not in (select p.module.id from Privilege p where p.role.id=?1)")
	public List<Module> getModulesByRoleWithoutPrivileges(Integer roleId);
	
	@Query(value = "select m.name from jenakahw.module as m where m.id in (select p.module_id "
			+ "from jenakahw.privilege as p where p.role_id in (Select uhr.role_id from jenakahw.user_has_role as uhr where uhr.user_id in ("
			+ "select u.id from jenakahw.user as u where u.username=?1)) and p.sel=1)",nativeQuery = true)
	public String[] getModulesByLogedUser(String username);

}
