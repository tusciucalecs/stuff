package com.bbob.security;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.bbob.models.Account;
import com.bbob.services.AccountService;
import com.bbob.services.UserService;
import com.bbob.util.PasswordUtil;

public final class BbobAuthenticationProvider implements
        AuthenticationProvider {

    private static final Logger LOGGER = Logger
            .getLogger(BbobAuthenticationProvider.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = (String) authentication.getPrincipal();

        if (username == null || username.trim().equals("")) {
            LOGGER.warn("username is null or empty");
            throw new BadCredentialsException("Enter your username address.");
        }

        String decodedPassword = (String) authentication.getCredentials();
        if (StringUtils.equalsIgnoreCase("", decodedPassword)) {
            LOGGER.warn("Password is null or empty");
            throw new BadCredentialsException("Enter your password.");
        }

        String password = PasswordUtil.encodePassword(decodedPassword);

        Account account = accountService.findAccountByUsername(username);

        if (account != null && account.getPassword().equals(password)) {
            LOGGER.info("Logged with username: " + username);
            return userService.loadGrantedAuthorities(username, account);
        } else {
            throw new BadCredentialsException("Authentication failed. "
                    + "The username or password you entered is incorrect.");
        }
    }

    public boolean supports(Class<?> authentication) {
        return true;
    }
}
