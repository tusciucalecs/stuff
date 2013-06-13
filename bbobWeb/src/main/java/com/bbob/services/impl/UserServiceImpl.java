package com.bbob.services.impl;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.
    security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.bbob.models.Account;
import com.bbob.services.AccountService;
import com.bbob.services.UserService;

@Repository
public final class UserServiceImpl implements UserService {

    @Autowired
    private AccountService accountService;

    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Account account = accountService.findAccountByUsername(username);
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account
                .getUser().getRole().name());
        authorities.add(grantedAuthority);
        User user = new User(username, account.getPassword(), authorities);
        return user;
    }

    public Authentication loadGrantedAuthorities(String username,
            Account account) {
        UsernamePasswordAuthenticationToken result;
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(account
                .getUser().getRole().name());
        authorities.add(grantedAuthority);
        User user = new User(username, account.getPassword(),
                authorities);
        result = new UsernamePasswordAuthenticationToken(user,
                account.getPassword(), authorities);
        result.setDetails(account);
        return result;
    }

    public Account getAccountIfUserIsAuthenticated() {
        Object authenticationDetails = SecurityContextHolder.getContext()
                .getAuthentication().getDetails();
        if (authenticationDetails instanceof Account) {
            Account account = (Account) authenticationDetails;
            return account;
        } else {
            String username = SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            Account account = accountService.findAccountByUsername(username);
            return account;
        }
    }

    public void resetAccount(String userName, Account account) {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        Object authenticationDetails = authentication.getDetails();
        if (authenticationDetails instanceof Account) {
            SecurityContextHolder.getContext().setAuthentication(
                    loadGrantedAuthorities(userName, account));
        }
    }
}
