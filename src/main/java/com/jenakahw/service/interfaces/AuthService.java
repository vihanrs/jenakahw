package com.jenakahw.service.interfaces;

import com.jenakahw.domain.User;

public interface AuthService {
	
	User getLoggedUser();
	
    String getLoggedUserRole();
    
    boolean isLoggedUserHasRole(String roleName);
}
