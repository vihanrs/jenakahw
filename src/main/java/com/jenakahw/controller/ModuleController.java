package com.jenakahw.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jenakahw.repository.ModuleRepository;
import com.jenakahw.domain.Module;
    
@RestController
@RequestMapping(value = "/module") // class level mapping
public class ModuleController {
	
	@Autowired  //inject DesignationDao object into variable
	private ModuleRepository moduleRepository;  //create designationDao object
	
	@GetMapping(value = "/findall", produces = "application/json")
	public List<Module> getAllModules(){
		return moduleRepository.findAll();
	}
	
	//get mapping for get module date by given role id - [/module/listbyrole?roleid=1]
	@GetMapping(value = "/listbyrole", params = {"roleid"}, produces = "application/json")
	public List<Module> getModulesByRoleWithoutPrivileges(@RequestParam("roleid") Integer roleId){
		System.out.println(roleId);
		return moduleRepository.getModulesByRoleWithoutPrivileges(roleId);
	}
}
