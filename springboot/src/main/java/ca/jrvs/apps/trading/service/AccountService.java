package ca.jrvs.apps.trading.service;

import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.repository.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    /**
     * Deletes the account if the balance is 0
     *
     * @param traderId cannot be null
     * @throws IllegalArgumentException if unable to delete
     */
    @Transactional
    public void deleteAccountByTraderId(Integer traderId) {
        Account account = accountDao.getAccountByTraderId(traderId);
        if (account.getAmount() != 0) {
            throw new IllegalArgumentException("Balance not 0!");
        }
        accountDao.deleteById(account.getId());
    }
}