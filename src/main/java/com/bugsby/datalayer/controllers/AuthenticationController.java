package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.dtos.UserDto;
import com.bugsby.datalayer.security.AuthenticationRequest;
import com.bugsby.datalayer.security.AuthenticationResponse;
import com.bugsby.datalayer.security.JwtRequestFilter;
import com.bugsby.datalayer.security.JwtUtils;
import com.bugsby.datalayer.security.SecurityConstants;
import com.bugsby.datalayer.security.UserDetailsServiceImpl;
import com.bugsby.datalayer.utils.UriMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin
@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    @Qualifier(BeanIds.AUTHENTICATION_MANAGER)
    private AuthenticationManager authenticationManager;

    private final UserDetailsServiceImpl service;

    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        service = context.getBean(UserDetailsServiceImpl.class);
    }

    @GetMapping(UriMapping.HELLO)
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    @PostMapping(UriMapping.LOGGING)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(SecurityConstants.AUTHENTICATION_FAILED_MESSAGE, HttpStatus.FORBIDDEN);
        }

        UserDetails userDetails = service.loadUserByUsername(request.getUsername());
        String jwt = JwtUtils.generateToken(userDetails);

        return new ResponseEntity<>(new AuthenticationResponse(jwt, UserDto.from(service.getLastUser())), HttpStatus.OK);
    }
}

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;

    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring.xml");
        userDetailsService = context.getBean(UserDetailsServiceImpl.class);
    }

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
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(UriMapping.CREATE_ACCOUNT, UriMapping.LOGGING)
                .permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(JwtRequestFilter.getInstance(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
