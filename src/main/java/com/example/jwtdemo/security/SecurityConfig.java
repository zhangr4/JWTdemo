package com.example.jwtdemo.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //UserDetailsService is an interface, which once implemented, can find the user by username;
    private final UserDetailsService userDetailsService;

    //to encoder the password
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        /*AuthenticationManagerBuilder is a class to allow different authentication method
        use userDetailsService() to all user-defined the implementation of storage of user
         */
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        //disable csrf() and make it stateless for json
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(STATELESS);
        //allow any request
        http.authorizeRequests().anyRequest().permitAll();

        //add filter for authentication
        http.addFilter(null);




    }
}
