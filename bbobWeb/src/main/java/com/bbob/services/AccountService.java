package com.bbob.services;

import java.util.Locale;

import com.bbob.models.Account;

public interface AccountService extends RepositoryService<Account> {

    void createAccount(Account account) throws Exception;

    Account findAccountByUsername(String username);

    void changePassword(Account account, String newPassword, Locale locale)
            throws Exception;
}
