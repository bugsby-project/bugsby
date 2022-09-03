package com.bugsby.datalayer.controllers.security;

import com.bugsby.datalayer.service.Service;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final Service service;
    private com.bugsby.datalayer.model.User lastUser;

    public UserDetailsServiceImpl(@Autowired Service service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            lastUser = service.login(username);
            return new User(lastUser.getUsername(), lastUser.getPassword(), new ArrayList<>());
        } catch (UserNotFoundException e) {
            throw new UsernameNotFoundException(username);
        }
    }

    /**
     * Method for obtaining the last user that tried to authenticate using their username.
     * The purpose of this method is to minimise the calls to the service, which holds data about its users.
     *
     * @return the last {@code User} that tried to login
     */
    public com.bugsby.datalayer.model.User getLastUser() {
        return lastUser;
    }
}
