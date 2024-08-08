package com.jenakahw.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jenakahw.domain.Privilege;
import com.jenakahw.repository.PrivilegeRepository;

@RestController
// add class level mapping /privilage
//@RequestMapping(value = "/privilege")
public class PrivilegeController {

	/* Create Repository object ->
	 Dependency injection:Repository is an interface so it cannot create instance 
	 * then use dependency injection
	 */
	@Autowired
	private PrivilegeRepository privilegeRepository;
	
	private static final String MODULE = "Privilege";

	// get mapping for generate privilege UI
	@GetMapping(value = "/privileges")
	public ModelAndView getPrivilegeUI() {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		ModelAndView privilegeView = new ModelAndView();
		privilegeView.addObject("title", "Privilege  | Jenaka Hardware");
		privilegeView.addObject("logusername", auth.getName());
		privilegeView.setViewName("privilege.html");
		return privilegeView;
	}

	// get service mapping for get all privileges
	@GetMapping(value = "/privileges/findall", produces = "application/json")
	public List<Privilege> findAll() {
		return privilegeRepository.findAll(Sort.by(Direction.DESC, "id"));
	}

	// post mapping for save new privilege
	@PostMapping(value = "/privileges")
	public String savePrivilege(@RequestBody Privilege privilege) {

		// check privileges
		if (!hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// duplicate check
		Privilege extPrivilege = privilegeRepository.getByRoleModule(privilege.getRole().getId(),
				privilege.getModule().getId());
		if (extPrivilege != null) {
			return "Privilege already exist by given role and module";
		}

		try {
			privilegeRepository.save(privilege);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// post mapping for update privilege record
	@PutMapping(value = "/privileges")
	public String updatePrivilege(@RequestBody Privilege privilege) {

		// check privileges
		if (!hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check existing
		Privilege extPrivilege = privilegeRepository.getReferenceById(privilege.getId());
		if (extPrivilege == null) {
			return "Given privilege record not exist...!";
		}

		try {
			privilegeRepository.save(privilege);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// delete mapping for delete privilege record
	@DeleteMapping(value = "/privileges")
	public String deletePrivilege(@RequestBody Privilege privilege) {
		
		// check privileges
		if (!hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}
		// check existing
		Privilege extPrivilege = privilegeRepository.getReferenceById(privilege.getId());
		if (extPrivilege == null) {
			return "Given privilege record not exist...!";
		}
		try {
			// set auto generated values
			extPrivilege.setSel(false);
			extPrivilege.setInst(false);
			extPrivilege.setUpd(false);
			extPrivilege.setDel(false);
			privilegeRepository.save(extPrivilege);
			return "OK";

		} catch (Exception e) {
			return e.getMessage();
		}
	}

	// get mapping for get privileges by logged user and module
	@GetMapping(value = "privilege/byloggeduserandmodule/{modulename}", produces = "application/json")
	public HashMap<String, Boolean> getPrivilegeByLoggedUserAndModule(@PathVariable("modulename") String moduleName) {

		return getPrivilegeByUserAndModule(moduleName);
	}

	// method for get privileges by given user and module
	private HashMap<String, Boolean> getPrivilegeByUserAndModule(String module) {
		// get logged user authentication object
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		HashMap<String, Boolean> userPrivilege = new HashMap<String, Boolean>();

		if (auth.getName().equals("admin")) {
			userPrivilege.put("select", true);
			userPrivilege.put("insert", true);
			userPrivilege.put("update", true);
			userPrivilege.put("delete", true);
		} else {
			String userPrivileges = privilegeRepository.getPrivilegesByUserAndModule(auth.getName(), module);
			String[] privileges = userPrivileges.split(",");
			userPrivilege.put("select", privileges[0].equals("1"));
			userPrivilege.put("insert", privileges[1].equals("1"));
			userPrivilege.put("update", privileges[2].equals("1"));
			userPrivilege.put("delete", privileges[3].equals("1"));
		}
		return userPrivilege;
	}

	// method for check privileges for selected operation in selected module
	public Boolean hasPrivilege(String module, String operation) {
		HashMap<String, Boolean> logedUserPrivileges = getPrivilegeByUserAndModule(module);
		return logedUserPrivileges.get(operation);
	}

}
