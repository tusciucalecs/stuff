package com.bbob.services.impl;

import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.bbob.models.Account;
import com.bbob.models.Role;
import com.bbob.services.AccountService;
import com.bbob.util.PasswordUtil;

@Repository
public final class AccountServiceImpl extends RepositoryServiceImpl<Account>
        implements AccountService {

    private static final Logger LOGGER = Logger
            .getLogger(AccountServiceImpl.class);

    public void createAccount(Account account) throws Exception {
        account.getUser().setRole(Role.ROLE_USER);
        String rawPassword = account.getPassword();
        account.setPassword(PasswordUtil.encodePassword(rawPassword));

        create(account);

        LOGGER.info("Created new account with id: " + account.getId());
    }

    public Account findAccountByUsername(String username) {
        Account accountAE = findAccountByField("username", username);
        if (accountAE != null) {
            return accountAE;
        }
        return null;
    }

    private Account findAccountByField(String filedName, String email) {
        try {
            List<Account> accounts = findBy(Account.class, filedName, email);
            if (CollectionUtils.isNotEmpty(accounts)) {
                return accounts.get(0);
            }
        } catch (Exception e) {
            LOGGER.error("Get account exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public void changePassword(Account account, String newPassword,
            Locale locale) throws Exception {
        account.setPassword(newPassword);
        update(account);

        LOGGER.info("Updated password for account with id: " + account.getId());
    }
}
