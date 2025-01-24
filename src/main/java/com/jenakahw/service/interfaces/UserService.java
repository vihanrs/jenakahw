package com.jenakahw.service.interfaces;

import java.util.List;

import com.jenakahw.domain.User;

public interface UserService {
	
	List<User> findAll();
	
	List<User> findAllUsers();
	
	User getLoggedUserDetails();
	
	String saveUser(User user);
	
    String updateUser(User user);
    
    String deleteUser(User user);
    
    String updateUserSettings(User user);

}
