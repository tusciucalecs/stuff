package com.bbob.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.bbob.models.Account;

public interface UserService extends UserDetailsService {

    Account getAccountIfUserIsAuthenticated();

    void resetAccount(String userName, Account account);

    Authentication loadGrantedAuthorities(String username, Account account);
}
