package com.jenakahw.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jenakahw.domain.User;
import com.jenakahw.repository.UserRepository;
import com.jenakahw.repository.UserStatusRepository;
import com.jenakahw.service.interfaces.UserService;
import com.jenakahw.util.PrivilegeHelper;

@Service
public class UserServiceImpl implements UserService {

	private static final String MODULE = "User";

	private final UserRepository userRepository; // Make it final for immutability
	private final UserStatusRepository userStatusRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final PrivilegeHelper privilegeHelper;

	// Constructor injection
	//@Autowired  optional (use if we have multiple constructures)
	public UserServiceImpl(UserRepository userRepository, UserStatusRepository userStatusRepository,
			BCryptPasswordEncoder bCryptPasswordEncoder, PrivilegeHelper privilegeHelper) {
		this.userRepository = userRepository;
		this.userStatusRepository = userStatusRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.privilegeHelper = privilegeHelper;
	}

	@Override
	public List<User> findAll() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			List<User> users = userRepository.findAll();
			for (User user : users) {
				user.setPassword(null);
			}
			return users;
		} else {
			return null;
		}
	}

	@Override
	public List<User> findAllUsers() {
		if (privilegeHelper.hasPrivilege(MODULE, "select")) {
			return userRepository.findAllUsers();
		} else {
			return null;
		}
	}

	@Override
	public User getLoggedUserDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String saveUser(User user) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "insert")) {
			return "Access Denied !!!";
		}

		// check duplicates...
		// check email
		User extUserByEmail = userRepository.getUserByEmail(user.getEmail());
		if (extUserByEmail != null) {
			return "Email " + user.getEmail() + " is already exist!";
		}
		// check contact
		User extUserByContact = userRepository.getUserByContact(user.getContact());
		if (extUserByContact != null) {
			return "Contact no " + user.getContact() + " is already exist!";
		}
		// check NIC
		User extUserByNIC = userRepository.getUserByNIC(user.getNic());
		if (extUserByNIC != null) {
			return "NIC " + user.getNic() + " is already exist!";
		}
		// check username
		User extUserByUsername = userRepository.getUserByUsername(user.getUsername());
		if (extUserByUsername != null) {
			return "Username " + user.getUsername() + " is already exist!";
		}

		try {
			// set auto generated employee id
			user.setEmpId(userRepository.generateNextEmpId());
			// set added date time
			user.setAddedDateTime(LocalDateTime.now());

			// encrypt password
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String updateUser(User user) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "update")) {
			return "Access Denied !!!";
		}

		// check existing
		User extUser = userRepository.getReferenceById(user.getId());
		if (extUser == null) {
			return "User not available";
		}

		// check duplicates
		// check email
		User extUserByEmail = userRepository.getUserByEmail(user.getEmail());
		if (extUserByEmail != null && user.getId() != extUserByEmail.getId()) {
			return "Email " + extUserByEmail.getEmail() + " is already exist!";
		}

		// check contact
		User extUserByContact = userRepository.getUserByContact(user.getContact());
		if (extUserByContact != null && user.getId() != extUserByContact.getId()) {
			return "Contact no " + user.getContact() + " is already exist!";
		}

		// check NIC
		User extUserByNIC = userRepository.getUserByNIC(user.getNic());
		if (extUserByNIC != null && user.getId() != extUserByNIC.getId()) {
			return "NIC " + user.getNic() + " is already exist!";
		}

		// check username
		User extUserByUsername = userRepository.getUserByUsername(user.getUsername());
		if (extUserByUsername != null && user.getId() != extUserByUsername.getId()) {
			return "Username " + extUserByUsername.getUsername() + " is already exist!";
		}

		try {
			if (user.getPassword() != null || !user.getPassword().equals("")) {
				// encrypt password
				user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			} else {
				// set password to new user object
				user.setPassword(extUser.getPassword());
			}
			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override
	public String deleteUser(User user) {
		// check privileges
		if (!privilegeHelper.hasPrivilege(MODULE, "delete")) {
			return "Access Denied !!!";
		}

		// need to check given user exist or not
		User extUser = userRepository.getReferenceById(user.getId());
		if (extUser == null) {
			return "Selected User Not Exist..!";
		}

		try {
			user.setUserStatusId(userStatusRepository.getReferenceById(2)); // set user status to 'Resigned'
			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@Override //update with password
	public String updateUserSettings(User user) {

		// check duplicates...

		// check username
		User extUserByUsername = userRepository.getUserByUsername(user.getUsername());
		if (extUserByUsername != null && user.getId() != extUserByUsername.getId()) {
			return "Username " + user.getUsername() + " is already exist!";
		}

		// check email
		User extUserByEmail = userRepository.getUserByEmail(user.getEmail());
		if (extUserByEmail != null && user.getId() != extUserByEmail.getId()) {
			return "Email " + extUserByEmail.getEmail() + " is already exist!";
		}

		try {
			// check password
			User extUserByPassword = userRepository.getReferenceById(user.getId());
			if (user.getPassword() != null) {
				if (bCryptPasswordEncoder.matches(user.getPassword(), extUserByPassword.getPassword())) {
					return "Cannot Update Same Password";
				} else {
					// encrypt password
					user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
				}
			} else {
				user.setPassword(extUserByPassword.getPassword());
			}

			userRepository.save(user);
			return "OK";
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
