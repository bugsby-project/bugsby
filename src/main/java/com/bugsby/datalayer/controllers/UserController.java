package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.filters.JwtRequestFilter;
import com.bugsby.datalayer.controllers.security.JwtUtils;
import com.bugsby.datalayer.controllers.security.UserDetailsServiceImpl;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.swagger.api.UsersApi;
import com.bugsby.datalayer.swagger.model.AuthenticationRequest;
import com.bugsby.datalayer.swagger.model.AuthenticationResponse;
import com.bugsby.datalayer.swagger.model.UserRequest;
import com.bugsby.datalayer.swagger.model.UserResponse;
import com.bugsby.datalayer.swagger.model.UsernameList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;
import java.util.function.Function;

@Controller
@CrossOrigin
public class UserController implements UsersApi {
    @Autowired
    private Service service;
    @Autowired
    @Qualifier(BeanIds.AUTHENTICATION_MANAGER)
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private Function<User, UserResponse> userResponseMapper;
    @Autowired
    private Function<UserRequest, User> userRequestMapper;

    @Override
    public ResponseEntity<UserResponse> createAccount(UserRequest body) {
        User user = userRequestMapper.apply(body);
        user = service.createAccount(user);
        UserResponse userResponse = userResponseMapper.apply(user);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<UserResponse> getUserByUsername(String authorization, String username) {
        User user = service.login(username);
        UserResponse userResponse = userResponseMapper.apply(user);
        return ResponseEntity.ok(userResponse);
    }

    @Override
    public ResponseEntity<UsernameList> getUsernames(String authorization) {
        UsernameList usernameList = new UsernameList().usernames(service.getAllUsernames());
        return ResponseEntity.ok(usernameList);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest body) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(body.getUsername());
        String jwt = JwtUtils.generateToken(userDetails);

        User lastUser = userDetailsService.getLastUser();
        UserResponse lastUserResponse = userResponseMapper.apply(lastUser);

        AuthenticationResponse response = new AuthenticationResponse()
                .jwt(jwt)
                .user(lastUserResponse);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
        configuration.setAllowedMethods(List.of("*"));
        configuration.addAllowedOrigin("*");

        http.cors()
                .configurationSource(request -> configuration)
                .and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/users", "/users/login")
                .permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
