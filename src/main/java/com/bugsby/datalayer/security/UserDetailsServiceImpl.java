package com.bugsby.datalayer.security;

import com.bugsby.datalayer.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.Service;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class UserDetailsServiceImpl implements UserDetailsService {
    private final Service service;
    private com.bugsby.datalayer.model.User lastUser;

    public UserDetailsServiceImpl(Service service) {
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
