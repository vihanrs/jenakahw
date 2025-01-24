package com.jenakahw.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jenakahw.domain.UserStatus;
import com.jenakahw.repository.UserStatusRepository;
import com.jenakahw.service.interfaces.UserStatusService;

@Service
public class UserStatusServiceImpl implements UserStatusService{
	// Make it final for immutability
	private final UserStatusRepository userStatusRepository;

	// Constructor injection
    public UserStatusServiceImpl(UserStatusRepository userStatusRepository) {
        this.userStatusRepository = userStatusRepository;
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }
}
