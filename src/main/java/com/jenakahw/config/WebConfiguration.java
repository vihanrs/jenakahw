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
			.requestMatchers("/dashboard/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/user/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/module/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/privileges/**").hasAnyAuthority("Admin","Manager")
			.requestMatchers("/privilege/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/product/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/supplier/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier")
			.requestMatchers("/purchaseorder/**").hasAnyAuthority("Admin","Manager","Store-Keeper")
			.requestMatchers("/grn/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier")
			.requestMatchers("/customer/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/stock/**").hasAnyAuthority("Admin","Manager","Store-Keeper","Cashier","Helper")
			.requestMatchers("/report/**").hasAnyAuthority("Admin","Manager","Store-Keeper")
			.requestMatchers("/supplierpayment/**").hasAnyAuthority("Admin","Manager","Cashier")
			.requestMatchers("/customerpayment/**").hasAnyAuthority("Admin","Manager","Cashier")
			.requestMatchers("/invoicepayment/**").hasAnyAuthority("Admin","Manager","Cashier")
			.requestMatchers("/dailyextraincome/**").hasAnyAuthority("Admin","Manager","Cashier")
			.requestMatchers("/dailyexpenses/**").hasAnyAuthority("Admin","Manager","Cashier")
			.requestMatchers("/reportpurchaseorderui/**").hasAnyAuthority("Admin","Manager")
			.requestMatchers("/reportgrnui/**").hasAnyAuthority("Admin","Manager")
			.requestMatchers("/reportsalesui/**").hasAnyAuthority("Admin","Manager")
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
