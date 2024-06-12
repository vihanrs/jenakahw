package com.jenakahw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jenakahw.domain.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Integer> {

	// create query for get privilege by username and module name
	@Query(value = "SELECT bit_or(p.sel) as sel, bit_or(p.inst) as inst, bit_or(p.upd) as upd, bit_or(p.del) as del FROM jenakahw.privilege as p WHERE p.role_id in (SELECT uhr.role_id FROM jenakahw.user_has_role as uhr WHERE uhr.user_id = (SELECT u.id FROM jenakahw.user as u WHERE u.username = ?1)) AND p.module_id = (SELECT m.id FROM jenakahw.module as m WHERE m.name = ?2);", nativeQuery = true)
	public String getPrivilegesByUserAndModule(String username, String module);

	// create query for get privilege object by given role id and module id
	@Query(value = "SELECT p  FROM Privilege p WHERE p.role.id = ?1 and p.module.id = ?2")
	public Privilege getByRoleModule(Integer roleId, Integer moduleId);
}
