package com.jenakahw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jenakahw.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	
	// query for get all users except admin
	@Query(value = "Select u from User u where u.username != 'admin' order by u.id desc")
	public List<User> findAll();

	// query for generate employee id
	@Query(value= "Select concat('EMP', lpad (substring(max(u.emp_id),4 )+1, 3, '0')) FROM user as u", nativeQuery = true)
    public String generateNextEmpId();
	
	// query for get user by given user name
	@Query(value = "Select u from User u where u.username = ?1")
	public User getUserByUsername(String username);

	// query for get user by given contact
	@Query(value = "Select u from User u where u.contact = ?1")
	public User getUserByContact(String contact);

	// query for get user by given nic
	@Query(value = "Select u from User u where u.nic = ?1")
	public User getUserByNIC(String nic);

	// query for get user by given email
	@Query(value = "Select u from User u where u.email = ?1")
	public User getUserByEmail(String email);
	
	
}
