package com.jenakahw.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Privilege;
import com.jenakahw.domain.Role;
import com.jenakahw.domain.User;
import com.jenakahw.service.interfaces.AuthService;
import com.jenakahw.service.interfaces.PrivilegeService;
import com.jenakahw.util.PrivilegeHelper;

@RestController
// add class level mapping /privilage
//@RequestMapping(value = "/privilege")
public class PrivilegeController {

	@Autowired 
	private PrivilegeService privilegeService;

	@Autowired
	private AuthService authService;
	
	@Autowired
    private PrivilegeHelper privilegeHelper;

	// get mapping for generate privilege UI
	@GetMapping(value = "/privileges")
	public ModelAndView getPrivilegeUI() {
		User loggedUser = authService.getLoggedUser();

		String userRole = "";

		for (Role role : loggedUser.getRoles()) {
			if (role.getName().equals("Admin")) {
				userRole = "Admin";
				break;
			} else if (role.getName().equals("Manager")) {
				userRole = "Manager";
				break;
			} else {
				userRole = role.getName();
			}
		}

		ModelAndView privilegeView = new ModelAndView();
		privilegeView.addObject("title", "Privilege  | Jenaka Hardware");
		privilegeView.addObject("logusername", loggedUser.getUsername());
		privilegeView.addObject("loguserrole", userRole);
		privilegeView.addObject("loguserphoto", loggedUser.getUserPhoto());
		privilegeView.setViewName("privilege.html");
		return privilegeView;
	}

	// get service mapping for get all privileges
	@GetMapping(value = "/privileges/findall", produces = "application/json")
	public List<Privilege> findAll() {
		return privilegeService.findAll();
	}

	// post mapping for save new privilege
	@PostMapping(value = "/privileges")
	public String savePrivilege(@RequestBody Privilege privilege) {
		return privilegeService.savePrivilege(privilege);
	}

	// post mapping for update privilege record
	@PutMapping(value = "/privileges")
	public String updatePrivilege(@RequestBody Privilege privilege) {
		return privilegeService.updatePrivilege(privilege);
	}

	// delete mapping for delete privilege record
	@DeleteMapping(value = "/privileges")
	public String deletePrivilege(@RequestBody Privilege privilege) {
		return privilegeService.deletePrivilege(privilege);
	}
	
	// Fetch privileges for the logged user and a specific module
    @GetMapping("privilege/byloggeduserandmodule/{modulename}")
    public HashMap<String, Boolean> getPrivilegeByLoggedUserAndModule(@PathVariable("modulename") String moduleName) {
        return privilegeHelper.getPrivilegeByUserAndModule(moduleName);
    }

}
