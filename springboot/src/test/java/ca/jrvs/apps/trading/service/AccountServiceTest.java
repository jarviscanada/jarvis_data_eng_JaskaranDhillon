package ca.jrvs.apps.trading.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import ca.jrvs.apps.trading.model.Account;
import ca.jrvs.apps.trading.repository.AccountDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AccountServiceTest {

    @Mock
    private AccountDao accountDao;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void deleteAccountById_Failure_NonZeroBalance() {
        Account accountWithNonZeroBalance = new Account();
        accountWithNonZeroBalance.setAmount(100);
        accountWithNonZeroBalance.setTraderId(1);
        accountWithNonZeroBalance.setId(0);
        when(accountDao.getAccountByTraderId(1)).thenReturn(accountWithNonZeroBalance);

        try {
            accountService.deleteAccountByTraderId(1);
            fail("Expected IllegalArgumentException to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Balance not 0!", e.getMessage());
        }
    }

    @Test
    public void deleteAccountById_Success() {
        Account accountWithZeroBalance = new Account();
        accountWithZeroBalance.setAmount(0);
        accountWithZeroBalance.setTraderId(1);
        accountWithZeroBalance.setId(0);
        when(accountDao.getAccountByTraderId(1)).thenReturn(accountWithZeroBalance);

        accountService.deleteAccountByTraderId(1);

        verify(accountDao).deleteById(accountWithZeroBalance.getId());
    }
}
