package com.jenakahw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebConfiguration {

	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Bean //create an SecurityFilterChain instance at configuration
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.authorizeHttpRequests(auth -> {
			auth
			.requestMatchers("/resources/**").permitAll()
			.requestMatchers("/createadmin").permitAll()
			.requestMatchers("/login").permitAll()
			.requestMatchers("/error").permitAll()
			.requestMatchers("/dashboard/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier")
			.requestMatchers("/user/**").hasAnyAuthority("Admin","Manager")
			.requestMatchers("/privilege/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier")
//			.requestMatchers("/privilege/byloggeduserandmodule/**").permitAll()
			.requestMatchers("/product/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier")
			.requestMatchers("/supplier/**").hasAnyAuthority("Admin","Manager","Store-Keeper")
			.requestMatchers("/purchaseorder/**").hasAnyAuthority("Admin","Manager","Store-Keeper")
			.requestMatchers("/grn/**").hasAnyAuthority("Admin","Manager","Store-Keeper")
			.anyRequest().authenticated();
		})
		//login form detail
		.formLogin(login -> {
			login
			.loginPage("/login")
			.defaultSuccessUrl("/dashboard",true)
			.failureUrl("/login?error=usernamepassworderror")
			.usernameParameter("username")
			.passwordParameter("password");
		})
		//logout
		.logout(logout->{
			logout
			.logoutUrl("/logout")
			.logoutSuccessUrl("/login");
		})
		//exception
		.exceptionHandling(exception -> {
			exception.accessDeniedPage("/error");
		})
		.csrf(csrf -> {
			csrf.disable();
		});
		
		return http.build();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}
}
