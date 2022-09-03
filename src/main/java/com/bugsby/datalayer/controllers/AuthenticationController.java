package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.dtos.UserDto;
import com.bugsby.datalayer.controllers.dtos.requests.AuthenticationRequest;
import com.bugsby.datalayer.controllers.dtos.requests.AuthenticationResponse;
import com.bugsby.datalayer.controllers.security.JwtRequestFilter;
import com.bugsby.datalayer.controllers.security.JwtUtils;
import com.bugsby.datalayer.controllers.security.SecurityConstants;
import com.bugsby.datalayer.controllers.security.UserDetailsServiceImpl;
import com.bugsby.datalayer.controllers.utils.UriMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
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

    @Autowired
    private UserDetailsServiceImpl service;

    @GetMapping(UriMapping.HELLO)
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("Hello", HttpStatus.OK);
    }

    @PostMapping(UriMapping.LOGGING)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(SecurityConstants.AUTHENTICATION_FAILED_MESSAGE, HttpStatus.FORBIDDEN);
        }

        UserDetails userDetails = service.loadUserByUsername(request.username());
        String jwt = JwtUtils.generateToken(userDetails);

        return new ResponseEntity<>(new AuthenticationResponse(jwt, UserDto.from(service.getLastUser())), HttpStatus.OK);
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
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(UriMapping.CREATE_ACCOUNT, UriMapping.LOGGING)
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
